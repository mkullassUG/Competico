package com.projteam.app.service;

import static com.projteam.app.domain.Account.LECTURER_ROLE;
import static com.projteam.app.domain.Account.PLAYER_ROLE;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import com.projteam.app.domain.Account;

@RunWith(MockitoJUnitRunner.class)
public class LobbyServiceTests
{
	private @Mock AccountService accountService;
	
	private @InjectMocks LobbyService lobbyService;
	
	@BeforeEach
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void contextLoads()
	{
		assertNotNull(lobbyService);
	}
	
	@ParameterizedTest
	@MethodSource("mockHosts")
	public void canCreateLobbies(Account host)
	{
		String gameCode = lobbyService.createLobby(host);
		assertFalse(gameCode.isEmpty());
		assertTrue(lobbyService.lobbyExists(gameCode));
		assertTrue(lobbyService.getLobbyCount() > 0);
	}
	@ParameterizedTest
	@MethodSource("mockHosts")
	public void canCreateLobbiesWithAuthenticatedAccount(Account host)
	{
		when(accountService.getAuthenticatedAccount()).thenReturn(
				Optional.of(host));
		
		String gameCode = lobbyService.createLobby();
		assertFalse(gameCode.isEmpty());
		assertTrue(lobbyService.lobbyExists(gameCode));
		assertTrue(lobbyService.getLobbyCount() > 0);
	}
	@Test
	public void cannotCreateLobbiesWhenUnauthenticated()
	{
		when(accountService.getAuthenticatedAccount()).thenReturn(
				Optional.empty());
		
		assertThrows(IllegalArgumentException.class, () -> lobbyService.createLobby());
	}
	@ParameterizedTest
	@MethodSource("mockHosts")
	public void createdLobbyHasCorrectHost(Account host)
	{
		when(accountService.getAuthenticatedAccount()).thenReturn(
				Optional.of(host));
		
		String gameCode = lobbyService.createLobby(host);
		
		assertTrue(lobbyService.isHost(gameCode, host));
		assertTrue(lobbyService.isHost(gameCode));
		assertTrue(lobbyService.getHost(gameCode).equals(host));
	}
	@Test
	public void hostPlayerAppearsInList()
	{
		Account host = new Account.Builder()
				.withEmail("testHost@test.pl")
				.withUsername("TestHost")
				.withPasswordHash("QWERTY")
				.withRoles(List.of(PLAYER_ROLE))
				.build();
		String gameCode = lobbyService.createLobby(host);
		
		assertTrue(lobbyService.getPlayers(gameCode).contains(host));
	}
	@Test
	public void hostLecturerDoesNotAppearInList()
	{
		Account host = new Account.Builder()
				.withEmail("testHost@test.pl")
				.withUsername("TestHost")
				.withPasswordHash("QWERTY")
				.withRoles(List.of(LECTURER_ROLE))
				.build();
		String gameCode = lobbyService.createLobby(host);
		
		assertFalse(lobbyService.getPlayers(gameCode).contains(host));
	}
	@Test
	public void gameCodeHasCorrectLength()
	{
		assertEquals(lobbyService
				.createLobby(new Account())
				.length(), lobbyService.getGameCodeLength());
	}
	@Test
	public void gameCodeContainsCorrectChars()
	{
		assertTrue(lobbyService
				.createLobby(new Account())
				.chars()
				.allMatch(Character::isLetterOrDigit));
	}
	@ParameterizedTest
	@MethodSource("mockHosts")
	public void gameHostIsAssignedCorrectly(Account host)
	{
		String gameCode = lobbyService.createLobby(host);
		assertTrue(lobbyService.isHost(gameCode, host));
	}
	@ParameterizedTest
	@MethodSource("mockHosts")
	public void cannotCheckIfGameHostWithoutAccount(Account host)
	{
		when(accountService.getAuthenticatedAccount())
			.thenReturn(Optional.empty());
		
		String gameCode = lobbyService.createLobby(host);
		assertThrows(IllegalArgumentException.class, () -> lobbyService.isHost(gameCode));
	}
	@ParameterizedTest
	@MethodSource({"mockPlayerHostAndPlayers", "mockLecturerHostAndPlayers"})
	public void addedPlayersAppearInPlayerList(Account mockHost,
			List<Account> mockPlayers)
	{
		String gameCode = lobbyService.createLobby(mockHost);
		int maxPlayerCount = lobbyService.getMaximumPlayerCount(gameCode)
				- (mockHost.hasRole(PLAYER_ROLE)?1:0);
		
		mockPlayers.stream()
			.limit(maxPlayerCount)
			.forEach(player -> lobbyService.addPlayer(gameCode, player));
		
		assertTrue(lobbyService.getPlayers(gameCode)
				.containsAll(mockPlayers.stream()
						.limit(maxPlayerCount)
						.collect(Collectors.toList())));
	}
	@ParameterizedTest
	@MethodSource({"mockPlayerHostAndPlayers", "mockLecturerHostAndPlayers"})
	public void removedPlayersDoNotAppearInPlayerList_sequential(Account mockHost,
			List<Account> mockPlayers)
	{
		String gameCode = lobbyService.createLobby(mockHost);
		int maxPlayerCount = lobbyService.getMaximumPlayerCount(gameCode)
				- (mockHost.hasRole(PLAYER_ROLE)?1:0);
		
		mockPlayers.stream()
			.limit(maxPlayerCount)
			.forEach(player -> assertTrue(lobbyService.addPlayer(gameCode, player)));
		mockPlayers.stream()
			.limit(maxPlayerCount)
			.forEach(player -> assertTrue(lobbyService.removePlayer(gameCode, player)));
		mockPlayers.stream()
			.limit(maxPlayerCount)
			.forEach(player -> assertFalse(lobbyService
					.getPlayers(gameCode)
					.contains(player)));
	}
	@ParameterizedTest
	@MethodSource({"mockPlayerHostAndPlayers", "mockLecturerHostAndPlayers"})
	public void removedPlayersDoNotAppearInPlayerList_isolated(Account mockHost,
			List<Account> mockPlayers)
	{
		String gameCode = lobbyService.createLobby(mockHost);
		mockPlayers.stream()
			.limit(lobbyService.getMaximumPlayerCount(gameCode)
					- (mockHost.hasRole(PLAYER_ROLE)?1:0))
			.forEach(player -> 
			{
				assertTrue(lobbyService.addPlayer(gameCode, player));
				assertTrue(lobbyService.removePlayer(gameCode, player));
				assertFalse(lobbyService
						.getPlayers(gameCode)
						.contains(player));
			});
	}
	
