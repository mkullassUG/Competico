package com.projteam.app.service;

import static com.projteam.app.domain.Account.PLAYER_ROLE;
import static java.util.Collections.synchronizedMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.projteam.app.dao.game.PlayerResultDAO;
import com.projteam.app.dao.game.GameResultDAO;
import com.projteam.app.dao.game.tasks.ChoiceWordFillDAO;
import com.projteam.app.dao.game.tasks.ChronologicalOrderDAO;
import com.projteam.app.dao.game.tasks.ListChoiceWordFillDAO;
import com.projteam.app.dao.game.tasks.ListSentenceFormingDAO;
import com.projteam.app.dao.game.tasks.ListWordFillDAO;
import com.projteam.app.dao.game.tasks.MultipleChoiceDAO;
import com.projteam.app.dao.game.tasks.SingleChoiceDAO;
import com.projteam.app.dao.game.tasks.WordConnectDAO;
import com.projteam.app.dao.game.tasks.WordFillDAO;
import com.projteam.app.domain.Account;
import com.projteam.app.domain.game.Game;
import com.projteam.app.domain.game.PlayerResult;
import com.projteam.app.domain.game.GameResult;
import com.projteam.app.domain.game.tasks.Task;
import com.projteam.app.domain.game.tasks.answers.TaskAnswer;
import com.projteam.app.dto.game.GameResultPersonalDTO;
import com.projteam.app.dto.game.GameResultTotalDTO;
import com.projteam.app.dto.game.GameResultTotalDuringGameDTO;
import com.projteam.app.dto.game.tasks.TaskInfoDTO;

@Service
public class GameService
{
	private AccountService accServ;
	private LobbyService lobbyServ;
	private PlayerResultDAO prDAO;
	private GameResultDAO grDAO;
	
	private GameTaskDataService gtdServ;
	
	private Map<String, Game> games;
	
	private static final long MAX_TIME_SINCE_LAST_INTERACTION_MILLI = 120000;
	private static final int HISTORY_PAGE_SIZE = 30;
	
	@Autowired
	public GameService(AccountService accServ, LobbyService lobbyServ,
			PlayerResultDAO grDAO, GameResultDAO grsDAO,
			GameTaskDataService gtdServ,
			
			ChoiceWordFillDAO cwfDao,
			ChronologicalOrderDAO coDao,
			ListChoiceWordFillDAO lcwfDao,
			ListSentenceFormingDAO lsfDao,
			ListWordFillDAO lwfDao,
			MultipleChoiceDAO mcDao,
			SingleChoiceDAO scDao,
			WordConnectDAO wcDao,
			WordFillDAO wfDao)
	{
		this.accServ = accServ;
		this.lobbyServ = lobbyServ;
		this.prDAO = grDAO;
		this.grDAO = grsDAO;
		
		this.gtdServ = gtdServ;
		
		games = syncMap();
	}
	
	public boolean createGameFromLobby(String gameCode)
	{
		return createGameFromLobby(gameCode, getAccount());
	}
	public boolean createGameFromLobby(String gameCode, Account requestSource)
	{
		if (!lobbyServ.lobbyExists(gameCode))
			return false;
		if (games.containsKey(gameCode))
			return false;
		if (!lobbyServ.isHost(gameCode, requestSource))
			return false;
		
		List<Account> players = lobbyServ.getPlayers(gameCode);
		List<Account> spectators = Optional.ofNullable(lobbyServ.getHost(gameCode))
				.filter(host -> !host.hasRole(PLAYER_ROLE))
				.stream()
				.collect(Collectors.toList());
		
		if (players.size() < 1)
			return false;
		if (!lobbyServ.deleteLobby(gameCode, requestSource))
			return false;
		
		int taskCount = 6 + (int) (Math.random() * 4); //TODO export to properties
		double targetDifficulty = 100; //TODO refactor
		Map<UUID, List<Task>> taskMap = new HashMap<>();
		taskMap.putAll(players.stream()
				.collect(Collectors.toMap(player -> player.getId(), player ->
					IntStream.range(0, taskCount)
							.mapToObj(i -> gtdServ
									.generateRandomTask(targetDifficulty))
							.collect(Collectors.toList()))));
		
		games.put(gameCode, new Game(players, spectators, taskCount, taskMap));
		return true;
	}

	public boolean gameExists(String gameCode)
	{
		return games.containsKey(gameCode);
	}
	
