package com.projteam.app.service;

import static com.projteam.app.domain.Account.LECTURER_ROLE;
import static com.projteam.app.domain.Account.PLAYER_ROLE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.RETURNS_DEFAULTS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import com.projteam.app.dao.game.PlayerResultDAO;
import com.fasterxml.jackson.databind.JsonNode;
import com.projteam.app.dao.game.GameResultDAO;
import com.projteam.app.domain.Account;
import com.projteam.app.domain.game.PlayerResult;
import com.projteam.app.domain.game.GameResult;
import com.projteam.app.domain.game.PlayerData;
import com.projteam.app.domain.game.tasks.Task;
import com.projteam.app.domain.game.tasks.WordConnect;
import com.projteam.app.domain.game.tasks.answers.WordFillAnswer;
import com.projteam.app.dto.game.GameResultPersonalDTO;
import com.projteam.app.dto.game.GameResultTotalDuringGameDTO;
import com.projteam.app.service.game.GameService;
import com.projteam.app.service.game.GameTaskDataService;
import com.projteam.app.service.game.LobbyService;
import com.projteam.app.service.game.PlayerDataService;

public class GameServiceTests
{
	private @Mock AccountService accountService;
	private @Mock LobbyService lobbyService;
	private @Mock PlayerResultDAO prDAO;
	private @Mock GameResultDAO grDAO;
	private @Mock GameTaskDataService gtdService;
	private @Mock PlayerDataService pdService;
	
	private @InjectMocks GameService gameService;
	
	@BeforeEach
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void contextLoads()
	{
		assertNotNull(gameService);
	}
	
	@ParameterizedTest
	@MethodSource({"mockPlayerHostAndPlayer", "mockLecturerHostAndPlayer"})
	public void canCreateGameFromLobby(Account host, Account player)
	{
		String gameCode = "gameCode";
		List<Account> players = List.of(host, player)
				.stream()
				.filter(a -> a.hasRole(PLAYER_ROLE))
				.collect(Collectors.toList());
		when(lobbyService.lobbyExists(gameCode)).thenReturn(true);
		when(lobbyService.getPlayers(gameCode)).thenReturn(players);
		when(lobbyService.getHost(gameCode)).thenReturn(host);
		when(lobbyService.isHost(gameCode, host)).thenReturn(true);
		when(lobbyService.deleteLobby(gameCode, host)).thenReturn(true);
		
		assertTrue(gameService.createGameFromLobby(gameCode, host));
	}
	@ParameterizedTest
	@MethodSource({"mockPlayerHostAndPlayer", "mockLecturerHostAndPlayer"})
	public void canCreateGameFromLobbyWithAuthenticatedAccount(Account host, Account player)
	{
		String gameCode = "gameCode";
		List<Account> players = List.of(host, player)
				.stream()
				.filter(a -> a.hasRole(PLAYER_ROLE))
				.collect(Collectors.toList());
		when(accountService.getAuthenticatedAccount()).thenReturn(Optional.of(host));
		when(lobbyService.lobbyExists(gameCode)).thenReturn(true);
		when(lobbyService.getPlayers(gameCode)).thenReturn(players);
		when(lobbyService.getHost(gameCode)).thenReturn(host);
		when(lobbyService.isHost(gameCode, host)).thenReturn(true);
		when(lobbyService.deleteLobby(gameCode, host)).thenReturn(true);
		
		assertTrue(gameService.createGameFromLobby(gameCode));
	}
	@ParameterizedTest
	@MethodSource({"mockPlayerHostAndPlayer", "mockLecturerHostAndPlayer"})
	public void cannotCreateGameIfLobbyDoesNotExist(Account host, Account player)
	{
		String gameCode = "gameCode";
		List<Account> players = List.of(host, player)
				.stream()
				.filter(a -> a.hasRole(PLAYER_ROLE))
				.collect(Collectors.toList());
		when(accountService.getAuthenticatedAccount()).thenReturn(Optional.of(host));
		when(lobbyService.lobbyExists(gameCode)).thenReturn(false);
		when(lobbyService.getPlayers(gameCode)).thenReturn(players);
		when(lobbyService.getHost(gameCode)).thenReturn(host);
		when(lobbyService.isHost(gameCode, host)).thenReturn(true);
		when(lobbyService.deleteLobby(gameCode, host)).thenReturn(true);
		
		assertFalse(gameService.createGameFromLobby(gameCode));
	}
	@ParameterizedTest
	@MethodSource({"mockPlayerHostAndPlayer", "mockLecturerHostAndPlayer"})
	public void cannotCreateGameIfCannotDeleteLobby(Account host, Account player)
	{
		String gameCode = "gameCode";
		List<Account> players = List.of(host, player)
				.stream()
				.filter(a -> a.hasRole(PLAYER_ROLE))
				.collect(Collectors.toList());
		when(accountService.getAuthenticatedAccount()).thenReturn(Optional.of(host));
		when(lobbyService.lobbyExists(gameCode)).thenReturn(true);
		when(lobbyService.getPlayers(gameCode)).thenReturn(players);
		when(lobbyService.getHost(gameCode)).thenReturn(host);
		when(lobbyService.isHost(gameCode, host)).thenReturn(true);
		when(lobbyService.deleteLobby(gameCode, host)).thenReturn(false);
		
		assertFalse(gameService.createGameFromLobby(gameCode));
	}
	@ParameterizedTest
	@MethodSource({"mockLecturerHost"})
	public void cannotCreateGameIfLobbyContainsNoPlayers(Account host)
	{
		String gameCode = "gameCode";
		when(lobbyService.lobbyExists(gameCode)).thenReturn(true);
		when(lobbyService.getPlayers(gameCode)).thenReturn(List.of());
		when(lobbyService.getHost(gameCode)).thenReturn(host);
		when(lobbyService.isHost(gameCode, host)).thenReturn(true);
		when(lobbyService.deleteLobby(gameCode, host)).thenReturn(true);
		
		assertFalse(gameService.createGameFromLobby(gameCode, host));
	}
	@ParameterizedTest
	@MethodSource({"mockPlayerHostAndPlayer", "mockLecturerHostAndPlayer"})
	public void cannotCreateGameIfGameAlreadyExists(Account host, Account player)
	{
		String gameCode = "gameCode";
		List<Account> players = List.of(host, player)
				.stream()
				.filter(a -> a.hasRole(PLAYER_ROLE))
				.collect(Collectors.toList());
		when(lobbyService.lobbyExists(gameCode)).thenReturn(true);
		when(lobbyService.getPlayers(gameCode)).thenReturn(players);
		when(lobbyService.getHost(gameCode)).thenReturn(host);
		when(lobbyService.isHost(gameCode, host)).thenReturn(true);
		when(lobbyService.deleteLobby(gameCode, host)).thenReturn(true);
		
		gameService.createGameFromLobby(gameCode, host);
		assertFalse(gameService.createGameFromLobby(gameCode, host));
	}
	@ParameterizedTest
	@MethodSource("mockTwoHostsAndPlayer")
	public void cannotCreateGameIfNotHostOfLobby(Account host, Account otherHost, Account player)
	{
		String gameCode = "gameCode";
		List<Account> players = List.of(host, otherHost, player)
				.stream()
				.filter(a -> a.hasRole(PLAYER_ROLE))
				.collect(Collectors.toList());
		when(lobbyService.lobbyExists(gameCode)).thenReturn(true);
		when(lobbyService.getPlayers(gameCode)).thenReturn(players);
		when(lobbyService.getHost(gameCode)).thenReturn(host);
		when(lobbyService.isHost(gameCode, host)).thenReturn(true);
		when(lobbyService.deleteLobby(gameCode, host)).thenReturn(true);
		
		assertFalse(gameService.createGameFromLobby(gameCode, otherHost));
	}
	
