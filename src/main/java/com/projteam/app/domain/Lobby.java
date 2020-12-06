package com.projteam.app.domain;

import static com.projteam.app.domain.Account.PLAYER_ROLE;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Lobby
{
	private String gameCode;
	private Account host;
	private List<Account> players;
	private int maxPlayerCount;
	
	public Lobby(String gameCode, Account host)
	{
		this(gameCode, host, 20);
	}
	public Lobby(String gameCode, Account host, int maxPlayerCount)
	{
		Objects.requireNonNull(gameCode);
		Objects.requireNonNull(host);
		
		this.gameCode = gameCode;
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
		if (host.hasRole(PLAYER_ROLE))
			ret.add(host);
		return ret;
	}
	public boolean addPlayer(Account player)
	{
		if ((players.size() + ((host != null)?1:0)) > maxPlayerCount)
			return false;
		if (player.hasRole(PLAYER_ROLE))
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
	public String getGameCode()
	{
		return gameCode;
	}
}
