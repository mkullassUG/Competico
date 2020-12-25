package com.projteam.app.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
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
		lobbies = Collections.synchronizedMap(new HashMap<>());
		lobbyCodesAllowingRandomPlayers = new HashSet<>();
		
		gameCodeChars = IntStream.range(0, 256)
			.filter(LobbyService::isValidGameCodeChar)
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
		Objects.requireNonNull(host);
		
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
	public boolean lobbyExists(String gameCode)
	{
		return lobbies.containsKey(gameCode);
	}
	
	public boolean addPlayer(String gameCode)
	{
		return addPlayer(gameCode, accServ.getAuthenticatedAccount());
	}
	public boolean addPlayer(String gameCode, Account player)
	{
		Lobby lobby = lobbies.get(gameCode);
		if (lobby != null)
			return lobby.addPlayer(player);
		return false;
	}
	public boolean removePlayer(String gameCode)
	{
		return removePlayer(gameCode, accServ.getAuthenticatedAccount());
	}
	public boolean removePlayer(String gameCode, Account player)
	{
		Lobby lobby = lobbies.get(gameCode);
		if (lobby != null)
			return lobby.removePlayer(player);
		return false;
	}
	public List<Account> getPlayers(String gameCode)
	{
		Lobby lobby = lobbies.get(gameCode);
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
	public Account getHost(String gameCode)
	{
		Lobby lobby = lobbies.get(gameCode);
		if (lobby == null)
			return null;
		return lobby.getHost();
	}
	public boolean isHost(String gameCode)
	{
		return isHost(gameCode, accServ.getAuthenticatedAccount());
	}
	public boolean isHost(String gameCode, Account acc)
	{
		return Optional.ofNullable(lobbies.get(gameCode))
				.map(l -> l.isHost(acc))
				.orElse(false);
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
		return lobbyCodesToChooseFrom.stream()
			.filter(gameCode -> lobbies.get(gameCode).canAcceptPlayer())
			.findFirst()
			.orElse(null);
	}
	public int getGameCodeLength()
	{
		return gameCodeLength;
	}
	public boolean isLobbyFull(String gameCode)
	{
		return !lobbies.get(gameCode).canAcceptPlayer();
	}
	
	private static boolean isValidGameCodeChar(int c)
	{
		return ((c >= '0') && (c <= '9'))
				|| ((c >= 'a') && (c <= 'z'))
				|| ((c >= 'A') && (c <= 'Z'));
	}

	public String getLobbyForPlayer(Account player)
	{
		return lobbies.entrySet()
				.stream()
				.filter(e -> e.getValue().containsPlayerOrHost(player))
				.map(e -> e.getKey())
				.findAny()
				.orElse(null);
	}
}