	@ParameterizedTest
	@MethodSource({"mockPlayerHostAndPlayer", "mockLecturerHostAndPlayer"})
	public void gameShouldExist(Account host, Account player)
	{
		String gameCode = "gameCode";
		createGameFromLobby(gameCode, host, player);
		
		assertTrue(gameService.gameExists(gameCode));
	}
	@ParameterizedTest
	@MethodSource({"mockPlayerHostAndPlayer", "mockLecturerHostAndPlayer"})
	public void canGetCurrentTaskInfo(Account host, Account player)
	{
		when(accountService.getAuthenticatedAccount())
			.thenReturn(Optional.of(player));
		when(gtdService.generateRandomTask(anyDouble()))
			.thenReturn(mockTask());
		
		String gameCode = "gameCode";
		createGameFromLobby(gameCode, host, player);
		
		assertNotNull(gameService.getCurrentTaskInfo(gameCode, player));
	}
	@ParameterizedTest
	@MethodSource({"mockPlayerHostAndPlayer", "mockLecturerHostAndPlayer"})
	public void canGetCurrentTaskInfoWithAuthenticatedAccount(Account host, Account player)
	{
		when(accountService.getAuthenticatedAccount())
			.thenReturn(Optional.of(player));
		when(gtdService.generateRandomTask(anyDouble()))
			.thenReturn(mockTask());
		
		String gameCode = "gameCode";
		
		createGameFromLobby(gameCode, host, player);
		
		assertNotNull(gameService.getCurrentTaskInfo(gameCode));
	}
	@ParameterizedTest
	@MethodSource({"mockPlayerHostAndPlayer", "mockLecturerHostAndPlayer"})
	public void cannotGetCurrentTaskInfoIfGameDoesNotExist(Account host, Account player)
	{
		String gameCode = "gameCode";
		
		assertNull(gameService.getCurrentTaskInfo(gameCode, player));
	}
	
	@ParameterizedTest
	@MethodSource({"mockPlayerHostAndPlayer", "mockLecturerHostAndPlayer"})
	public void gameShouldNotBeFinishedAtTheStart(Account host, Account player)
	{
		String gameCode = "gameCode";
		createGameFromLobby(gameCode, host, player);
		
		assertFalse(gameService.hasGameFinished(gameCode, player));
	}
	@ParameterizedTest
	@MethodSource({"mockPlayerHostAndPlayer", "mockLecturerHostAndPlayer"})
	public void gameShouldNotBeFinishedAtTheStartWithAuthenticatedAccount
		(Account host, Account player)
	{
		String gameCode = "gameCode";
		when(accountService.getAuthenticatedAccount())
			.thenReturn(Optional.of(player));
		createGameFromLobby(gameCode, host, player);
		
		assertFalse(gameService.hasGameFinished(gameCode));
	}
	
	@ParameterizedTest
	@MethodSource({"mockPlayerHostAndPlayer", "mockLecturerHostAndPlayer"})
	public void taskNumberShouldStartAt0(Account host, Account player)
	{
		String gameCode = "gameCode";
		createGameFromLobby(gameCode, host, player);
		
		assertEquals(gameService.getTaskNumber(gameCode, player), 0);
	}
	@ParameterizedTest
	@MethodSource({"mockPlayerHostAndPlayer", "mockLecturerHostAndPlayer"})
	public void taskNumberShouldStartAt0WithAuthenticatedAccount
		(Account host, Account player)
	{
		String gameCode = "gameCode";
		when(accountService.getAuthenticatedAccount())
			.thenReturn(Optional.of(player));
		createGameFromLobby(gameCode, host, player);
		
		assertEquals(gameService.getTaskNumber(gameCode), 0);
	}
	@ParameterizedTest
	@MethodSource({"mockPlayerHostAndPlayer", "mockLecturerHostAndPlayer"})
	public void cannotGetTaskNumberIfGameDoesNotExist(Account host, Account player)
	{
		String gameCode = "gameCode";
		
		assertEquals(gameService.getTaskNumber(gameCode, player), -1);
	}
	