	@ParameterizedTest
	@MethodSource({"mockPlayerHostAndPlayer", "mockLecturerHostAndPlayer"})
	public void playerNotAddedAbovePlayerLimit(Account mockHost, Account mockPlayer)
	{
		String gameCode = lobbyService.createLobby(mockHost);
		int maximumPlayerCount = lobbyService.getMaximumPlayerCount(gameCode)
				- (mockHost.hasRole(PLAYER_ROLE)?1:0);
		List<Account> mockPlayers = mockPlayers(maximumPlayerCount);
		
		mockPlayers.stream()
				.limit(maximumPlayerCount)
				.forEach(player -> assertTrue(lobbyService.addPlayer(gameCode, player)));
		
		assertTrue(lobbyService.isLobbyFull(gameCode));
		assertFalse(lobbyService.addPlayer(gameCode, mockPlayer));
		assertFalse(lobbyService.getPlayers(gameCode).contains(mockPlayer));
	}
	
	@ParameterizedTest
	@MethodSource("mockHosts")
	public void cannotAddPlayerToInexistantLobby(Account player)
	{
		assertFalse(lobbyService.addPlayer("wrongCode", player));
	}
	
	@ParameterizedTest
	@MethodSource("mockHosts")
	public void canDeleteOwnLobbies(Account host)
	{
		String gameCode = lobbyService.createLobby(host);

		assertTrue(lobbyService.deleteLobby(gameCode, host));
	}
	@ParameterizedTest
	@MethodSource("mockHosts")
	public void cannotDeleteOtherLobbies(Account host)
	{
		String gameCode = lobbyService.createLobby(host);
		Account otherAccount = new Account();
		
		assertFalse(lobbyService.deleteLobby(gameCode, otherAccount));
	}
	@ParameterizedTest
	@MethodSource("mockHosts")
	public void cannotDeleteInexistantLobbies(Account host)
	{
		String gameCode = lobbyService.createLobby(host);
		String wrongGameCode = gameCode + "wrong";
		
		assertFalse(lobbyService.deleteLobby(wrongGameCode, host));
	}

