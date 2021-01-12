package com.projteam.app.service;

import static com.projteam.app.domain.Account.PLAYER_ROLE;
import java.util.Arrays;
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
import com.projteam.app.dao.game.GameResultDAO;
import com.projteam.app.dao.game.GameResultsDAO;
import com.projteam.app.domain.Account;
import com.projteam.app.domain.game.Game;
import com.projteam.app.domain.game.GameResult;
import com.projteam.app.domain.game.GameResults;
import com.projteam.app.domain.game.tasks.ChronologicalOrder;
import com.projteam.app.domain.game.tasks.ListWordFill;
import com.projteam.app.domain.game.tasks.Task;
import com.projteam.app.domain.game.tasks.WordConnect;
import com.projteam.app.domain.game.tasks.WordFill;
import com.projteam.app.domain.game.tasks.WordFillElement;
import com.projteam.app.domain.game.tasks.WordFillElement.EmptySpace;
import com.projteam.app.domain.game.tasks.answers.TaskAnswer;
import com.projteam.app.dto.game.tasks.TaskInfoDTO;

@Service
public class GameService
{
	private AccountService accServ;
	private LobbyService lobbyServ;
	private GameResultDAO grDAO;
	private GameResultsDAO grsDAO;
	
	private Map<String, Game> games;
	