	@ParameterizedTest
	@MethodSource({"mockPlayerHostAndPlayer", "mockLecturerHostAndPlayer"})
	public void taskCountShouldBePositive(Account host, Account player)
	{
		String gameCode = "gameCode";
		when(accountService.getAuthenticatedAccount())
			.thenReturn(Optional.of(player));
		createGameFromLobby(gameCode, host, player);
		
		assertTrue(gameService.getTaskCount(gameCode) > 0);
	}
	@ParameterizedTest
	@MethodSource({"mockPlayerHostAndPlayer", "mockLecturerHostAndPlayer"})
	public void cannotGetTaskCountIfGameDoesNotExist(Account host, Account player)
	{
		String gameCode = "gameCode";
		when(accountService.getAuthenticatedAccount())
			.thenReturn(Optional.of(player));
		
		assertEquals(gameService.getTaskCount(gameCode), -1);
	}
	
	@ParameterizedTest
	@MethodSource({"mockPlayerHostAndPlayer", "mockLecturerHostAndPlayer"})
	public void shouldGetGameID
		(Account host, Account player)
	{
		String gameCode = "gameCode";
		createGameFromLobby(gameCode, host, player);
		
		assertNotNull(gameService.getGameID(gameCode));
	}
	@ParameterizedTest
	@MethodSource({"mockPlayerHostAndPlayer", "mockLecturerHostAndPlayer"})
	public void shouldGetGameForAccount
		(Account host, Account player)
	{
		String gameCode = "gameCode";
		createGameFromLobby(gameCode, host, player);
		
		assertEquals(gameService.getGameForAccount(host), Optional.of(gameCode));
		assertEquals(gameService.getGameForAccount(player), Optional.of(gameCode));
	}
	@ParameterizedTest
	@MethodSource({"mockPlayerHostAndPlayer", "mockLecturerHostAndPlayer"})
	public void shouldNotGetGameForAccountIfPlayerFinished
		(Account host, Account player)
	{
		when(accountService.getAuthenticatedAccount())
			.thenReturn(Optional.of(player));
		when(gtdService.generateRandomTask(anyDouble()))
			.thenReturn(mockTask());
		
		String gameCode = "gameCode";
		createGameFromLobby(gameCode, host, player);
		
		int taskCount = gameService.getTaskCount(gameCode);
				
				IntStream.range(0, taskCount)
					.forEach(i -> 
					{
						gameService.acceptAnswer(gameCode,
								mock(gameService.getCurrentAnswerClass(
										gameCode, player),
										inv ->
											isCollectionOrMap(inv.getMethod()
													.getReturnType())?
											null:RETURNS_DEFAULTS.answer(inv)));
					});
		
		assertEquals(gameService.getGameForAccount(player), Optional.empty());
	}
	
	@ParameterizedTest
	@MethodSource({"mockPlayerHostAndPlayer", "mockLecturerHostAndPlayer"})
	public void canGetCurrentGameResults(Account host, Account player)
	{
		String gameCode = "gameCode";
		int playerCount = createGameFromLobby(gameCode, host, player);
		UUID gameID = gameService.getGameID(gameCode);
		
		Optional<List<GameResultTotalDuringGameDTO>> ret =
				gameService.getCurrentResults(gameID);
		assertTrue(ret.isPresent());
		assertEquals(ret.get().size(), playerCount);
	}
	@ParameterizedTest
	@MethodSource({"mockPlayerHostAndPlayer", "mockLecturerHostAndPlayer"})
	public void cannotGetCurrentGameResultsIfGameDoesNotExist(Account host, Account player)
	{
		String gameCode = "gameCode";
		createGameFromLobby(gameCode, host, player);
		UUID gameID = UUID.randomUUID();
		
		assertTrue(gameService.getCurrentResults(gameID).isEmpty());
	}
	
	@ParameterizedTest
	@MethodSource({"mockPlayerHost", "mockLecturerHost"})
	public void canGetGameResults(Account host)
	{
		UUID gameID = UUID.randomUUID();
		GameResult gr = new GameResult(gameID);
		mockPlayers(5).forEach(player ->
		{
			UUID playerID = player.getId();
			gr.addResult(new PlayerResult(UUID.randomUUID(),
					playerID,
					Map.of(0, Math.round(5 * Math.random()) / 5.0),
					Map.of(0, 100.0 + (Math.random() * 10)),
					Map.of(0, 5000l + ((long) (Math.random() * 10000)))));
			when(accountService.findByID(playerID)).thenReturn(Optional.of(player));
		});
		when(grDAO.findById(gameID)).thenReturn(Optional.of(gr));
		
		assertTrue(gameService.getResults(gameID).isPresent());
	}
	@ParameterizedTest
	@MethodSource({"mockPlayerHostAndPlayer", "mockLecturerHostAndPlayer"})
	public void cannotGetGameResultsIfGameHistoryDoesNotExist(Account host, Account player)
	{
		UUID gameID = UUID.randomUUID();
		when(grDAO.findById(gameID)).thenReturn(Optional.empty());
		
		assertTrue(gameService.getResults(gameID).isEmpty());
	}
	
