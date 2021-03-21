package com.projteam.app.service.game;

import static com.projteam.app.domain.Account.LECTURER_ROLE;
import static com.projteam.app.domain.Account.PLAYER_ROLE;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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
import com.projteam.app.dto.lobby.LobbyOptionsDTO;
import com.projteam.app.service.AccountService;

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
				.withID(UUID.randomUUID())
				.withEmail("testHost@test.pl")
				.withUsername("TestHost")
				.withPassword("QWERTY")
				.withRoles(List.of(PLAYER_ROLE))
				.build();
		String gameCode = lobbyService.createLobby(host);
		
		assertTrue(lobbyService.getPlayers(gameCode).contains(host));
	}
	@Test
	public void hostLecturerDoesNotAppearInList()
	{
		Account host = new Account.Builder()
				.withID(UUID.randomUUID())
				.withEmail("testHost@test.pl")
				.withUsername("TestHost")
				.withPassword("QWERTY")
				.withRoles(List.of(LECTURER_ROLE))
				.build();
		String gameCode = lobbyService.createLobby(host);
		
		assertFalse(lobbyService.getPlayers(gameCode).contains(host));
	}
	@ParameterizedTest
	@MethodSource("mockHosts")
	public void gameCodeHasCorrectLength(Account host)
	{
		assertEquals(lobbyService
				.createLobby(host)
				.length(), lobbyService.getGameCodeLength());
	}
	@ParameterizedTest
	@MethodSource("mockHosts")
	public void gameCodeContainsCorrectChars(Account host)
	{
		assertTrue(lobbyService
				.createLobby(host)
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
	public void canDeleteOwnLobbiesWithAuthenticatedAccount(Account host)
	{
		String gameCode = lobbyService.createLobby(host);
		when(accountService.getAuthenticatedAccount())
			.thenReturn(Optional.of(host));
		
		assertTrue(lobbyService.deleteLobby(gameCode));
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
	public void cannotDeleteOtherLobbiesWithAuthenticatedAccount(Account host)
	{
		String gameCode = lobbyService.createLobby(host);
		Account otherAccount = new Account();
		when(accountService.getAuthenticatedAccount())
			.thenReturn(Optional.of(otherAccount));
		
		assertFalse(lobbyService.deleteLobby(gameCode));
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
	public void cannotDeleteInexistantLobbiesWithAuthenticatedAccount(Account host)
	{
		String gameCode = lobbyService.createLobby(host);
		String wrongGameCode = gameCode + "wrong";
		when(accountService.getAuthenticatedAccount())
			.thenReturn(Optional.of(host));
		
		assertFalse(lobbyService.deleteLobby(wrongGameCode));
	}

	@ParameterizedTest
	@MethodSource("mockHosts")
	public void canAddAuthenticatedPlayer(Account host)
	{
		String gameCode = lobbyService.createLobby(host);
		Account player = new Account.Builder()
				.withID(UUID.randomUUID())
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
				.withID(UUID.randomUUID())
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
				.withID(UUID.randomUUID())
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
				.withID(UUID.randomUUID())
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
				.withID(UUID.randomUUID())
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
				.withID(UUID.randomUUID())
				.withEmail("testPlayer1@test.pl")
				.withUsername("testPlayer1")
				.withRoles(List.of(PLAYER_ROLE))
				.build();
		Account otherPlayer = new Account.Builder()
				.withID(UUID.randomUUID())
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
				.withID(UUID.randomUUID())
				.withEmail("testPlayer1@test.pl")
				.withUsername("testPlayer1")
				.withRoles(List.of(PLAYER_ROLE))
				.build();
		Account otherPlayer = new Account.Builder()
				.withID(UUID.randomUUID())
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
		
		assertEquals(lobbyService.getLobbyForAccount(mockPlayer).orElse(null), gameCode);
	}
	@ParameterizedTest
	@MethodSource("mockHosts")
	public void cannotFindLobbyIfPlayerIsNotInOne(Account player)
	{
		assertTrue(lobbyService.getLobbyForAccount(player).isEmpty());
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
		
		lobbyService.hasAnythingChanged(gameCode, host);
		assertFalse(lobbyService.hasAnythingChanged(gameCode, host).orElse(false));
		assertFalse(lobbyService.hasAnythingChanged(gameCode, host).orElse(false));
		assertFalse(lobbyService.hasAnythingChanged(gameCode, host).orElse(false));
	}
	@ParameterizedTest
	@MethodSource({"mockPlayerHostAndPlayer", "mockLecturerHostAndPlayer"})
	public void consecutiveCallsShouldNotHaveChanges(Account host, Account player)
	{
		String gameCode = lobbyService.createLobby(host);
		
		lobbyService.hasAnythingChanged(gameCode, host);
		assertFalse(lobbyService.hasAnythingChanged(gameCode, host).orElse(false));
		assertFalse(lobbyService.hasAnythingChanged(gameCode, host).orElse(false));
		assertFalse(lobbyService.hasAnythingChanged(gameCode, host).orElse(false));
	}
	@ParameterizedTest
	@MethodSource({"mockPlayerHostAndTwoPlayers", "mockLecturerHostAndTwoPlayers"})
	public void addingPlayerCausesRecordedChanges(Account host, Account player1, Account player2)
	{
		String gameCode = lobbyService.createLobby(host);
		
		lobbyService.addPlayer(gameCode, player1);
		lobbyService.hasAnythingChanged(gameCode, player1);
		lobbyService.addPlayer(gameCode, player2);
		assertTrue(lobbyService.hasAnythingChanged(gameCode, player1).orElse(false));
	}
	@ParameterizedTest
	@MethodSource({"mockPlayerHostAndTwoPlayers", "mockLecturerHostAndTwoPlayers"})
	public void removingPlayerCausesRecordedChanges(Account host, Account player1, Account player2)
	{
		String gameCode = lobbyService.createLobby(host);
		
		lobbyService.addPlayer(gameCode, player1);
		lobbyService.addPlayer(gameCode, player2);
		lobbyService.hasAnythingChanged(gameCode, player1);
		lobbyService.removePlayer(gameCode, player2);
		assertTrue(lobbyService.hasAnythingChanged(gameCode, player1).orElse(false));
	}
	@ParameterizedTest
	@MethodSource({"mockPlayerHostAndTwoPlayers", "mockLecturerHostAndTwoPlayers"})
	public void removingPlayerByHostCausesRecordedChanges(Account host, Account player1, Account player2)
	{
		String gameCode = lobbyService.createLobby(host);
		
		lobbyService.addPlayer(gameCode, player1);
		lobbyService.addPlayer(gameCode, player2);
		lobbyService.hasAnythingChanged(gameCode, player1);
		lobbyService.removePlayer(gameCode, host, player2);
		assertTrue(lobbyService.hasAnythingChanged(gameCode, player1).orElse(false));
	}
	@ParameterizedTest
	@MethodSource("mockHostAndSettings")
	public void changingLobbySettingsAffectsReturnedValues(
			Account host, LobbyOptionsDTO options)
	{
		String gameCode = lobbyService.createLobby(host);
		
		lobbyService.updateOptions(gameCode, options, host);
		
		assertEquals(lobbyService.getMaximumPlayerCount(gameCode),
				options.getMaxPlayers());
		assertEquals(lobbyService.allowsRandomPlayers(gameCode),
				options.allowsRandomPlayers());
	}
	@ParameterizedTest
	@MethodSource("mockHostAndSettings")
	public void changingLobbySettingsAffectsReturnedValuesWithAuthenticatedAccount(
			Account host, LobbyOptionsDTO options)
	{
		String gameCode = lobbyService.createLobby(host);
		when(accountService.getAuthenticatedAccount())
			.thenReturn(Optional.of(host));
		
		boolean success = lobbyService.updateOptions(gameCode, options);
		
		assertTrue(success);
		assertEquals(lobbyService.getMaximumPlayerCount(gameCode),
				options.getMaxPlayers());
		assertEquals(lobbyService.allowsRandomPlayers(gameCode),
				options.allowsRandomPlayers());
	}
	@ParameterizedTest
	@MethodSource("mockHostAndSettings")
	public void cannotChangeLobbySettingsIfLobbyDoesNotExist(
			Account host, LobbyOptionsDTO options)
	{
		String gameCode = lobbyService.createLobby(host);
		int originalMaxPlayers = lobbyService.getMaximumPlayerCount(gameCode);
		boolean originalAllowsRandomPlayers = lobbyService.allowsRandomPlayers(gameCode);
		String wrongGameCode = gameCode + "wrong";
		
		boolean success = lobbyService.updateOptions(wrongGameCode, options, host);
		
		assertFalse(success);
		assertEquals(lobbyService.getMaximumPlayerCount(gameCode),
				originalMaxPlayers);
		assertEquals(lobbyService.allowsRandomPlayers(gameCode),
				originalAllowsRandomPlayers);
	}
	@ParameterizedTest
	@MethodSource("mockHostAndSettings")
	public void cannotChangeLobbySettingsIfNotHost(
			Account host, LobbyOptionsDTO options)
	{
		String gameCode = lobbyService.createLobby(host);
		int originalMaxPlayers = lobbyService.getMaximumPlayerCount(gameCode);
		boolean originalAllowsRandomPlayers = lobbyService.allowsRandomPlayers(gameCode);
		Account otherAccount = new Account();
		
		boolean success = lobbyService.updateOptions(gameCode, options, otherAccount);
		
		assertFalse(success);
		assertEquals(lobbyService.getMaximumPlayerCount(gameCode),
				originalMaxPlayers);
		assertEquals(lobbyService.allowsRandomPlayers(gameCode),
				originalAllowsRandomPlayers);
	}
	@ParameterizedTest
	@MethodSource("mockHostAndSettings")
	public void cannotChangeLobbySettingsIfTooManyPlayersInGame(
			Account host, LobbyOptionsDTO options)
	{
		String gameCode = lobbyService.createLobby(host);
		lobbyService.updateOptions(gameCode,
				new LobbyOptionsDTO(options.getMaxPlayers() + 2, options.allowsRandomPlayers()),
				host);
		
		int originalMaxPlayers = lobbyService.getMaximumPlayerCount(gameCode);
		boolean originalAllowsRandomPlayers = lobbyService.allowsRandomPlayers(gameCode);
		List<Account> players = mockPlayers(options.getMaxPlayers() + 1);
		
		players.forEach(p -> lobbyService.addPlayer(gameCode, p));
		
		boolean success = lobbyService.updateOptions(gameCode, options, host);
		
		assertFalse(success);
		assertEquals(lobbyService.getMaximumPlayerCount(gameCode),
				originalMaxPlayers);
		assertEquals(lobbyService.allowsRandomPlayers(gameCode),
				originalAllowsRandomPlayers);
	}
	
	@ParameterizedTest
	@MethodSource({"mockPlayerHostAndTwoPlayers", "mockLecturerHostAndTwoPlayers"})
	public void shouldNotRemovePlayersWhileTheyAreActive(Account host, Account player1, Account player2)
	{
		String gameCode = lobbyService.createLobby(host);
		lobbyService.addPlayer(gameCode, player1);
		lobbyService.addPlayer(gameCode, player2);
		
		lobbyService.removeInactive();
		
		assertTrue(lobbyService.lobbyExists(gameCode));
		assertTrue(lobbyService.isHost(gameCode, host));
		List<Account> players = lobbyService.getPlayers(gameCode);
		assertTrue(players.contains(player1));
		assertTrue(players.contains(player2));
	}
	@ParameterizedTest
	@MethodSource({"mockPlayerHostAndTwoPlayers", "mockLecturerHostAndTwoPlayers"})
	public void shouldRemovePlayersWhenTheyAreInactive(Account host, Account playerToRemove, Account playerToStay)
	{
		String gameCode = lobbyService.createLobby(host);
		lobbyService.addPlayer(gameCode, playerToRemove);
		lobbyService.addPlayer(gameCode, playerToStay);
		
		lobbyService.markInactive(gameCode, playerToRemove);
		
		lobbyService.removeInactive();
		
		assertTrue(lobbyService.lobbyExists(gameCode));
		assertTrue(lobbyService.isHost(gameCode, host));
		List<Account> players = lobbyService.getPlayers(gameCode);
		assertFalse(players.contains(playerToRemove));
		assertTrue(players.contains(playerToStay));
	}
	@ParameterizedTest
	@MethodSource({"mockPlayerHostAndTwoPlayers", "mockLecturerHostAndTwoPlayers"})
	public void shouldRemoveAllPlayersWhenAllInactive(Account host, Account playerToRemove1, Account playerToRemove2)
	{
		String gameCode = lobbyService.createLobby(host);
		lobbyService.addPlayer(gameCode, playerToRemove1);
		lobbyService.addPlayer(gameCode, playerToRemove2);
		
		lobbyService.markInactive(gameCode, playerToRemove1);
		lobbyService.markInactive(gameCode, playerToRemove2);
		
		lobbyService.removeInactive();
		
		assertTrue(lobbyService.lobbyExists(gameCode));
		assertTrue(lobbyService.isHost(gameCode, host));
		List<Account> players = lobbyService.getPlayers(gameCode);
		assertFalse(players.contains(playerToRemove1));
		assertFalse(players.contains(playerToRemove2));
	}
	@ParameterizedTest
	@MethodSource({"mockPlayerHostAndTwoPlayers", "mockLecturerHostAndTwoPlayers"})
	public void shouldDeleteLobbyWhenHostInactive(Account host, Account playerToRemove1, Account playerToRemove2)
	{
		String gameCode = lobbyService.createLobby(host);
		lobbyService.addPlayer(gameCode, playerToRemove1);
		lobbyService.addPlayer(gameCode, playerToRemove2);
		
		lobbyService.markInactive(gameCode, host);
		
		lobbyService.removeInactive();
		
		assertFalse(lobbyService.lobbyExists(gameCode));
	}
	@ParameterizedTest
	@MethodSource({"mockPlayerHostAndTwoPlayers", "mockLecturerHostAndTwoPlayers"})
	public void shouldNotRemovePlayersIfTheyBecameActiveAgain(Account host, Account playerToStay1, Account playerToStay2)
	{
		String gameCode = lobbyService.createLobby(host);
		lobbyService.addPlayer(gameCode, playerToStay1);
		lobbyService.addPlayer(gameCode, playerToStay2);
		
		lobbyService.markInactive(gameCode, playerToStay1);
		lobbyService.hasAnythingChanged(gameCode, playerToStay1);
		
		lobbyService.removeInactive();
		
		assertTrue(lobbyService.lobbyExists(gameCode));
		assertTrue(lobbyService.isHost(gameCode, host));
		List<Account> players = lobbyService.getPlayers(gameCode);
		assertTrue(players.contains(playerToStay1));
		assertTrue(players.contains(playerToStay2));
	}
	@ParameterizedTest
	@MethodSource({"mockPlayerHostAndTwoPlayers", "mockLecturerHostAndTwoPlayers"})
	public void shouldNotThrowWhenMarkingAsInactiveWhenGameDoesNotExist
		(Account host, Account playerToStay1, Account playerToStay2)
	{
		String gameCode = lobbyService.createLobby(host);
		lobbyService.addPlayer(gameCode, playerToStay1);
		lobbyService.addPlayer(gameCode, playerToStay2);
		String wrongGameCode = gameCode + "wrong";
		
		assertDoesNotThrow(() -> lobbyService.markInactive(wrongGameCode, host));
		assertDoesNotThrow(() -> lobbyService.markInactive(wrongGameCode, playerToStay1));
		assertDoesNotThrow(() -> lobbyService.markInactive(wrongGameCode, playerToStay2));
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
	public static List<Arguments> mockHostAndSettings()
	{
		Account[] hosts =
		{
			mockHost(PLAYER_ROLE),
			mockHost(LECTURER_ROLE)
		};
		int[] maxPlayerCounts =
		{
			1, 2, 3, 4, 5, 10, 15, 20, 25, 50
		};
		boolean[] allowRandomPlayers = {false, true};
		
		List<Arguments> ret = new ArrayList<>();
		for (Account host: hosts)
			for (int maxPlayerCount: maxPlayerCounts)
				for (boolean allowsRandomPlayers: allowRandomPlayers)
					ret.add(Arguments.of(
							host,
							new LobbyOptionsDTO(
								maxPlayerCount,
								allowsRandomPlayers)));
		return ret;
	}
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
	private static Account mockPlayer(String name)
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
}
