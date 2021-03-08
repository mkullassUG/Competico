package com.projteam.app.service.game;

import static com.projteam.app.domain.Account.PLAYER_ROLE;
import static java.util.Collections.synchronizedMap;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.projteam.app.dao.game.PlayerResultDAO;
import com.projteam.app.dao.game.GameResultDAO;
import com.projteam.app.domain.Account;
import com.projteam.app.domain.game.Game;
import com.projteam.app.domain.game.PlayerResult;
import com.projteam.app.domain.game.GameResult;
import com.projteam.app.domain.game.PlayerData;
import com.projteam.app.domain.game.tasks.Task;
import com.projteam.app.domain.game.tasks.answers.TaskAnswer;
import com.projteam.app.dto.game.GameResultPersonalDTO;
import com.projteam.app.dto.game.GameResultTotalDTO;
import com.projteam.app.dto.game.GameResultTotalDuringGameDTO;
import com.projteam.app.dto.game.tasks.show.TaskInfoDTO;
import com.projteam.app.service.AccountService;

@Service
public class GameService
{
	private AccountService accServ;
	private LobbyService lobbyServ;
	private GameTaskDataService gtdServ;
	private PlayerDataService pdServ;
	private PlayerResultDAO prDAO;
	private GameResultDAO grDAO;
	
	private Map<String, Game> games;
	
	private DateFormat df = new SimpleDateFormat("EEEE, d MMM yyyy HH:mm", new Locale("pl"));
	
	private static final long MAX_TIME_SINCE_LAST_INTERACTION_MILLI = 120000;
	private static final int HISTORY_PAGE_SIZE = 30;
	private static final int GAME_VALUE = 32;
	
	private final ObjectMapper mapper = new ObjectMapper();
	