	@ParameterizedTest
	@MethodSource({"mockPlayerHostAndTwoPlayers", "mockLecturerHostAndTwoPlayers"})
	public void canGetPersonalResultsDuringGame(
			Account host, Account player, Account otherPlayer)
	{
		when(accountService.getAuthenticatedAccount())
			.thenReturn(Optional.of(player));
		when(gtdService.generateRandomTask(anyDouble()))
			.thenReturn(mockTask());
		
		String gameCode = "gameCode";
		createGameFromLobby(gameCode, host, player, otherPlayer);
		
		UUID gameID = gameService.getGameID(gameCode);
		int taskCount = gameService.getTaskCount(gameCode);
		
		IntStream.range(0, taskCount)
			.forEach(i -> 
			{
				gameService.acceptAnswer(gameCode,
						mock(gameService.getCurrentAnswerClass(
								gameCode, player),
								inv ->
									isCollectionOrMap(inv.getMethod()
											.getReturnType())?
									null:RETURNS_DEFAULTS.answer(inv)));
			});
		
		Optional<List<GameResultPersonalDTO>> res =
				gameService.getPersonalResults(gameID);
		
		assertTrue(res.isPresent());
		assertEquals(res.get().size(), taskCount);
	}
	@ParameterizedTest
	@MethodSource({"mockPlayerHostAndTwoPlayers", "mockLecturerHostAndTwoPlayers"})
	public void canGetPersonalResultsDuringGameWithAuthenticatedAccount(
			Account host, Account player, Account otherPlayer)
	{
		when(accountService.getAuthenticatedAccount())
			.thenReturn(Optional.of(player));
		when(gtdService.generateRandomTask(anyDouble()))
			.thenReturn(mockTask());
		
		String gameCode = "gameCode";
		createGameFromLobby(gameCode, host, player, otherPlayer);
		
		UUID gameID = gameService.getGameID(gameCode);
		int taskCount = gameService.getTaskCount(gameCode);
		
		IntStream.range(0, taskCount)
			.forEach(i -> 
				gameService.acceptAnswer(gameCode,
						mock(gameService.getCurrentAnswerClass(
								gameCode, player),
								inv ->
									isCollectionOrMap(inv.getMethod()
											.getReturnType())?
									null:RETURNS_DEFAULTS.answer(inv))));
		
		Optional<List<GameResultPersonalDTO>> res =
				gameService.getPersonalResults(gameID);
		
		assertTrue(res.isPresent());
		assertEquals(res.get().size(), taskCount);
	}
	@ParameterizedTest
	@MethodSource({"mockPlayerHost", "mockLecturerHost"})
	public void canGetPersonalResultsFromHistory(Account host)
	{
		String gameCode = "gameCode";
		List<Account> players = mockPlayers(5);
		createGameFromLobby(gameCode, host, players.toArray(l -> new Account[l]));
		UUID gameID = UUID.randomUUID();
		GameResult gr = new GameResult(gameID);
		players.forEach(player ->
		{
			UUID playerID = player.getId();
			gr.addResult(new PlayerResult(UUID.randomUUID(),
					playerID,
					Map.of(0, Math.round(5 * Math.random()) / 5.0),
					Map.of(0, 100.0 + (Math.random() * 10)),
					Map.of(0, 5000l + ((long) (Math.random() * 10000)))));
			when(accountService.findByID(playerID)).thenReturn(Optional.of(player));
		});
		when(grDAO.findById(gameID)).thenReturn(Optional.of(gr));
		
		players.forEach(player ->
		{
			when(accountService.getAuthenticatedAccount())
				.thenReturn(Optional.of(player));
			Optional<List<GameResultPersonalDTO>> res =
					gameService.getPersonalResults(gameID);
			assertTrue(res.isPresent());
			assertEquals(res.get().size(), 1);
		});
		
	}
	@ParameterizedTest
	@MethodSource({"mockPlayerHostAndPlayer", "mockLecturerHostAndPlayer"})
	public void cannotGetPersonalResultsDuringGameIfGameOrHistoryDoesNotExist(Account host, Account player)
	{
		String gameCode = "gameCode";
		createGameFromLobby(gameCode, host, player);
		UUID gameID = UUID.randomUUID();
		when(grDAO.findById(gameID)).thenReturn(Optional.empty());
		
		assertTrue(gameService.getPersonalResults(gameID, player).isEmpty());
	}
	
	@ParameterizedTest
	@MethodSource({"mockPlayerHostAndPlayer", "mockLecturerHostAndPlayer"})
	public void canGetAnswerType(Account host, Account player)
	{
		when(gtdService.generateRandomTask(anyDouble()))
			.thenReturn(mockTask());
		
		String gameCode = "gameCode";
		createGameFromLobby(gameCode, host, player);
		
		assertNotNull(gameService.getCurrentAnswerClass(gameCode, player));
	}
	@ParameterizedTest
	@MethodSource({"mockPlayerHostAndPlayer", "mockLecturerHostAndPlayer"})
	public void cannotGetAnswerTypeIfGameDoesNotExist(Account host, Account player)
	{
		String gameCode = "gameCode";
		createGameFromLobby(gameCode, host, player);
		String wrongGameCode = gameCode + "wrong";
		
		assertNull(gameService.getCurrentAnswerClass(wrongGameCode, player));
	}
	@ParameterizedTest
	@MethodSource({"mockPlayerHostAndTwoPlayers", "mockLecturerHostAndTwoPlayers"})
	public void cannotGetAnswerTypeIfGameFinished(
			Account host, Account player, Account otherPlayer)
	{
		when(accountService.getAuthenticatedAccount())
			.thenReturn(Optional.of(player));
		when(gtdService.generateRandomTask(anyDouble()))
			.thenReturn(mockTask());
		
		String gameCode = "gameCode";
		createGameFromLobby(gameCode, host, player, otherPlayer);
		
		int taskCount = gameService.getTaskCount(gameCode);
		IntStream.range(0, taskCount)
			.forEach(i ->
				gameService.acceptAnswer(gameCode,
						mock(gameService.getCurrentAnswerClass(
								gameCode, player),
								inv ->
									isCollectionOrMap(inv.getMethod()
											.getReturnType())?
									null:RETURNS_DEFAULTS.answer(inv))));
		
		assertNull(gameService.getCurrentAnswerClass(gameCode, player));
	}
	@ParameterizedTest
	@MethodSource({"mockPlayerHostAndTwoPlayers", "mockLecturerHostAndTwoPlayers"})
	public void canAcceptAnswer(Account host, Account player, Account otherPlayer)
	{
		when(accountService.getAuthenticatedAccount())
			.thenReturn(Optional.of(player));
		when(gtdService.generateRandomTask(anyDouble()))
			.thenReturn(mockTask());
		
		String gameCode = "gameCode";
		createGameFromLobby(gameCode, host, player, otherPlayer);
		
		boolean success = gameService.acceptAnswer(gameCode,
				mock(gameService.getCurrentAnswerClass(
						gameCode, player),
						inv ->
							isCollectionOrMap(inv.getMethod()
									.getReturnType())?
							null:RETURNS_DEFAULTS.answer(inv)));
		
		assertTrue(success);
	}
	@ParameterizedTest
	@MethodSource({"mockPlayerHostAndTwoPlayers", "mockLecturerHostAndTwoPlayers"})
	public void cannotAcceptAnswerIfGameDoesNotExist(
			Account host, Account player, Account otherPlayer)
	{
		when(accountService.getAuthenticatedAccount())
			.thenReturn(Optional.of(player));
		when(gtdService.generateRandomTask(anyDouble()))
			.thenReturn(mockTask());
		
		String gameCode = "gameCode";
		createGameFromLobby(gameCode, host, player, otherPlayer);
		String wrongGameCode = gameCode + "wrong";
		
		boolean success = gameService.acceptAnswer(wrongGameCode,
				mock(gameService.getCurrentAnswerClass(
						gameCode, player),
						inv ->
							isCollectionOrMap(inv.getMethod()
									.getReturnType())?
							null:RETURNS_DEFAULTS.answer(inv)));
		
		assertFalse(success);
	}
	
