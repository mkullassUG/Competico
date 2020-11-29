package com.projteam.app.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Lobby
{
	private String gameKey;
	private Account host;
	private List<Account> players;
	private int maxPlayerCount;
	
	public Lobby(String gameKey, Account host)
	{
		this(gameKey, host, 20);
	}
	public Lobby(String gameKey, Account host, int maxPlayerCount)
	{
		Objects.requireNonNull(gameKey);
		Objects.requireNonNull(host);
		
		this.gameKey = gameKey;
		this.host = host;
		players = new ArrayList<>();
		
		this.maxPlayerCount = maxPlayerCount;
	}
	
	public Account getHost()
	{
		return host;
	}
	public List<Account> getPlayers()
	{
		List<Account> ret = new ArrayList<>(players);
		if (host.hasRole("PLAYER"))
			ret.add(host);
		return ret;
	}
	public boolean addPlayer(Account player)
	{
		if ((players.size() + ((host != null)?1:0)) > maxPlayerCount)
			return false;
		if (player.hasRole("PLAYER"))
			return players.add(player);
		return false;
	}
	public boolean removePlayer(Account player)
	{
		return players.remove(player);
	}
	public int getMaximumPlayerCount()
	{
		return maxPlayerCount;
	}
	public String getGameKey()
	{
		return gameKey;
	}
}
