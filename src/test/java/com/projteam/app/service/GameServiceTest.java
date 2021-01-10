package com.projteam.app.service;

import static com.projteam.app.domain.Account.LECTURER_ROLE;
import static com.projteam.app.domain.Account.PLAYER_ROLE;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.projteam.app.domain.Account;

public class GameServiceTest
{
	private @Mock AccountService accountService;
	private @Mock LobbyService lobbyService;
	
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
		
		assertTrue(gameService.createGameFromLobby(gameCode, host));
	}
	@Test
	public void cannotCreateGameIfLobbyDoesNotExist()
	{
		String gameCode = "gameCode";
		when(lobbyService.lobbyExists(gameCode)).thenReturn(false);
		
		assertFalse(gameService.createGameFromLobby(gameCode, null));
	}
	@ParameterizedTest
	@MethodSource({"mockLecturerHost"})
	public void canCreateGameIfLobbyContainsNoPlayers(Account host)
	{
		String gameCode = "gameCode";
		when(lobbyService.lobbyExists(gameCode)).thenReturn(true);
		when(lobbyService.getPlayers(gameCode)).thenReturn(List.of());
		when(lobbyService.getHost(gameCode)).thenReturn(host);
		
		assertFalse(gameService.createGameFromLobby(gameCode, host));
	}
	@ParameterizedTest
	@MethodSource({"mockPlayerHostAndPlayer", "mockLecturerHostAndPlayer"})
	public void cannotCreateGameIfAlreadyExists(Account host, Account player)
	{
		String gameCode = "gameCode";
		List<Account> players = List.of(host, player)
				.stream()
				.filter(a -> a.hasRole(PLAYER_ROLE))
				.collect(Collectors.toList());
		when(lobbyService.lobbyExists(gameCode)).thenReturn(true);
		when(lobbyService.getPlayers(gameCode)).thenReturn(players);
		when(lobbyService.getHost(gameCode)).thenReturn(host);
		
		gameService.createGameFromLobby(gameCode, host);
		assertFalse(gameService.createGameFromLobby(gameCode, host));
	}
	
	//---Sources---
	
	public static List<Arguments> mockLecturerHost()
	{
		return List.of(Arguments.of(mockHost(LECTURER_ROLE)));
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
	public static Account mockPlayer(String name)
	{
		return new Account.Builder()
				.withEmail("test" + name + "@test.pl")
				.withUsername("Test" + name)
				.withPasswordHash("QWERTY" + name)
				.withRoles(List.of(PLAYER_ROLE))
				.build();
	}
}