	@Autowired
	public GameService(AccountService accServ,
			LobbyService lobbyServ,
			GameTaskDataService gtdServ,
			PlayerDataService pdServ,
			PlayerResultDAO prDAO,
			GameResultDAO grDAO)
	{
		this.accServ = accServ;
		this.lobbyServ = lobbyServ;
		this.gtdServ = gtdServ;
		this.pdServ = pdServ;
		this.prDAO = prDAO;
		this.grDAO = grDAO;
		
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
		
		
		int taskCount = 6 + (int) (Math.random() * 4); //TODO export to properties
		double targetDifficulty = 100; //TODO refactor
		Map<UUID, List<Task>> taskMap = new HashMap<>();
		taskMap.putAll(players.stream()
				.collect(Collectors.toMap(player -> player.getId(), player ->
					IntStream.range(0, taskCount)
							.mapToObj(i -> gtdServ
									.generateRandomTask(targetDifficulty))
							.collect(Collectors.toList()))));
		if (!lobbyServ.deleteLobby(gameCode, requestSource))
			return false;
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
					.map(task -> task.prepareTaskInfo(getTaskNumber(gameCode, player), getTaskCount(gameCode)))
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
	
	@Transactional
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
			GameResult gr = game.createGameResult();
			for (PlayerResult pr: gr.getResults().values())
				prDAO.save(pr);
			grDAO.save(gr);
			
			updateRatings(gameCode, game);
			
			games.remove(gameCode);
		}
	}
	private void updateRatings(String gameCode, Game game)
	{
		/*
		 * O(n^2) time complexity, but way better accuracy than
		 * just running the algorithm against 1 player higher and 1 player lower.
		 * Still, better algorithms should be possible.
		 * 
		 * Idea 1: compute the average rating of the game (once outside of the loop), 
		 * compute the expected result from that and the current player's rating,
		 * use his overall position as the actual result (mapped to [-1, 1]).
		 * 
		 * Total completion should also be a factor in computing the rating:
		 * - A player with 0% completion should never win rating
		 * - A player with 100% completion should never lose rating
		 * - The implementation must not introduce any
		 * noticeable inflation or deflation to the system
		 */
		
		Map<UUID, GameResultTotalDuringGameDTO> scores =
				game.getCurrentResultsWithIDs();
		
		Map<UUID, PlayerData> playerDataMap = new HashMap<>();
		Map<UUID, Integer> newRatings = new HashMap<>();
		
		scores.forEach((playerID, playerScore) ->
		{
			PlayerData playerPD = pdServ.getPlayerData(accServ
					.findByID(playerID)
					.orElse(null))
					.orElse(null);
			if (playerPD == null)
				return;
			
			int playerRating = playerPD.getRating();
			
			double scoreDeltaTotal = 0;
			int eligibleOpponentsCount = 0;
			for (Entry<UUID, GameResultTotalDuringGameDTO> e:
				scores.entrySet())
			{
				UUID pID = e.getKey();
				if (pID.equals(playerID))
					continue;
				
				PlayerData pPD = pdServ.getPlayerData(accServ
						.findByID(pID)
						.orElse(null))
						.orElse(null);
				if (pPD == null)
					continue;
				
				int pRating = playerPD.getRating();
				
				double real = (
						Math.signum(
							playerScore.getTotalScore()
							- e.getValue().getTotalScore())
						+ 1) / 2.0;
				double expected = 1 / (1 + Math.pow(10,
						(pRating - playerRating) / 400.0));
				
				scoreDeltaTotal += (real - expected);
				eligibleOpponentsCount++;
			}
			
			if (eligibleOpponentsCount == 0)
				return;
			
			playerDataMap.put(playerID, playerPD);
			newRatings.put(playerID, (int) Math.round(playerRating +
					GAME_VALUE * (scoreDeltaTotal / eligibleOpponentsCount)));
		});
		
		playerDataMap.forEach((playerID, playerPD) ->
		{
			playerPD.setRating(newRatings.getOrDefault(playerID, playerPD.getRating()));
			pdServ.savePlayerData(playerPD);
		});
	}
	
	private Account getAccount()
	{
		return accServ.getAuthenticatedAccount()
				.orElseThrow(() -> new IllegalArgumentException("Not authenticated."));
	}

	private TaskAnswer convertRawAnswer(String gameCode, JsonNode answer) throws JsonProcessingException
	{
		if (answer == null)
			return mapper.treeToValue(
					JsonNodeFactory.instance.nullNode(),
					getCurrentTask(gameCode).getAnswerType());
		return mapper.treeToValue(answer, getCurrentTask(gameCode).getAnswerType());
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

	@Transactional
	public Optional<List<GameResultTotalDTO>> getResults(UUID gameID)
	{
		return grDAO.findById(gameID)
				.map(gr ->
				{
					List<GameResultTotalDTO> ret = new ArrayList<>();
					for (PlayerResult pr: gr.getResults().values())
					{
						Account player = accServ.findByID(pr.getPlayerID())
								.orElse(null);
						var completion = pr.getCompletion();
						var difficulty = pr.getDifficulty();
						var timeTaken = pr.getTimeTaken();
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
	@Transactional
	public Optional<List<GameResultPersonalDTO>> getPersonalResults(UUID gameID)
	{
		return getPersonalResults(gameID, getAccount());
	}
	@Transactional
	public Optional<List<GameResultPersonalDTO>> getPersonalResults(UUID gameID, Account player)
	{
		Optional<List<GameResultPersonalDTO>> ret = games.values()
				.stream()
				.filter(game -> game.getID().equals(gameID))
				.findFirst()
				.map(game -> game.getPersonalResults(player));
		if (ret.isEmpty())
			return grDAO.findById(gameID)
					.map(gr ->
					{
						return gr.getResults()
							.values()
							.stream()
							.filter(pr -> pr.getPlayerID().equals(player.getId()))
							.findFirst()
							.map(pr ->
							{
								List<GameResultPersonalDTO> retList = new ArrayList<>();
								var completion = pr.getCompletion();
								var difficulty = pr.getDifficulty();
								var timeTaken = pr.getTimeTaken();
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
	@Transactional
	public Page<Map<String, String>> getHistory(int page)
	{
		return getHistory(page, getAccount());
	}
	@Transactional
	public Page<Map<String, String>> getHistory(int page, Account player)
	{
		return grDAO.findAllByResults_PlayerID(player.getId(),
				PageRequest.of(page, HISTORY_PAGE_SIZE, Sort.by(Order.desc("date"))))
				.map(gr -> Map.of(
						"id", gr.getGameID().toString(),
						"date", formatDate(gr.getDate())));
	}
	public Optional<Integer> getRating()
	{
		return getRating(getAccount());
	}
	public Optional<Integer> getRating(Account player)
	{
		return pdServ.getPlayerData(player)
				.map(pd -> pd.getRating());
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
	
	private String formatDate(Date date)
	{
		return df.format(date);
	}
	
	private <T, U> Map<T, U> syncMap()
	{
		return synchronizedMap(new HashMap<>());
	}
}
