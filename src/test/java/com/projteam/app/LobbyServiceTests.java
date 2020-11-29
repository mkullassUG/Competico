package com.projteam.app;

import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;
import com.projteam.app.domain.Account;
import com.projteam.app.service.AccountService;
import com.projteam.app.service.LobbyService;

@SpringBootTest
@AutoConfigureMockMvc
public class LobbyServiceTests
{
	@Autowired
	private LobbyService lobbyService;
	
	@Test
	public void contextLoads()
	{
		Assert.notNull(lobbyService, "No Lobby Service found");
	}
	
	@Test
	public void canCreateLobbies()
	{
		lobbyService.deleteAllLobbies();
		Assert.hasText(lobbyService.createLobby(new Account()), "Could not create a lobby");
	}
	@Test
	public void gameCodeHasCorrectLength()
	{
		lobbyService.deleteAllLobbies();
		Assert.isTrue(lobbyService
				.createLobby(new Account())
				.length() == lobbyService.getGameCodeLength(), "Game code does not have correct length");
	}
	@Test
	public void gameCodeContainsCorrectChars()
	{
		lobbyService.deleteAllLobbies();
		Assert.isTrue(lobbyService
				.createLobby(new Account())
				.chars()
				.allMatch(Character::isLetterOrDigit), "Game code contains invalid characters");
	}
	@Test
	public void gameHostIsAssignedCorrectly()
	{
		lobbyService.deleteAllLobbies();
		Account mockAcc = new Account();
		String gameCode = lobbyService.createLobby(mockAcc);
		Assert.isTrue(lobbyService.isHost(gameCode, mockAcc), "Game host was not assigned correctly");
	}
	@ParameterizedTest
	@MethodSource("mockHostAndPlayers")
	public void addedPlayersAppearInPlayerList(Account mockHost, List<Account> mockPlayers)
	{
		lobbyService.deleteAllLobbies();
		String gameCode = lobbyService.createLobby(mockHost);
		if (mockPlayers.size() > lobbyService.getMaximumPlayerCount(gameCode))
			return;
		for (Account player: mockPlayers)
			lobbyService.addPlayer(gameCode, player);
		Assert.isTrue(lobbyService.getPlayers(gameCode).containsAll(mockPlayers),
				"Player list does not contain added player");
	}
	@ParameterizedTest
	@MethodSource("mockHostAndPlayers")
	public void removedPlayersDoNotAppearInPlayerList_sequential(Account mockHost, List<Account> mockPlayers)
	{
		lobbyService.deleteAllLobbies();
		String gameCode = lobbyService.createLobby(mockHost);
		if (mockPlayers.size() > lobbyService.getMaximumPlayerCount(gameCode))
			return;
		for (Account player: mockPlayers)
			lobbyService.addPlayer(gameCode, player);
		for (Account player: mockPlayers)
			lobbyService.removePlayer(gameCode, player);
		for (Account player: mockPlayers)
			Assert.isTrue(!lobbyService.getPlayers(gameCode).contains(player),
				"Player list still contains removed player");
	}
	@ParameterizedTest
	@MethodSource("mockHostAndPlayers")
	public void removedPlayersDoNotAppearInPlayerList_isolated(Account mockHost, List<Account> mockPlayers)
	{
		lobbyService.deleteAllLobbies();
		String gameCode = lobbyService.createLobby(mockHost);
		if (mockPlayers.size() > lobbyService.getMaximumPlayerCount(gameCode))
			return;
		for (Account player: mockPlayers)
		{
			lobbyService.addPlayer(gameCode, player);
			lobbyService.removePlayer(gameCode, player);
			Assert.isTrue(!lobbyService.getPlayers(gameCode).contains(player),
				"Player list still contains removed player");
		}
	}
	
	//Sources
	
	public static List<Arguments> mockHostAndPlayers()
	{
		List<Arguments> ret = new ArrayList<>();
		Account host = new Account("testHost@test.pl", "TestHost", "QWERTY");
		for (int i = 0; i < 10; i++)
		{
			List<Account> players = new ArrayList<>();
			for (int i0 = 0; i0 < 10; i0++)
				players.add(new Account("testPlayer" + i0 + "@test.pl", "TestPlayer" + i0, "QWERTY" + i0));
			ret.add(Arguments.of(host, players));
		}
		return ret;
	}
}
