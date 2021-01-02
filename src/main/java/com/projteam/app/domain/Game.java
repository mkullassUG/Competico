package com.projteam.app.domain;

import java.util.ArrayList;
import java.util.List;

public class Game
{
	private List<Account> players;
	private List<Account> spectators;
	
	public Game(List<Account> players, List<Account> spectators)
	{
		this.players = new ArrayList<>(players);
		this.spectators = new ArrayList<>(spectators);
	}
}
