package com.projteam.app.domain;

import static com.projteam.app.domain.Account.PLAYER_ROLE;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Lobby
{
	private String gameCode;
	private Account host;
	private List<Account> players;
	private int maxPlayerCount;
	
	private int changeCount;
	private Map<UUID, Integer> playerLastChangeCounts;
	
	public Lobby(String gameCode, Account host)
	{
		this(gameCode, host, 20);
	}
	public Lobby(String gameCode, Account host, int maxPlayerCount)
	{
		this.gameCode = gameCode;
		this.host = host;
		players = new ArrayList<>();
		
		this.maxPlayerCount = maxPlayerCount;
		
		changeCount = 0;
		playerLastChangeCounts = new HashMap<>();
	}
	
	public Account getHost()
	{
		return host;
	}
	public List<Account> getPlayers()
	{
		List<Account> ret = new ArrayList<>(players);
		if (host.hasRole(PLAYER_ROLE))
			ret.add(host);
		return ret;
	}
	public boolean addPlayer(Account player)
	{
		if (isHost(player))
			return false;
		if (!canAcceptPlayer())
			return false;
		if (player.hasRole(PLAYER_ROLE) && players.add(player))
		{
			changeOccurred();
			return true;
		}
		return false;
	}
	public boolean removePlayer(Account player)
	{
		if (players.remove(player))
		{
			changeOccurred();
			return true;
		}
		return false;
	}
	public boolean removePlayer(Account player, Account requestSource)
	{
		if (host.equals(requestSource))
			return removePlayer(player);
		return false;
	}
	public int getMaximumPlayerCount()
	{
		return maxPlayerCount;
	}
	public String getGameCode()
	{
		return gameCode;
	}
	public boolean canAcceptPlayer()
	{
		return ((players.size() + ((host.hasRole(PLAYER_ROLE))?1:0)) < maxPlayerCount);
	}
	public boolean containsPlayer(Account player)
	{
		return players.contains(player);
	}
	public boolean containsPlayerOrHost(Account player)
	{
		return containsPlayer(player) || isHost(player);
	}
	public boolean isHost(Account player)
	{
		return host.equals(player);
	}
	
	private void changeOccurred()
	{
		changeCount++;
	}
	public boolean hasAnthingChanged(UUID accountID)
	{
		if (!playerLastChangeCounts.containsKey(accountID))
		{
			playerLastChangeCounts.put(accountID, changeCount);
			return true;
		}
		boolean ret = changeCount != playerLastChangeCounts.get(accountID);
		if (ret)
			playerLastChangeCounts.put(accountID, changeCount);
		return ret;
	}
	
	@Override
	public String toString()
	{
		return "Lobby [gameCode=" + gameCode + ", host=" + host + ", players=" + players + ", maxPlayerCount=" + maxPlayerCount + "]";
	}
}