	public TaskInfoDTO getCurrentTaskInfo(String gameCode)
	{
		return getCurrentTaskInfo(gameCode, getAccount());
	}
	public TaskInfoDTO getCurrentTaskInfo(String gameCode, Account player)
	{
		return 
				Optional.ofNullable(getCurrentTask(gameCode, player))
					.map(task -> task.toDTO(getTaskNumber(gameCode, player), getTaskCount(gameCode)))
					.orElse(null);
	}
	
	private Task getCurrentTask(String gameCode)
	{
		return getCurrentTask(gameCode, getAccount());
	}
	private Task getCurrentTask(String gameCode, Account player)
	{
		if (!games.containsKey(gameCode))
			return null;
		
		Game game = games.get(gameCode);
		return game.getCurrentTask(player);
	}
	
	public boolean hasGameFinished(String gameCode)
	{
		return hasGameFinished(gameCode, getAccount());
	}
	public boolean hasGameFinished(String gameCode, Account player)
	{
		if (!games.containsKey(gameCode))
			return true;
		return games.get(gameCode).hasGameFinishedFor(player);
	}
	
	public boolean acceptAnswer(String gameCode, TaskAnswer answer)
	{
		return acceptAnswer(gameCode, answer, getAccount());
	}
	private boolean acceptAnswer(String gameCode, TaskAnswer answer, Account player)
	{
		if (!games.containsKey(gameCode))
			return false;
		
		Game game = games.get(gameCode);
		if (game.hasGameFinishedFor(player))
			return false;
		Task task = game.getCurrentTask(player);
		double completion = task.acceptAnswer(answer);
		game.advance(player, completion);
		
		checkIfGameFinished(gameCode, game);
		return true;
	}
	public Class<? extends TaskAnswer> getCurrentAnswerClass(String gameCode, Account player)
	{
		if (!games.containsKey(gameCode))
			return null;
		
		Game game = games.get(gameCode);
		if (game.hasGameFinishedFor(player))
			return null;
		
		return game.getCurrentTask(player).getAnswerType();
	}
	private void checkIfGameFinished(String gameCode, Game game)
	{
		if (game.hasGameFinished())
		{
			saveGameScores(game);
			games.remove(gameCode);
		}
	}
	private void saveGameScores(Game game)
	{
		GameResult grs = game.createGameResult();
		for (PlayerResult gr: grs.getResults().values())
			prDAO.save(gr);
		grDAO.save(grs);
	}

	private Account getAccount()
	{
		return accServ.getAuthenticatedAccount()
				.orElseThrow(() -> new IllegalArgumentException("Not authenticated."));
	}

	private TaskAnswer convertRawAnswer(String gameCode, JsonNode answer) throws JsonProcessingException
	{
		return new ObjectMapper().treeToValue(answer, getCurrentTask(gameCode).getAnswerType());
	}
	public void acceptAnswer(String gameCode, JsonNode answer) throws JsonProcessingException
	{
		acceptAnswer(gameCode, convertRawAnswer(gameCode, answer));
	}

	public int getTaskNumber(String gameCode)
	{
		return getTaskNumber(gameCode, getAccount());
	}
	public int getTaskNumber(String gameCode, Account player)
	{
		if (!games.containsKey(gameCode))
			return -1;
		Game game = games.get(gameCode);
		return game.getCurrentTaskNumber(player);
	}

	public int getTaskCount(String gameCode)
	{
		return getTaskCount(gameCode, getAccount());
	}
	public int getTaskCount(String gameCode, Account acc)
	{
		if (!games.containsKey(gameCode))
			return -1;
		Game game = games.get(gameCode);
		return game.getTaskCount(acc);
	}