	@ParameterizedTest
	@MethodSource({"mockPlayerHostAndTwoPlayers", "mockLecturerHostAndTwoPlayers"})
	public void cannotAcceptAnswerIfGameFinished(
			Account host, Account player, Account otherPlayer)
	{
		when(accountService.getAuthenticatedAccount())
			.thenReturn(Optional.of(player));
		when(gtdService.generateRandomTask(anyDouble()))
			.thenReturn(mockTask());
		
		String gameCode = "gameCode";
		createGameFromLobby(gameCode, host, player, otherPlayer);
		
		int taskCount = gameService.getTaskCount(gameCode);
		IntStream.range(0, taskCount)
			.forEach(i ->
				gameService.acceptAnswer(gameCode,
						mock(gameService.getCurrentAnswerClass(
								gameCode, player),
								inv ->
									isCollectionOrMap(inv.getMethod()
											.getReturnType())?
									null:RETURNS_DEFAULTS.answer(inv))));
		
		boolean success = gameService.acceptAnswer(gameCode,
				mock(WordFillAnswer.class));
		
		assertFalse(success);
	}
	@ParameterizedTest
	@MethodSource({"mockPlayerHostAndTwoPlayers", "mockLecturerHostAndTwoPlayers"})
	public void cannotAcceptNullAnswer(
			Account host, Account player, Account otherPlayer)
	{
		when(accountService.getAuthenticatedAccount())
			.thenReturn(Optional.of(player));
		when(gtdService.generateRandomTask(anyDouble()))
			.thenReturn(mockTask());
		
		String gameCode = "gameCode";
		createGameFromLobby(gameCode, host, player, otherPlayer);
		
		assertThrows(NullPointerException.class,
				() -> gameService.acceptAnswer(gameCode, (JsonNode) null));
	}
	
	@ParameterizedTest
	@MethodSource({"mockPlayerHostAndTwoPlayers", "mockLecturerHostAndTwoPlayers"})
	public void firstCallToHaveResultsChangedReturnsTrue(
			Account host, Account player, Account otherPlayer)
	{
		String gameCode = "gameCode";
		createGameFromLobby(gameCode, host, player, otherPlayer);
		when(accountService.getAuthenticatedAccount())
			.thenReturn(Optional.of(player));
		UUID gameID = gameService.getGameID(gameCode);
		
		Optional<Boolean> ret = gameService.haveResultsChanged(gameID);
		assertTrue(ret.isPresent());
		assertTrue(ret.get());
	}
	@ParameterizedTest
	@MethodSource({"mockPlayerHostAndTwoPlayers", "mockLecturerHostAndTwoPlayers"})
	public void returnsFalseIfResultsDidNotChange(
			Account host, Account player, Account otherPlayer)
	{
		String gameCode = "gameCode";
		createGameFromLobby(gameCode, host, player, otherPlayer);
		when(accountService.getAuthenticatedAccount())
			.thenReturn(Optional.of(player));
		UUID gameID = gameService.getGameID(gameCode);
		
		gameService.haveResultsChanged(gameID);
		
		Optional<Boolean> ret = gameService.haveResultsChanged(gameID);
		assertTrue(ret.isPresent());
		assertFalse(ret.get());
	}
	@ParameterizedTest
	@MethodSource({"mockPlayerHostAndTwoPlayers", "mockLecturerHostAndTwoPlayers"})
	public void returnsTrueIfResultsDidChange(
			Account host, Account player, Account otherPlayer)
	{
		when(accountService.getAuthenticatedAccount())
			.thenReturn(Optional.of(player));
		when(gtdService.generateRandomTask(anyDouble()))
			.thenReturn(mockTask());
		
		String gameCode = "gameCode";
		createGameFromLobby(gameCode, host, player, otherPlayer);
		UUID gameID = gameService.getGameID(gameCode);
		
		gameService.haveResultsChanged(gameID);
		
		gameService.acceptAnswer(gameCode,
				mock(gameService.getCurrentAnswerClass(
						gameCode, player),
						inv ->
							isCollectionOrMap(inv.getMethod()
									.getReturnType())?
							null:RETURNS_DEFAULTS.answer(inv)));
		
		Optional<Boolean> ret = gameService.haveResultsChanged(gameID);
		assertTrue(ret.isPresent());
		assertTrue(ret.get());
	}
	@ParameterizedTest
	@MethodSource({"mockPlayerHostAndTwoPlayers", "mockLecturerHostAndTwoPlayers"})
	public void cannotCheckIfResultsChangedIfGameDoesNotExist(
			Account host, Account player, Account otherPlayer)
	{
		String gameCode = "gameCode";
		createGameFromLobby(gameCode, host, player, otherPlayer);
		when(accountService.getAuthenticatedAccount())
			.thenReturn(Optional.of(player));
		UUID gameID = UUID.randomUUID();
		
		assertTrue(gameService.haveResultsChanged(gameID).isEmpty());
	}
	@ParameterizedTest
	@MethodSource({"mockPlayerHostAndTwoPlayers", "mockLecturerHostAndTwoPlayers"})
	public void cannotCheckIfResultsChangedIfNotAuthenticated(
			Account host, Account player, Account otherPlayer)
	{
		String gameCode = "gameCode";
		createGameFromLobby(gameCode, host, player, otherPlayer);
		UUID gameID = gameService.getGameID(gameCode);
		
		assertThrows(IllegalArgumentException.class,
				() -> gameService.haveResultsChanged(gameID));
	}
	
