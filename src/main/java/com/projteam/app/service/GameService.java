package com.projteam.app.service;

import static com.projteam.app.domain.Account.PLAYER_ROLE;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.projteam.app.domain.Account;
import com.projteam.app.domain.game.Game;

@Service
public class GameService
{
	private AccountService accServ;
	private LobbyService lobbyServ;
	
	private Map<String, Game> games;
	
	@Autowired
	public GameService(AccountService accServ, LobbyService lobbyServ)
	{
		this.accServ = accServ;
		this.lobbyServ = lobbyServ;
		
		games = new HashMap<>();
	}
	
	public boolean createGameFromLobby(String gameCode)
	{
		if (!lobbyServ.lobbyExists(gameCode))
			return false;
		if (games.containsKey(gameCode))
			return false;
		
		List<Account> players = lobbyServ.getPlayers(gameCode);
		List<Account> spectators = Optional.ofNullable(lobbyServ.getHost(gameCode))
				.filter(host -> !host.hasRole(PLAYER_ROLE))
				.stream()
				.collect(Collectors.toList());
		
		if (players.size() > 0)
		{
			//TODO implement in next ticket
		}
		return false;
	}
}
