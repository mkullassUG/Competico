package com.projteam.app.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.projteam.app.domain.Account;
import com.projteam.app.domain.Lobby;

@Service
public class LobbyService
{
	private AccountService accServ;
	
	private Map<String, Lobby> lobbies;
	private Set<String> lobbyCodesAllowingRandomPlayers;

	private final char[] gameCodeChars;
	private final int gameCodeLength = 8;
	
	@Autowired
	public LobbyService(AccountService accServ)
	{
		lobbies = new HashMap<>();
		lobbyCodesAllowingRandomPlayers = new HashSet<>();
		
		gameCodeChars = IntStream.iterate(0, i -> i + 1)
			.limit(255)
			.filter(Character::isLetterOrDigit)
			.mapToObj(c -> Character.toString((char) c))
            .collect(Collectors.joining())
            .toCharArray();
		this.accServ = accServ;
	}
	
	public String createLobby()
	{
		return createLobby(accServ.getAuthenticatedAccount());
	}
	public String createLobby(Account host)
	{
		String gameCode = new Random().ints(gameCodeLength, 0, gameCodeChars.length)
				.mapToObj(i -> Character.toString(gameCodeChars[i]))
	            .collect(Collectors.joining());
		
		lobbies.put(gameCode, new Lobby(gameCode, host));
		
		return gameCode;
	}
	public boolean deleteLobby(String gameCode)
	{
		if (!lobbies.containsKey(gameCode))
			return false;
		lobbyCodesAllowingRandomPlayers.remove(gameCode);
		lobbies.remove(gameCode);
		return true;
	}
	public void deleteAllLobbies()
	{
		lobbyCodesAllowingRandomPlayers.clear();
		lobbies.clear();
	}
	
	public boolean addPlayer(String gameKey)
	{
		return addPlayer(gameKey, accServ.getAuthenticatedAccount());
	}
	public boolean addPlayer(String gameKey, Account player)
	{
		Lobby lobby = lobbies.get(gameKey);
		if (lobby != null)
			return lobby.addPlayer(player);
		return false;
	}
	public boolean removePlayer(String gameKey)
	{
		return removePlayer(gameKey, accServ.getAuthenticatedAccount());
	}
	public boolean removePlayer(String gameKey, Account player)
	{
		Lobby lobby = lobbies.get(gameKey);
		if (lobby != null)
			return lobby.removePlayer(player);
		return false;
	}
	public List<Account> getPlayers(String gameKey)
	{
		Lobby lobby = lobbies.get(gameKey);
		return lobby.getPlayers();
	}
	public int getMaximumPlayerCount(String gameCode)
	{
		return lobbies.get(gameCode).getMaximumPlayerCount();
	}
	public boolean allowRandomPlayers(String gameCode, boolean allow)
	{
		if (lobbies.containsKey(gameCode))
			return lobbyCodesAllowingRandomPlayers.add(gameCode);
		lobbyCodesAllowingRandomPlayers.remove(gameCode);
		return false;
	}
	public boolean isHost(String gameCode)
	{
		return isHost(gameCode, accServ.getAuthenticatedAccount());
	}
	public boolean isHost(String gameCode, Account acc)
	{
		Lobby lobby = lobbies.get(gameCode);
		if (lobby == null)
			return false;
		return lobby.getHost().equals(acc);
	}
	public boolean allowsRandomPlayers(String gameCode)
	{
		return lobbyCodesAllowingRandomPlayers.contains(gameCode);
	}
	public String getRandomLobby()
	{
		if (lobbyCodesAllowingRandomPlayers.isEmpty())
			return null;
		List<String> lobbyCodesToChooseFrom = new ArrayList<>(lobbyCodesAllowingRandomPlayers);
		Collections.shuffle(lobbyCodesToChooseFrom);
		return lobbyCodesToChooseFrom.get(0);
	}
	public synchronized int getGameCodeLength()
	{
		return gameCodeLength;
	}
}