	@ParameterizedTest
	@MethodSource("mockPlayerHostAndTwoPlayers")
	public void shouldNotRemoveActivePlayersWithPlayerHost(
			Account host, Account player, Account otherPlayer)
	{
		String gameCode = "gameCode";
		createGameFromLobby(gameCode, host, player, otherPlayer);
		
		gameService.removeInactive();

		assertTrue(gameService.gameExists(gameCode));
		assertTrue(gameService.isPlayerActive(gameCode, host));
		assertTrue(gameService.isPlayerActive(gameCode, player));
		assertTrue(gameService.isPlayerActive(gameCode, otherPlayer));
	}
	@ParameterizedTest
	@MethodSource("mockLecturerHostAndTwoPlayers")
	public void shouldNotRemoveActivePlayersWithLecturerHost(
			Account host, Account player, Account otherPlayer)
	{
		String gameCode = "gameCode";
		createGameFromLobby(gameCode, host, player, otherPlayer);
		
		gameService.removeInactive();

		assertTrue(gameService.gameExists(gameCode));
		assertTrue(gameService.isPlayerActive(gameCode, player));
		assertTrue(gameService.isPlayerActive(gameCode, otherPlayer));
	}
	@ParameterizedTest
	@MethodSource("mockPlayerHostAndTwoPlayers")
	public void shouldRemoveInactivePlayersWithPlayerHost(
			Account host, Account player, Account otherPlayer)
	{
		String gameCode = "gameCode";
		createGameFromLobby(gameCode, host, player, otherPlayer);
		
		gameService.markInactive(gameCode, player);
		
		gameService.removeInactive();

		assertTrue(gameService.gameExists(gameCode));
		assertTrue(gameService.isPlayerActive(gameCode, host));
		assertFalse(gameService.isPlayerActive(gameCode, player));
		assertTrue(gameService.isPlayerActive(gameCode, otherPlayer));
	}
	@ParameterizedTest
	@MethodSource("mockLecturerHostAndTwoPlayers")
	public void shouldRemoveInactivePlayersWithLecturerHost(
			Account host, Account player, Account otherPlayer)
	{
		String gameCode = "gameCode";
		createGameFromLobby(gameCode, host, player, otherPlayer);
		
		gameService.markInactive(gameCode, player);
		
		gameService.removeInactive();
		
		assertTrue(gameService.gameExists(gameCode));
		assertFalse(gameService.isPlayerActive(gameCode, player));
		assertTrue(gameService.isPlayerActive(gameCode, otherPlayer));
	}
	@ParameterizedTest
	@MethodSource("mockPlayerHostAndTwoPlayers")
	public void shouldDeleteGameWhenAllInactiveWithPlayerHost(
			Account host, Account player, Account otherPlayer)
	{
		String gameCode = "gameCode";
		createGameFromLobby(gameCode, host, player, otherPlayer);
		
		gameService.markInactive(gameCode, host);
		gameService.markInactive(gameCode, player);
		gameService.markInactive(gameCode, otherPlayer);
		
		gameService.removeInactive();
		
		assertFalse(gameService.gameExists(gameCode));
	}
	@ParameterizedTest
	@MethodSource("mockLecturerHostAndTwoPlayers")
	public void shouldDeleteGameWhenAllInactiveWithLecturerHost(
			Account host, Account player, Account otherPlayer)
	{
		String gameCode = "gameCode";
		createGameFromLobby(gameCode, host, player, otherPlayer);
		
		gameService.markInactive(gameCode, player);
		gameService.markInactive(gameCode, otherPlayer);
		
		gameService.removeInactive();
		
		assertFalse(gameService.gameExists(gameCode));
	}
	@ParameterizedTest
	@MethodSource("mockPlayerHostAndTwoPlayers")
	public void shouldRemoveInactiveHost(
			Account host, Account player, Account otherPlayer)
	{
		String gameCode = "gameCode";
		createGameFromLobby(gameCode, host, player, otherPlayer);
		
		gameService.markInactive(gameCode, host);
		
		gameService.removeInactive();

		assertTrue(gameService.gameExists(gameCode));
		assertFalse(gameService.isPlayerActive(gameCode, host));
		assertTrue(gameService.isPlayerActive(gameCode, player));
		assertTrue(gameService.isPlayerActive(gameCode, otherPlayer));
	}
	@ParameterizedTest
	@MethodSource("mockPlayerHostAndTwoPlayers")
	public void shouldNotRemovePlayerWhenActiveAgainWithPlayerHost(
			Account host, Account player, Account otherPlayer)
	{
		String gameCode = "gameCode";
		createGameFromLobby(gameCode, host, player, otherPlayer);
		
		gameService.markInactive(gameCode, player);
		gameService.noteInteraction(gameCode, player);
		
		gameService.removeInactive();

		assertTrue(gameService.gameExists(gameCode));
		assertTrue(gameService.isPlayerActive(gameCode, host));
		assertTrue(gameService.isPlayerActive(gameCode, player));
		assertTrue(gameService.isPlayerActive(gameCode, otherPlayer));
	}
	@ParameterizedTest
	@MethodSource("mockLecturerHostAndTwoPlayers")
	public void shouldNotRemovePlayerWhenActiveAgainWithLecturerHost(
			Account host, Account player, Account otherPlayer)
	{
		String gameCode = "gameCode";
		createGameFromLobby(gameCode, host, player, otherPlayer);
		
		gameService.markInactive(gameCode, player);
		gameService.noteInteraction(gameCode, player);
		
		gameService.removeInactive();
		
		assertTrue(gameService.gameExists(gameCode));
		assertTrue(gameService.isPlayerActive(gameCode, player));
		assertTrue(gameService.isPlayerActive(gameCode, otherPlayer));
	}
	