	@ParameterizedTest
	@MethodSource("mockHosts")
	public void canAddAuthenticatedPlayer(Account host)
	{
		String gameCode = lobbyService.createLobby(host);
		Account player = new Account.Builder()
				.withRoles(List.of(PLAYER_ROLE))
				.build();
		
		when(accountService.getAuthenticatedAccount()).thenReturn(Optional.of(player));
		
		assertTrue(lobbyService.addPlayer(gameCode));
		assertTrue(lobbyService.getPlayers(gameCode).contains(player));
	}
	@ParameterizedTest
	@MethodSource("mockHosts")
	public void cannotAddUnauthenticatedPlayer(Account host)
	{
		String gameCode = lobbyService.createLobby(host);
		Account player = new Account.Builder()
				.withRoles(List.of(PLAYER_ROLE))
				.build();
		
		when(accountService.getAuthenticatedAccount()).thenReturn(Optional.empty());
		
		assertThrows(IllegalArgumentException.class, () -> lobbyService.addPlayer(gameCode));
		assertFalse(lobbyService.getPlayers(gameCode).contains(player));
	}
	@ParameterizedTest
	@MethodSource("mockHosts")
	public void canRemoveAuthenticatedPlayer(Account host)
	{
		String gameCode = lobbyService.createLobby(host);
		Account player = new Account.Builder()
				.withRoles(List.of(PLAYER_ROLE))
				.build();
		
		when(accountService.getAuthenticatedAccount()).thenReturn(Optional.of(player));
		
		lobbyService.addPlayer(gameCode, player);
		
		assertTrue(lobbyService.removePlayer(gameCode));
		assertFalse(lobbyService.getPlayers(gameCode).contains(player));
	}
	@ParameterizedTest
	@MethodSource("mockHosts")
	public void cannotRemoveUnauthenticatedPlayer(Account host)
	{
		String gameCode = lobbyService.createLobby(host);
		Account player = new Account.Builder()
				.withRoles(List.of(PLAYER_ROLE))
				.build();
		
		when(accountService.getAuthenticatedAccount()).thenReturn(Optional.empty());
		
		lobbyService.addPlayer(gameCode, player);
		
		assertThrows(IllegalArgumentException.class, () -> lobbyService.removePlayer(gameCode));
		assertTrue(lobbyService.getPlayers(gameCode).contains(player));
	}
	
	@ParameterizedTest
	@MethodSource("mockHosts")
	public void canRemoveOtherPlayerAsHost(Account host)
	{
		String gameCode = lobbyService.createLobby(host);
		Account player = new Account.Builder()
				.withRoles(List.of(PLAYER_ROLE))
				.build();
		lobbyService.addPlayer(gameCode, player);
		
		assertTrue(lobbyService.removePlayer(gameCode, host, player));
		assertFalse(lobbyService.getPlayers(gameCode).contains(player));
	}
	@ParameterizedTest
	@MethodSource("mockHosts")
	public void cannotRemoveOtherPlayerWhenNotHost(Account host)
	{
		String gameCode = lobbyService.createLobby(host);
		Account player = new Account.Builder()
				.withEmail("testPlayer1@test.pl")
				.withUsername("testPlayer1")
				.withRoles(List.of(PLAYER_ROLE))
				.build();
		Account otherPlayer = new Account.Builder()
				.withEmail("testPlayer2@test.pl")
				.withUsername("testPlayer2")
				.withRoles(List.of(PLAYER_ROLE))
				.build();
		lobbyService.addPlayer(gameCode, player);
		lobbyService.addPlayer(gameCode, otherPlayer);
		
		assertFalse(lobbyService.removePlayer(gameCode, otherPlayer, player));
		assertTrue(lobbyService.getPlayers(gameCode).contains(player));
		assertTrue(lobbyService.getPlayers(gameCode).contains(otherPlayer));
	}
	@ParameterizedTest
	@MethodSource("mockHosts")
	public void cannotRemoveOtherPlayerWhenNotInLobby(Account host)
	{
		String gameCode = lobbyService.createLobby(host);
		Account player = new Account.Builder()
				.withEmail("testPlayer1@test.pl")
				.withUsername("testPlayer1")
				.withRoles(List.of(PLAYER_ROLE))
				.build();
		Account otherPlayer = new Account.Builder()
				.withEmail("testPlayer2@test.pl")
				.withUsername("testPlayer2")
				.withRoles(List.of(PLAYER_ROLE))
				.build();
		lobbyService.addPlayer(gameCode, player);
		
		assertFalse(lobbyService.removePlayer(gameCode, otherPlayer, player));
		assertTrue(lobbyService.getPlayers(gameCode).contains(player));
		assertFalse(lobbyService.getPlayers(gameCode).contains(otherPlayer));
	}
	
