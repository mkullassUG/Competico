package com.projteam.app.service;

import static com.projteam.app.domain.Account.LECTURER_ROLE;
import static com.projteam.app.domain.Account.PLAYER_ROLE;
import java.util.ArrayList;
import java.util.List;
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
import org.springframework.util.Assert;
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
		Assert.notNull(lobbyService, "Lobby Service initialization failed");
	}
	
	@Test
	public void canCreateLobbies()
	{
		Assert.hasText(lobbyService.createLobby(new Account()),
				"Could not create a lobby");
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
		
		Assert.isTrue(lobbyService.getPlayers(gameCode)
				.contains(host), "Host player not on player list");
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
		
		Assert.isTrue(!lobbyService.getPlayers(gameCode)
				.contains(host), "Host lecturer on player list");
	}
	@Test
	public void gameCodeHasCorrectLength()
	{
		Assert.isTrue(lobbyService
				.createLobby(new Account())
				.length() == lobbyService.getGameCodeLength(),
				"Game code does not have correct length");
	}
	@Test
	public void gameCodeContainsCorrectChars()
	{
		Assert.isTrue(lobbyService
				.createLobby(new Account())
				.chars()
				.allMatch(Character::isLetterOrDigit),
				"Game code contains invalid characters");
	}
	@Test
	public void gameHostIsAssignedCorrectly()
	{
		Account mockAcc = new Account();
		String gameCode = lobbyService.createLobby(mockAcc);
		Assert.isTrue(lobbyService.isHost(gameCode, mockAcc),
				"Game host was not assigned correctly");
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
		
		Assert.isTrue(lobbyService.getPlayers(gameCode)
				.containsAll(mockPlayers.stream()
						.limit(maxPlayerCount)
						.collect(Collectors.toList())),
			"Player list does not contain added player");
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
			.forEach(player -> Assert.isTrue(lobbyService.addPlayer(gameCode, player),
					"Failed to add player"));
		mockPlayers.stream()
			.limit(maxPlayerCount)
			.forEach(player -> Assert.isTrue(lobbyService.removePlayer(gameCode, player),
					"Failed to remove player"));
		mockPlayers.stream()
			.limit(maxPlayerCount)
			.forEach(player -> Assert.isTrue(!lobbyService
					.getPlayers(gameCode)
					.contains(player),
					"Player list still contains removed player"));
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
				Assert.isTrue(lobbyService.addPlayer(gameCode, player),
						"Failed to add player");
				Assert.isTrue(lobbyService.removePlayer(gameCode, player),
						"Failed to remove player");
				Assert.isTrue(!lobbyService
						.getPlayers(gameCode)
						.contains(player),
						"Player list still contains removed player");
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
				.forEach(player -> Assert.isTrue(lobbyService.addPlayer(gameCode, player),
						"Failed to add player before reaching limit"));
		
		Assert.isTrue(!lobbyService.addPlayer(gameCode, mockPlayer), "Player added above limit");
		Assert.isTrue(!lobbyService.getPlayers(gameCode)
				.contains(mockPlayer),
				"Player list contains player added above limit");
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
		List<Arguments> ret = new ArrayList<>();
		Account host = mockHost(PLAYER_ROLE);
		Account player = mockPlayer("Player");
		
		IntStream.rangeClosed(0, 50)
			.forEach(max ->
			{
				List<Account> players = new ArrayList<>();
				players.addAll(
						IntStream.rangeClosed(0, max)
						.mapToObj(i -> mockPlayer("Player" + i))
						.collect(Collectors.toList()));
				ret.add(Arguments.of(host, player, players));
			});
		return ret;
	}
	public static List<Arguments> mockLecturerHostAndPlayer()
	{
		List<Arguments> ret = new ArrayList<>();
		Account host = mockHost(LECTURER_ROLE);
		Account player = mockPlayer("Player");
		
		IntStream.rangeClosed(0, 50)
			.forEach(max ->
			{
				List<Account> players = new ArrayList<>();
				players.addAll(
						IntStream.rangeClosed(0, max)
						.mapToObj(i -> mockPlayer("Player" + i))
						.collect(Collectors.toList()));
				ret.add(Arguments.of(host, player, players));
			});
		return ret;
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