	@ParameterizedTest
	@MethodSource({"mockPlayerHostAndTwoPlayers", "mockLecturerHostAndTwoPlayers"})
	public void canNoteInteractionWithAuthenticatedAccount(
			Account host, Account player, Account otherPlayer)
	{
		String gameCode = "gameCode";
		createGameFromLobby(gameCode, host, player, otherPlayer);
		
		when(accountService.getAuthenticatedAccount())
			.thenReturn(Optional.of(host));
		assertDoesNotThrow(() -> gameService.noteInteraction(gameCode));
		when(accountService.getAuthenticatedAccount())
			.thenReturn(Optional.of(player));
		assertDoesNotThrow(() -> gameService.noteInteraction(gameCode));
		when(accountService.getAuthenticatedAccount())
			.thenReturn(Optional.of(otherPlayer));
		assertDoesNotThrow(() -> gameService.noteInteraction(gameCode));
	}
	
	@ParameterizedTest
	@MethodSource({"mockPlayerHostAndTwoPlayers", "mockLecturerHostAndTwoPlayers"})
	public void noActivePlayerWhenGameDoesNotExist(
			Account host, Account player, Account otherPlayer)
	{
		String gameCode = "gameCode";
		createGameFromLobby(gameCode, host, player, otherPlayer);
		String wrongGameCode = gameCode + "wrong";
		
		assertFalse(gameService.isPlayerActive(wrongGameCode, host));
		assertFalse(gameService.isPlayerActive(wrongGameCode, player));
		assertFalse(gameService.isPlayerActive(wrongGameCode, otherPlayer));
	}
	@ParameterizedTest
	@MethodSource({"mockPlayerHostAndTwoPlayers", "mockLecturerHostAndTwoPlayers"})
	public void shouldNotMarkPlayersAsActiveWhenGameDoesNotExist(
			Account host, Account player, Account otherPlayer)
	{
		String gameCode = "gameCode";
		createGameFromLobby(gameCode, host, player, otherPlayer);
		String wrongGameCode = gameCode + "wrong";
		
		gameService.noteInteraction(wrongGameCode, host);
		gameService.noteInteraction(wrongGameCode, player);
		gameService.noteInteraction(wrongGameCode, otherPlayer);
		
		assertFalse(gameService.isPlayerActive(wrongGameCode, host));
		assertFalse(gameService.isPlayerActive(wrongGameCode, player));
		assertFalse(gameService.isPlayerActive(wrongGameCode, otherPlayer));
	}
	@ParameterizedTest
	@MethodSource({"mockPlayerHostAndTwoPlayers", "mockLecturerHostAndTwoPlayers"})
	public void shouldNotThrowOnMarkingPlayersAsInactiveWhenGameDoesNotExist(
			Account host, Account player, Account otherPlayer)
	{
		String gameCode = "gameCode";
		createGameFromLobby(gameCode, host, player, otherPlayer);
		String wrongGameCode = gameCode + "wrong";
		
		assertDoesNotThrow(() -> gameService.markInactive(wrongGameCode, host));
		assertDoesNotThrow(() -> gameService.markInactive(wrongGameCode, player));
		assertDoesNotThrow(() -> gameService.markInactive(wrongGameCode, otherPlayer));
	}
	
	@ParameterizedTest
	@MethodSource("mockPlayerHost")
	public void shouldGetGameHistory(Account player)
	{
		UUID gameID = UUID.randomUUID();
		when(grDAO.findAllByResults_PlayerID(eq(player.getId()), any()))
			.thenReturn(new PageImpl<>(List.of(new GameResult(gameID))));
		
		Page<Map<String, String>> res = gameService.getHistory(1, player);
		
		assertEquals(res.getNumberOfElements(), 1);
		assertEquals(res.getTotalElements(), 1);
		assertEquals(res.getContent().get(0).get("id"), gameID.toString());
	}
	@ParameterizedTest
	@MethodSource("mockPlayerHost")
	public void shouldGetGameHistoryWithAuthenticatedAccount(Account player)
	{
		UUID gameID = UUID.randomUUID();
		when(accountService.getAuthenticatedAccount())
			.thenReturn(Optional.of(player));
		when(grDAO.findAllByResults_PlayerID(eq(player.getId()), any()))
			.thenReturn(new PageImpl<>(List.of(new GameResult(gameID))));
		
		Page<Map<String, String>> res = gameService.getHistory(1);
		
		assertEquals(res.getNumberOfElements(), 1);
		assertEquals(res.getTotalElements(), 1);
		assertEquals(res.getContent().get(0).get("id"), gameID.toString());
	}
	