	public Optional<List<GameResultTotalDTO>> getResults(UUID gameID)
	{
		return grDAO.findById(gameID)
				.map(grs ->
				{
					List<GameResultTotalDTO> ret = new ArrayList<>();
					for (PlayerResult gr: grs.getResults().values())
					{
						Account player = accServ.findByID(gr.getPlayerID())
								.orElse(null);
						var completion = gr.getCompletion();
						var difficulty = gr.getDifficulty();
						var timeTaken = gr.getTimeTaken();
						int l = Math.min(completion.size(),
								Math.min(difficulty.size(),
										timeTaken.size()));
						double score = 0;
						long totalTime = 0;
						for (int i = 0; i < l; i++)
						{
							long time = timeTaken.get(i);
							score += Game.calculateScore(
									completion.get(i),
									difficulty.get(i),
									time);
							totalTime += time;
						}
						ret.add(new GameResultTotalDTO(
								player.getUsername(),
								player.getNickname(),
								(long) score,
								totalTime));
					}
					return ret.stream()
							.sorted((r1, r2) -> -Long.compare(r1.getTotalScore(), r2.getTotalScore()))
							.collect(Collectors.toList());
				});
	}
	public Optional<List<GameResultTotalDuringGameDTO>> getCurrentResults(UUID gameID)
	{
		return games.values()
			.stream()
			.filter(game -> game.getID().equals(gameID))
			.findFirst()
			.map(game -> game.getCurrentResults());
	}
	public Optional<List<GameResultPersonalDTO>> getPersonalResults(UUID gameID)
	{
		return getPersonalResults(gameID, getAccount());
	}
	public Optional<List<GameResultPersonalDTO>> getPersonalResults(UUID gameID, Account player)
	{
		Optional<List<GameResultPersonalDTO>> ret = games.values()
				.stream()
				.filter(game -> game.getID().equals(gameID))
				.findFirst()
				.map(game -> game.getPersonalResults(player));
		if (ret.isEmpty())
			return grDAO.findById(gameID)
					.map(grs ->
					{
						return grs.getResults()
							.values()
							.stream()
							.filter(gr -> gr.getPlayerID().equals(player.getId()))
							.findFirst()
							.map(gr ->
							{
								List<GameResultPersonalDTO> retList = new ArrayList<>();
								var completion = gr.getCompletion();
								var difficulty = gr.getDifficulty();
								var timeTaken = gr.getTimeTaken();
								int l = Math.min(completion.size(),
										Math.min(difficulty.size(),
												timeTaken.size()));
								for (int i = 0; i < l; i++)
								{
									retList.add(new GameResultPersonalDTO(
											completion.get(i),
											timeTaken.get(i),
											difficulty.get(i)));
								}
								return retList;
							})
							.orElse(null);
					});
		return ret;
	}
	public Optional<Boolean> haveResultsChanged(UUID gameID)
	{
		return haveResultsChanged(gameID, getAccount());
	}
	public Optional<Boolean> haveResultsChanged(UUID gameID, Account acc)
	{
		return games.values()
				.stream()
				.filter(game -> game.getID().equals(gameID))
				.findFirst()
				.map(game -> game.haveResultsChanged(gameID, acc))
				.orElse(Optional.empty());
	}

	public UUID getGameID(String gameCode)
	{
		return Optional.ofNullable(games.get(gameCode))
				.map(g -> g.getID())
				.orElse(null);
	}
	public Optional<String> getGameForAccount(Account acc)
	{
		return games.entrySet()
				.stream()
				.filter(e -> e.getValue().containsPlayerOrSpectator(acc))
				.filter(e -> !acc.hasRole(PLAYER_ROLE)
						|| !e.getValue().hasGameFinishedFor(acc))
				.map(e -> e.getKey())
				.findAny();
	}
	
	public void noteInteraction(String gameCode)
	{
		noteInteraction(gameCode, getAccount());
	}
	public void noteInteraction(String gameCode, Account acc)
	{
		Game game = games.get(gameCode);
		if (game == null)
			return;
		game.noteInteraction(acc);
	}
	public void markInactive(String gameCode, Account acc)
	{
		Game game = games.get(gameCode);
		if (game == null)
			return;
		game.markInactive(acc);
	}
	@Scheduled(fixedDelay = 30000)
	public void removeInactive()
	{
		synchronized (games)
		{
			Iterator<Game> it = games.values().iterator();
			while (it.hasNext())
			{
				Game game = it.next();
				game.removeInactivePlayers(MAX_TIME_SINCE_LAST_INTERACTION_MILLI);
				if (game.isInactive())
					it.remove();
			}
		}
	}
	public boolean isPlayerActive(String gameCode, Account acc)
	{
		Game game = games.get(gameCode);
		if (game == null)
			return false;
		return game.isPlayerActive(acc);
	}
	
	private <T, U> Map<T, U> syncMap()
	{
		return synchronizedMap(new HashMap<>());
	}

	public Page<UUID> getHistory(int page)
	{
		return getHistory(page, getAccount());
	}
	public Page<UUID> getHistory(int page, Account player)
	{
		return grDAO.findAllByResults_PlayerID(player.getId(),
				PageRequest.of(page, HISTORY_PAGE_SIZE))
				.map(gr -> gr.getGameID());
	}
}
