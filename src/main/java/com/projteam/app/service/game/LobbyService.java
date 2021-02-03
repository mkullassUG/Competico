package com.projteam.app.service.game;

import static java.util.Collections.synchronizedMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.projteam.app.domain.Account;
import com.projteam.app.domain.game.Lobby;
import com.projteam.app.dto.lobby.LobbyOptionsDTO;
import com.projteam.app.service.AccountService;

@Service
public class LobbyService
{
	private AccountService accServ;
	
	private Map<String, Lobby> lobbies;
	private Set<String> lobbyCodesAllowingRandomPlayers;

	private final char[] gameCodeChars;
	private final int gameCodeLength = 8;
	private final int maxGameCodeRerollCount = 1000;
	private final long MAX_TIME_SINCE_LAST_INTERACTION_MILLI = 120000;
	
	@Autowired
	public LobbyService(AccountService accServ)
	{
		lobbies = syncMap();
		lobbyCodesAllowingRandomPlayers = syncSet();
		
		gameCodeChars = IntStream.range(0, 256)
			.filter(LobbyService::isValidGameCodeChar)
			.mapToObj(c -> Character.toString((char) c))
            .collect(Collectors.joining())
            .toCharArray();
		this.accServ = accServ;
	}
	
	public String createLobby()
	{
		return createLobby(getAccount());
	}
	public String createLobby(Account host)
	{
		Objects.requireNonNull(host);
		
		String gameCode = generateGameCode();
		
		for (int i = 0; i < maxGameCodeRerollCount; i++)
		{
			if (lobbies.containsKey(gameCode))
				gameCode = generateGameCode();
		}
		
		synchronized (lobbies)
		{
			lobbies.put(gameCode, new Lobby(gameCode, host));
		}
		
		return gameCode;
	}

	public boolean deleteLobby(String gameCode)
	{
		return deleteLobby(gameCode, getAccount());
	}
	public boolean deleteLobby(String gameCode, Account requestSource)
	{
		if (!isHost(gameCode, requestSource))
			return false;
		synchronized (lobbies)
		{
			lobbyCodesAllowingRandomPlayers.remove(gameCode);
			lobbies.remove(gameCode);
			return true;
		}
	}
	public boolean lobbyExists(String gameCode)
	{
		return lobbies.containsKey(gameCode);
	}
	
	public Optional<Boolean> hasAnythingChanged(String gameCode, Account account)
	{
		return Optional.ofNullable(lobbies.get(gameCode))
				.filter(lobby -> lobby.containsPlayerOrHost(account))
				.map(lobby -> lobby.hasAnthingChanged(account.getId()));
	}
	
	public boolean addPlayer(String gameCode)
	{
		return addPlayer(gameCode, accServ.getAuthenticatedAccount()
				.orElseThrow(() -> new IllegalArgumentException("Not authenticated.")));
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
		return removePlayer(gameCode, accServ.getAuthenticatedAccount()
				.orElseThrow(() -> new IllegalArgumentException("Not authenticated.")));
	}
	public boolean removePlayer(String gameCode, Account player)
	{
		Lobby lobby = lobbies.get(gameCode);
		if (lobby != null)
			return lobby.removePlayer(player);
		return false;
	}
	public boolean removePlayer(String gameCode, Account requestSource, Account player)
	{
		Lobby lobby = lobbies.get(gameCode);
		if (lobby != null)
			return lobby.removePlayer(player, requestSource);
		return false;
	}
	public List<Account> getPlayers(String gameCode)
	{
		return Optional.ofNullable(lobbies.get(gameCode))
			.map(lobby -> lobby.getPlayers())
			.orElse(null);
	}
	public int getMaximumPlayerCount(String gameCode)
	{
		return lobbies.get(gameCode).getMaximumPlayerCount();
	}
	public boolean allowRandomPlayers(String gameCode, boolean allow, Account requestSource)
	{
		if (!isHost(gameCode, requestSource))
			return false;
		if (allow)
			return lobbyCodesAllowingRandomPlayers.add(gameCode);
		else
			return lobbyCodesAllowingRandomPlayers.remove(gameCode);
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
		return isHost(gameCode, accServ.getAuthenticatedAccount()
				.orElseThrow(() -> new IllegalArgumentException("Not authenticated.")));
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
		return lobbyCodesToChooseFrom.get((int) (Math.random() * lobbyCodesToChooseFrom.size()));
	}
	public int getGameCodeLength()
	{
		return gameCodeLength;
	}
	public boolean isLobbyFull(String gameCode)
	{
		return !lobbies.get(gameCode).canAcceptPlayer();
	}
	public int getLobbyCount()
	{
		return lobbies.size();
	}
	public int getRandomAccessibleLobbyCount()
	{
		return lobbyCodesAllowingRandomPlayers.size();
	}
	
	private Account getAccount()
	{
		return accServ.getAuthenticatedAccount()
				.orElseThrow(() -> new IllegalArgumentException("Not authenticated."));
	}
	private static boolean isValidGameCodeChar(int c)
	{
		return ((c >= '0') && (c <= '9'))
				|| ((c >= 'a') && (c <= 'z'))
				|| ((c >= 'A') && (c <= 'Z'));
	}

	public Optional<String> getLobbyForAccount(Account acc)
	{
		return lobbies.entrySet()
				.stream()
				.filter(e -> e.getValue().containsPlayerOrHost(acc))
				.map(e ->
				{
					e.getValue().noteInteraction(acc);
					return e;
				})
				.map(e -> e.getKey())
				.findAny();
	}

	public boolean updateOptions(String gameCode, LobbyOptionsDTO options)
	{
		return updateOptions(gameCode, options, getAccount());
	}
	public boolean updateOptions(String gameCode, LobbyOptionsDTO options, Account requestSource)
	{
		Lobby lobby = lobbies.get(gameCode);
		if ((lobby != null) && (lobby.isHost(requestSource)))
		{
			if (!lobby.setMaximumPlayerCount(options.getMaxPlayers(), requestSource))
				return false;
			if (options.isAllowsRandomPlayers())
				lobbyCodesAllowingRandomPlayers.add(gameCode);
			else
				lobbyCodesAllowingRandomPlayers.remove(gameCode);
			return true;
		}
		return false;
	}
	
	public void markInactive(String gameCode, Account acc)
	{
		Lobby lobby = lobbies.get(gameCode);
		if (lobby == null)
			return;
		lobby.markInactive(acc);
	}
	@Scheduled(fixedDelay = 30000)
	public void removeInactive()
	{
		synchronized (lobbies)
		{
			Iterator<Lobby> it = lobbies.values().iterator();
			while (it.hasNext())
			{
				Lobby lobby = it.next();
				if (lobby.isInactive(MAX_TIME_SINCE_LAST_INTERACTION_MILLI))
				{
					it.remove();
					lobbyCodesAllowingRandomPlayers.remove(lobby.getGameCode());
				}
				else
					lobby.removeInactivePlayers(MAX_TIME_SINCE_LAST_INTERACTION_MILLI);
			}
		}
	}
	
	private String generateGameCode()
	{
		return new Random().ints(gameCodeLength, 0, gameCodeChars.length)
			.mapToObj(i -> Character.toString(gameCodeChars[i]))
	        .collect(Collectors.joining());
	}
	
	private static <T, U> Map<T, U> syncMap()
	{
		return synchronizedMap(new HashMap<>());
	}
	private static <T> Set<T> syncSet()
	{
		return Collections.synchronizedSet(new HashSet<>());
	}
}