	@ParameterizedTest
	@MethodSource("mockHosts")
	public void canGetRandomLobby(Account host)
	{
		String gameCode = lobbyService.createLobby(host);
		lobbyService.allowRandomPlayers(gameCode, true, host);
		
		assertTrue(lobbyService.getRandomAccessibleLobbyCount() > 0);
		assertNotNull(lobbyService.getRandomLobby());
	}
	@Test
	public void cannotGetRandomLobbyWhenNoneAvailable()
	{
		assertEquals(lobbyService.getRandomAccessibleLobbyCount(), 0);
		assertNull(lobbyService.getRandomLobby());
	}
	
	@ParameterizedTest
	@MethodSource({"mockPlayerHostAndPlayer", "mockLecturerHostAndPlayer"})
	public void canFindLobbyThePlayerIsIn(Account mockHost, Account mockPlayer)
	{
		String gameCode = lobbyService.createLobby(mockHost);
		lobbyService.addPlayer(gameCode, mockPlayer);
		
		assertEquals(lobbyService.getLobbyForPlayer(mockPlayer), gameCode);
	}
	@ParameterizedTest
	@MethodSource("mockHosts")
	public void cannotFindLobbyIfPlayerIsNotInOne(Account player)
	{
		assertNull(lobbyService.getLobbyForPlayer(player));
	}
	@ParameterizedTest
	@MethodSource("mockHosts")
	public void canSetAllowanceOfRandomPlayers(Account host)
	{
		String gameCode = lobbyService.createLobby(host);
		
		lobbyService.allowRandomPlayers(gameCode, true, host);
		assertTrue(lobbyService.allowsRandomPlayers(gameCode));
		
		lobbyService.allowRandomPlayers(gameCode, false, host);
		assertFalse(lobbyService.allowsRandomPlayers(gameCode));
	}
	@ParameterizedTest
	@MethodSource("mockHosts")
	public void newLobbyShouldNotBeFull(Account host)
	{
		String gameCode = lobbyService.createLobby(host);
		
		assertFalse(lobbyService.isLobbyFull(gameCode));
	}
	
	@Test
	public void shouldHandleNullGameCode()
	{
		assertFalse(lobbyService.addPlayer(null, null));
		assertFalse(lobbyService.removePlayer(null, null));
		assertFalse(lobbyService.removePlayer(null, null, null));
		assertNull(lobbyService.getPlayers(null));
		assertFalse(lobbyService.allowRandomPlayers(null, false, null));
		assertFalse(lobbyService.allowRandomPlayers(null, true, null));
		assertNull(lobbyService.getHost(null));
	}
	