	@Autowired
	public GameService(AccountService accServ, LobbyService lobbyServ,
			GameResultDAO grDAO, GameResultsDAO grsDAO)
	{
		this.accServ = accServ;
		this.lobbyServ = lobbyServ;
		this.grDAO = grDAO;
		this.grsDAO = grsDAO;
		
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
		if (!lobbyServ.deleteLobby(gameCode, requestSource))
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
		
		List<String> leftWords1 = List.of("data mining", "pattern identification", "quantitative modelling", "class label", "class membership", "explanatory variable", "variable", "fault-tolerant", "spurious pattern", "outlier");
		List<String> rightWords1 = List.of("eksploracja danych", "identyfikacja wzorca", "modelowanie ilościowe", "etykieta klasy", "przynależność do klasy", "zmienna objaśniająca", "zmienna", "odporny na błędy", "fałszywy wzorzec", "wartość skrajna");
		Map<Integer, Integer> correctMapping1 = Map.ofEntries(
				Map.entry(0, 0),
				Map.entry(1, 1),
				Map.entry(2, 2),
				Map.entry(3, 3),
				Map.entry(4, 4),
				Map.entry(5, 5),
				Map.entry(6, 6),
				Map.entry(7, 7),
				Map.entry(8, 8),
				Map.entry(9, 9));

		WordConnect wc1 = new WordConnect(UUID.randomUUID(),
				leftWords1, rightWords1, correctMapping1, targetDifficulty);
		
		List<String> leftWords2 = List.of("keynote", "to convey (information)", "to unveil (a theme)", "consistent", "stiff", "a knack (for sth)", "a flair", "intricate", "dazzling", "to rehearse");
		List<String> rightWords2 = List.of("myśl przewodnia, główny motyw", "przekazywać/dostarczać (informacje)", "odkryć, ujawnić, odsłonić", "spójny, zgodny, konsekwentny", "sztywny, zdrętwiały", "talent, zręczność", "klasa, dar", "zawiły, misterny", "olśniewający", "próbować, przygotowywać się");
		Map<Integer, Integer> correctMapping2 = Map.ofEntries(
				Map.entry(0, 0),
				Map.entry(1, 1),
				Map.entry(2, 2),
				Map.entry(3, 3),
				Map.entry(4, 4),
				Map.entry(5, 5),
				Map.entry(6, 6),
				Map.entry(7, 7),
				Map.entry(8, 8),
				Map.entry(9, 9));
		
		WordConnect wc2 = new WordConnect(UUID.randomUUID(),
				leftWords2, rightWords2, correctMapping2, targetDifficulty);
		
		List<String> leftWords3 = List.of("SMATTERING", "DESCEND", "INEVITABLE", "PROPENSITY", "APPROACH", "OVERESTIMATE", "INGRESS", "GLEAN", "DEBUNK", "SOUND", "WINDING", "IN, DEPTH", "EGRESS", "ITEM");
		List<String> rightWords3 = List.of("bit, small amount", "go down, fall, drop", "bound to happen, predestined, unavoidable", "tendency, inclination", "attitude, method, way, manner", "overvalue, overstate, amplify", "entry, entrance", "obtain, gather", "invalidate, discredit", "healthy, toned, in good shape", "full of twists and turns, zigzagging", "thoroughly, extensively", "exit, way out", "thing, article, object");
		Map<Integer, Integer> correctMapping3 = Map.ofEntries(
				Map.entry(0, 0),
				Map.entry(1, 1),
				Map.entry(2, 2),
				Map.entry(3, 3),
				Map.entry(4, 4),
				Map.entry(5, 5),
				Map.entry(6, 6),
				Map.entry(7, 7),
				Map.entry(8, 8),
				Map.entry(9, 9),
				Map.entry(10, 10),
				Map.entry(11, 11),
				Map.entry(12, 12),
				Map.entry(13, 13));
		
		if (leftWords3.size() != rightWords3.size() || rightWords3.size() != correctMapping3.size())
			throw new IllegalArgumentException(leftWords3.size() + ", " + rightWords3.size() + ", " + correctMapping3.size());
		
		WordConnect wc3 = new WordConnect(UUID.randomUUID(),
				leftWords3, rightWords3, correctMapping3, targetDifficulty);
		
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
		
		List<WordFillElement> lwfeList1 = List.of(
				wordFillElement(List.of("I’m ", " you asked me that question."),
						emptySpaceList("GLAD"),
						true,
						List.of("GLAD", "SORRY", "REGRET", "INTERESTED")),
				wordFillElement(List.of("I’m afraid I can’t say it at the ", " of my head."),
						emptySpaceList("GLAD"),
						true,
						List.of("TIP", "END", "TOP", "BACK")),
				wordFillElement(List.of("As I’ve ", " before in my presentation, …"),
						emptySpaceList("MENTIONED"),
						true,
						List.of("SPOKEN", "MENTIONED", "SEEN", "TALKED")),
				wordFillElement(List.of("Do you mind if we deal ", " it later?"),
						emptySpaceList("WITH"),
						true,
						List.of("ON", "WITHOUT", "WITH", "FROM")),
				wordFillElement(List.of("In fact, it goes ", " to what I was saying earlier, …"),
						emptySpaceList("BACK"),
						true,
						List.of("BACK", "ON", "IN", "UP")),
				wordFillElement(List.of("I don’t want to go into too much ", " at this stage."),
						emptySpaceList("DETAIL"),
						true,
						List.of("DISTRUCTIONS", "DETAIL", "TIME", "DISCUSSIONS")));
		
		ListWordFill lwf1 = new ListWordFill(UUID.randomUUID(), lwfeList1, targetDifficulty);
		
		List<WordFillElement> lwfeList2 = List.of(
				wordFillElement(List.of("the act or way of leaving place: "),
						emptySpaceList("egress"),
						true,
						List.of("descend", "sound", "egress")),
				wordFillElement(List.of("a tendency to behave in a particular way: "),
						emptySpaceList("propensity"),
						true,
						List.of("smattering", "propensity", "glean")),
				wordFillElement(List.of("a very small amount or number: "),
						emptySpaceList("smattering"),
						true,
						List.of("glean", "ingress", "smattering")),
				wordFillElement(List.of("come down: "),
						emptySpaceList("descend"),
						true,
						List.of("descend", "in-depth", "winding")),
				wordFillElement(List.of("done carefully and in great detail: "),
						emptySpaceList("in-depth"),
						true,
						List.of("in-depth", "ingress", "debunk")),
				wordFillElement(List.of("healthy; in good condition: "),
						emptySpaceList("sound"),
						true,
						List.of("glean", "winding", "sound")),
				wordFillElement(List.of("a lot of something; big amount: "),
						emptySpaceList("sheer number"),
						true,
						List.of("propensity", "sheer number", "egress")),
				wordFillElement(List.of("repeatedly turns in different directions: "),
						emptySpaceList("winding"),
						true,
						List.of("debunk", "winding", "smattering")),
				wordFillElement(List.of("the act of entering something: "),
						emptySpaceList("ingress"),
						true,
						List.of("ingress", "egress", "propensity")),
				wordFillElement(List.of("to collect information in small amounts and often with difficulty: "),
						emptySpaceList("glean"),
						true,
						List.of("glean", "smattering", "debunk")),
				wordFillElement(List.of("to show that something is not true: "),
						emptySpaceList("debunk"),
						true,
						List.of("glean", "debunk", "in-depth")));
		
		ListWordFill lwf2 = new ListWordFill(UUID.randomUUID(), lwfeList2, targetDifficulty);
		
		List<Task> ret = List.of(wf, wc1, wc2, wc3, co, lwf1, lwf2);
		
		return ret.get((int) (Math.random() * ret.size()));
	}
	private WordFillElement wordFillElement(List<String> text,
			List<EmptySpace> emptySpaces,
			boolean startWithText,
			List<String> possibleAnswers)
	{
		return new WordFillElement(UUID.randomUUID(),
				text, emptySpaces, startWithText, possibleAnswers);
	}
	private List<EmptySpace> emptySpaceList(String... list)
	{
		return Arrays.asList(list)
			.stream()
			.map(ans -> new EmptySpace(ans))
			.collect(Collectors.toList());
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
		return games.get(gameCode).hasGameFinishedFor(player);
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
		if (game.hasGameFinishedFor(player))
			return;
		Task task = game.getCurrentTask(player);
		double completion = task.acceptAnswer(answer);
		game.advance(player, completion);
		
		checkIfGameFinished(gameCode, game);
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
		GameResults grs = game.createGameResult();
		for (GameResult gr: grs.getResults().values())
			grDAO.save(gr);
		grsDAO.save(grs);
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
