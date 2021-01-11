package com.projteam.app.service;

import static com.projteam.app.domain.Account.PLAYER_ROLE;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.projteam.app.domain.Account;
import com.projteam.app.domain.game.Game;
import com.projteam.app.domain.game.tasks.ChronologicalOrder;
import com.projteam.app.domain.game.tasks.Task;
import com.projteam.app.domain.game.tasks.WordConnect;
import com.projteam.app.domain.game.tasks.WordFill;
import com.projteam.app.domain.game.tasks.WordFillElement;
import com.projteam.app.domain.game.tasks.answers.TaskAnswer;
import com.projteam.app.dto.game.tasks.TaskInfoDTO;

@Service
public class GameService
{
	private AccountService accServ;
	private LobbyService lobbyServ;
	
	private Map<String, Game> games;
	
	@Autowired
	public GameService(AccountService accServ, LobbyService lobbyServ)
	{
		this.accServ = accServ;
		this.lobbyServ = lobbyServ;
		
		games = new HashMap<>();
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
							.mapToObj(i -> generateRandomTask(targetDifficulty))
							.collect(Collectors.toList()))));
		
		games.put(gameCode, new Game(players, spectators, taskCount, targetDifficulty, taskMap));
		return true;
	}

	public Task generateRandomTask(double targetDifficulty)
	{
		//TODO implement proper Task data fetching
		//this is just mock data needed to test later game behaviour
				
		List<String> text = List.of("Lorem ipsum dolor sit amet, consectetur ",
				" elit. Quisque vestibulum, enim id fringilla sodales, libero   ipsum ",
				" erat, id ullamcorper elit ante auctor est. Nulla facilisi. Maecenas ultricies, magna non pretium mattis, ligula risus pulvinar elit, eu mattis ",
				" dolor nec turpis. Quisque elementum ",
				" accumsan. Lorem ipsum dolor ",
				" amet, consectetur adipiscing elit. In nec ",
				" nisi, et semper nisl. Cras placerat ",
				" orci eget congue. Duis vitae gravida odio. Etiam elit turpis, ",
				" ac nisi et, dapibus blandit nibh. Duis eleifend metus in iaculis tincidunt.");
		List<String> possibleAnswers = List.of("slowo1", "slowo2",
				"slowo3", "slowo4", "slowo5",
				"slowo6", "slowo7", "slowo8");
		List<WordFillElement.EmptySpace> emptySpaces = possibleAnswers.stream()
				.map(ans -> new WordFillElement.EmptySpace(ans))
				.collect(Collectors.toList());
		
		WordFill wf = new WordFill(UUID.randomUUID(),
				new WordFillElement(UUID.randomUUID(),
						text, emptySpaces, true,
						possibleAnswers), targetDifficulty);
		
		List<String> leftWords = List.of("Lorem", "ipsum", "dolor", "sit", "amet");
		List<String> rightWords = List.of("consectetur", "adipiscing", "elit", "sed do", "eiusmod");
		Map<Integer, Integer> correctMapping = Map.of(
				0, 3,
				1, 0,
				2, 4,
				3, 2,
				4, 1);
		
		WordConnect wc = new WordConnect(UUID.randomUUID(),
				leftWords, rightWords, correctMapping, targetDifficulty);
		
		List<String> coText = List.of("Lorem ipsum dolor sit amet",
				"consectetur adipiscing elit",
				"sed do eiusmod tempor incididunt",
				"ut labore et dolore magna aliqua",
				"Ut enim ad minim veniam",
				"quis nostrud exercitation",
				"ullamco laboris nisi ut",
				"aliquip ex ea commodo consequat");
		
		ChronologicalOrder co = new ChronologicalOrder(
				UUID.randomUUID(), coText, targetDifficulty);
		
		List<Task> ret = List.of(wf, wc, co);
		
		return ret.get((int) (Math.random() * ret.size()));
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
		return getCurrentTask(gameCode, player).toDTO(getTaskNumber(gameCode, player));
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
		return games.get(gameCode).hasGameFinished(player);
	}
	
	public void acceptAnswer(String gameCode, TaskAnswer answer)
	{
		acceptAnswer(gameCode, answer, getAccount());
	}
	private void acceptAnswer(String gameCode, TaskAnswer answer, Account player)
	{
		if (!games.containsKey(gameCode))
			return;
		
		Game game = games.get(gameCode);
		if (game.hasGameFinished(player))
			return;
		Task task = game.getCurrentTask(player);
		task.acceptAnswer(answer);
		game.advance(player);
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
		if (!games.containsKey(gameCode))
			return -1;
		Game game = games.get(gameCode);
		return game.getTaskCount(getAccount());
	}
}