	@ParameterizedTest
	@MethodSource("mockHosts")
	public void consecutiveCallsShouldNotHaveChanges(Account host)
	{
		String gameCode = lobbyService.createLobby(host);
		
		lobbyService.hasAnthingChanged(gameCode, host);
		assertFalse(lobbyService.hasAnthingChanged(gameCode, host));
		assertFalse(lobbyService.hasAnthingChanged(gameCode, host));
		assertFalse(lobbyService.hasAnthingChanged(gameCode, host));
	}
	@ParameterizedTest
	@MethodSource({"mockPlayerHostAndPlayer", "mockLecturerHostAndPlayer"})
	public void consecutiveCallsShouldNotHaveChanges(Account host, Account player)
	{
		String gameCode = lobbyService.createLobby(host);
		
		lobbyService.hasAnthingChanged(gameCode, host);
		assertFalse(lobbyService.hasAnthingChanged(gameCode, host));
		assertFalse(lobbyService.hasAnthingChanged(gameCode, host));
		assertFalse(lobbyService.hasAnthingChanged(gameCode, host));
	}
	@ParameterizedTest
	@MethodSource({"mockPlayerHostAndTwoPlayers", "mockLecturerHostAndTwoPlayers"})
	public void addingPlayerCausesRecordedChanges(Account host, Account player1, Account player2)
	{
		String gameCode = lobbyService.createLobby(host);
		
		lobbyService.addPlayer(gameCode, player1);
		lobbyService.hasAnthingChanged(gameCode, player1);
		lobbyService.addPlayer(gameCode, player2);
		assertTrue(lobbyService.hasAnthingChanged(gameCode, player1));
	}
	@ParameterizedTest
	@MethodSource({"mockPlayerHostAndTwoPlayers", "mockLecturerHostAndTwoPlayers"})
	public void removingPlayerCausesRecordedChanges(Account host, Account player1, Account player2)
	{
		String gameCode = lobbyService.createLobby(host);
		
		lobbyService.addPlayer(gameCode, player1);
		lobbyService.addPlayer(gameCode, player2);
		lobbyService.hasAnthingChanged(gameCode, player1);
		lobbyService.removePlayer(gameCode, player2);
		assertTrue(lobbyService.hasAnthingChanged(gameCode, player1));
	}
	@ParameterizedTest
	@MethodSource({"mockPlayerHostAndTwoPlayers", "mockLecturerHostAndTwoPlayers"})
	public void removingPlayerByHostCausesRecordedChanges(Account host, Account player1, Account player2)
	{
		String gameCode = lobbyService.createLobby(host);
		
		lobbyService.addPlayer(gameCode, player1);
		lobbyService.addPlayer(gameCode, player2);
		lobbyService.hasAnthingChanged(gameCode, player1);
		lobbyService.removePlayer(gameCode, host, player2);
		assertTrue(lobbyService.hasAnthingChanged(gameCode, player1));
	}
	
	//---Sources---
	
	public static List<Arguments> mockPlayerHostAndPlayers()
	{
		List<Arguments> ret = new ArrayList<>();
		Account host = mockHost(PLAYER_ROLE);
		
		IntStream.rangeClosed(0, 50)
			.forEach(max ->
			{
				List<Account> players = new ArrayList<>();
				players.addAll(
						IntStream.rangeClosed(0, max)
						.mapToObj(i -> mockPlayer("Player" + i))
						.collect(Collectors.toList()));
				ret.add(Arguments.of(host, players));
			});
		return ret;
	}
	public static List<Arguments> mockLecturerHostAndPlayers()
	{
		List<Arguments> ret = new ArrayList<>();
		Account host = mockHost(LECTURER_ROLE);
		
		IntStream.rangeClosed(0, 50)
			.forEach(max ->
			{
				List<Account> players = new ArrayList<>();
				players.addAll(
						IntStream.rangeClosed(0, max)
						.mapToObj(i -> mockPlayer("Player" + i))
						.collect(Collectors.toList()));
				ret.add(Arguments.of(host, players));
			});
		return ret;
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
	public static List<Arguments> mockHosts()
	{
		return List.of(
				Arguments.of(mockHost(PLAYER_ROLE)),
				Arguments.of(mockHost(LECTURER_ROLE)));
	}
	private static Account mockHost(String role)
	{
		return new Account.Builder()
				.withEmail("testHost@test.pl")
				.withUsername("TestHost")
				.withPasswordHash("QWERTY")
				.withRoles(List.of(role))
				.build();
	}
	private static Account mockPlayer(String name)
	{
		return new Account.Builder()
				.withEmail("test" + name + "@test.pl")
				.withUsername("Test" + name)
				.withPasswordHash("QWERTY" + name)
				.withRoles(List.of(PLAYER_ROLE))
				.build();
	}
	private static List<Account> mockPlayers(int amount)
	{
		return IntStream.rangeClosed(0, amount)
				.mapToObj(i -> mockPlayer("Player" + i))
				.collect(Collectors.toList());
	}
}