	@ParameterizedTest
	@MethodSource("mockLecturerHost")
	public void shouldReturnEmptyWhenAccountIsNotAPlayer(Account acc)
	{
		when(pdService.getPlayerData(acc)).thenReturn(Optional.empty());
		
		assertTrue(gameService.getRating(acc).isEmpty());
	}
	@ParameterizedTest
	@MethodSource("mockLecturerHost")
	public void shouldReturnEmptyWhenAccountIsNotAPlayerWithAuthenticatedAccount(Account acc)
	{
		when(accountService.getAuthenticatedAccount())
			.thenReturn(Optional.of(acc));
		when(pdService.getPlayerData(acc)).thenReturn(Optional.empty());
		
		assertTrue(gameService.getRating().isEmpty());
	}
	
	@ParameterizedTest
	@MethodSource("mockPlayerHost")
	public void shouldReturnPlayerData(Account acc)
	{
		when(pdService.getPlayerData(acc)).thenReturn(Optional.of(new PlayerData()));
		
		var ret = gameService.getRating(acc);
		assertTrue(ret.isPresent());
		assertNotNull(ret.orElse(null));
	}
	@ParameterizedTest
	@MethodSource("mockPlayerHost")
	public void shouldReturnPlayerDataWithAuthenticatedAccount(Account acc)
	{
		when(accountService.getAuthenticatedAccount())
			.thenReturn(Optional.of(acc));
		when(pdService.getPlayerData(acc)).thenReturn(Optional.of(new PlayerData()));
		
		var ret = gameService.getRating();
		assertTrue(ret.isPresent());
		assertNotNull(ret.orElse(null));
	}
	
	//---Sources---
	
	public static List<Arguments> mockPlayerHost()
	{
		return List.of(Arguments.of(mockHost(PLAYER_ROLE)));
	}
	public static List<Arguments> mockLecturerHost()
	{
		return List.of(Arguments.of(mockHost(LECTURER_ROLE)));
	}
	public static List<Arguments> mockPlayerHostAndPlayer()
	{
		Account host = mockHost(PLAYER_ROLE);
		Account player = mockPlayer("Player");
		return List.of(Arguments.of(host, player));
	}
	public static List<Arguments> mockLecturerHostAndPlayer()
	{
		Account host = mockHost(LECTURER_ROLE);
		Account player = mockPlayer("Player");
		return List.of(Arguments.of(host, player));
	}
	public static List<Arguments> mockTwoHostsAndPlayer()
	{
		Account[] primaryHosts = 
		{
			mockHost(PLAYER_ROLE),
			mockHost(LECTURER_ROLE)
		};
		Account[] secondaryHosts = 
		{
			mockSecondHost(PLAYER_ROLE),
			mockSecondHost(LECTURER_ROLE)
		};
		Account player = mockPlayer("Player");
		
		List<Arguments> ret = new ArrayList<>();
		for (Account host: primaryHosts)
			for (Account otherHost: secondaryHosts)
				ret.add(Arguments.of(host, otherHost, player));
		return ret;
	}
	public static List<Arguments> mockPlayerHostAndTwoPlayers()
	{
		Account host = mockHost(PLAYER_ROLE);
		Account player1 = mockPlayer("Player1");
		Account player2 = mockPlayer("Player2");
		return List.of(Arguments.of(host, player1, player2));
	}
	public static List<Arguments> mockLecturerHostAndTwoPlayers()
	{
		Account host = mockHost(LECTURER_ROLE);
		Account player1 = mockPlayer("Player1");
		Account player2 = mockPlayer("Player2");
		return List.of(Arguments.of(host, player1, player2));
	}
	
	//---Helpers---
	
	private static Account mockHost(String role)
	{
		return new Account.Builder()
				.withID(UUID.randomUUID())
				.withEmail("testHost@test.pl")
				.withUsername("TestHost")
				.withPassword("QWERTY")
				.withRoles(List.of(role))
				.build();
	}
	private static Account mockSecondHost(String role)
	{
		return new Account.Builder()
				.withID(UUID.randomUUID())
				.withEmail("testHost2@test.pl")
				.withUsername("TestHost2")
				.withPassword("QWERTY")
				.withRoles(List.of(role))
				.build();
	}
	public static Account mockPlayer(String name)
	{
		return new Account.Builder()
				.withID(UUID.randomUUID())
				.withEmail("test" + name + "@test.pl")
				.withUsername("Test" + name)
				.withPassword("QWERTY" + name)
				.withRoles(List.of(PLAYER_ROLE))
				.build();
	}
	private static List<Account> mockPlayers(int amount)
	{
		return IntStream.rangeClosed(0, amount)
				.mapToObj(i -> mockPlayer("Player" + i))
				.collect(Collectors.toList());
	}
	private boolean isCollectionOrMap(Class<?> clazz)
	{
		return Collection.class.isAssignableFrom(clazz)
				|| Map.class.isAssignableFrom(clazz);
	}
	
	public int createGameFromLobby(String gameCode, Account host, Account... playerArr)
	{
		List<Account> players = new ArrayList<>();
		players.add(host);
		for (Account acc: playerArr)
			players.add(acc);
		players = players
				.stream()
				.filter(a -> a.hasRole(PLAYER_ROLE))
				.collect(Collectors.toList());
		when(lobbyService.lobbyExists(gameCode)).thenReturn(true);
		when(lobbyService.getPlayers(gameCode)).thenReturn(players);
		when(lobbyService.getHost(gameCode)).thenReturn(host);
		when(lobbyService.isHost(gameCode, host)).thenReturn(true);
		when(lobbyService.deleteLobby(gameCode, host)).thenReturn(true);
		
		assertTrue(gameService.createGameFromLobby(gameCode, host));
		
		return players.size();
	}
	private Task mockTask()
	{
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

		return new WordConnect(UUID.randomUUID(),
				"Test instruction", List.of(),
				leftWords1, rightWords1, correctMapping1, 100);
	}
}
