package com.projteam.app.service;

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
		Account host = new Account("testHost@test.pl", "TestHost", "QWERTY", List.of("PLAYER"));
		String gameCode = lobbyService.createLobby(host);
		
		Assert.isTrue(lobbyService.getPlayers(gameCode)
				.contains(host), "Host player not on player list");
	}
	@Test
	public void hostLecturerDoesNotAppearInList()
	{
		Account host = new Account("testHost@test.pl", "TestHost", "QWERTY", List.of("LECTURER"));
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
	@MethodSource("mockHostAndPlayers")
	public void addedPlayersAppearInPlayerList(Account mockHost,
			List<Account> mockPlayers)
	{
		String gameCode = lobbyService.createLobby(mockHost);
		int maxPlayerCount = lobbyService.getMaximumPlayerCount(gameCode);
		
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
	@MethodSource("mockHostAndPlayers")
	public void removedPlayersDoNotAppearInPlayerList_sequential(Account mockHost,
			List<Account> mockPlayers)
	{
		String gameCode = lobbyService.createLobby(mockHost);
		int maxPlayerCount = lobbyService.getMaximumPlayerCount(gameCode);
		
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
	@MethodSource("mockHostAndPlayers")
	public void removedPlayersDoNotAppearInPlayerList_isolated(Account mockHost,
			List<Account> mockPlayers)
	{
		String gameCode = lobbyService.createLobby(mockHost);
		mockPlayers.stream()
			.limit(lobbyService.getMaximumPlayerCount(gameCode))
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
	
	@Test
	public void playerNotAddedAbovePlayerLimit()
	{
		Account host = new Account("testHost@test.pl", "TestHost", "QWERTY");
		Account mockPlayer = new Account("mockPlayer@mock.pl",
				"MockPlayer",
				"QWERTY",
				List.of("PLAYER"));
		String gameCode = lobbyService.createLobby(host);
		int maxPlayerCount = lobbyService.getMaximumPlayerCount(gameCode);
		
		IntStream.range(0, maxPlayerCount)
				.mapToObj(i -> new Account("testPlayer" + i + "@test.pl",
						"TestPlayer" + i,
						"QWERTY" + i,
						List.of("PLAYER")))
				.forEach(player -> Assert.isTrue(lobbyService.addPlayer(gameCode, player),
						"Failed to add player before reaching limit"));
		
		Assert.isTrue(!lobbyService.addPlayer(gameCode, mockPlayer), "Player added above limit");
		Assert.isTrue(!lobbyService.getPlayers(gameCode)
				.contains(mockPlayer),
				"Player list contains player added above limit");
	}
	
	//---Sources---
	
	public static List<Arguments> mockHostAndPlayers()
	{
		List<Arguments> ret = new ArrayList<>();
		Account host = new Account("testHost@test.pl", "TestHost", "QWERTY");
		
		IntStream.rangeClosed(0, 50)
			.forEach(max ->
			{
				List<Account> players = new ArrayList<>();
				players.addAll(
						IntStream.rangeClosed(0, max)
						.mapToObj(i -> new Account("testPlayer" + i + "@test.pl",
								"TestPlayer" + i,
								"QWERTY" + i,
								List.of("PLAYER")))
						.collect(Collectors.toList()));
				ret.add(Arguments.of(host, players));
			});
		return ret;
	}
}
