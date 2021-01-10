package com.projteam.app.service;

import static com.projteam.app.domain.Account.PLAYER_ROLE;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.projteam.app.domain.Account;
import com.projteam.app.domain.game.Game;
import com.projteam.app.domain.game.tasks.Task;
import com.projteam.app.domain.game.tasks.WordFill;
import com.projteam.app.domain.game.tasks.answers.TaskAnswer;

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
		return createGameFromLobby(gameCode, getAccount());
	}
	public boolean createGameFromLobby(String gameCode, Account requestSource)
	{
		if (!lobbyServ.lobbyExists(gameCode))
			return false;
		if (games.containsKey(gameCode))
			return false;
		if (!lobbyServ.isHost(gameCode, requestSource))
			return false;
		
		List<Account> players = lobbyServ.getPlayers(gameCode);
		List<Account> spectators = Optional.ofNullable(lobbyServ.getHost(gameCode))
				.filter(host -> !host.hasRole(PLAYER_ROLE))
				.stream()
				.collect(Collectors.toList());
		
		if (players.size() < 1)
			return false;
		
		int taskCount = 8 + (int) (Math.random() * 4); //TODO export to properties
		double targetDifficulty = 100; //TODO refactor
		Map<UUID, List<Task>> taskMap = new HashMap<>();
		taskMap.putAll(players.stream()
				.collect(Collectors.toMap(player -> player.getId(), player ->
					IntStream.range(0, taskCount)
							.mapToObj(i -> new WordFill())
							.collect(Collectors.toList()))));
		
		games.put(gameCode, new Game(players, spectators, taskCount, targetDifficulty, taskMap));
		return true;
	}

	public boolean gameExists(String gameCode)
	{
		return games.containsKey(gameCode);
	}
	
	public Task getCurrentTask(String gameCode)
	{
		return getCurrentTask(gameCode, getAccount());
	}
	public Task getCurrentTask(String gameCode, Account player)
	{
		if (!games.containsKey(gameCode))
			return null;
		
		Game game = games.get(gameCode);
		return game.getCurrentTask(player);
	}
	
	public boolean hasGameFinished(String gameCode)
	{
		return hasGameFinished(gameCode, getAccount());
	}
	public boolean hasGameFinished(String gameCode, Account player)
	{
		if (!games.containsKey(gameCode))
			return true;
		return games.get(gameCode).hasGameFinished(player);
	}
	
	public void acceptAnswer(String gameCode, TaskAnswer answer)
	{
		acceptAnswer(gameCode, answer, getAccount());
	}
	public void acceptAnswer(String gameCode, TaskAnswer answer, Account player)
	{
		if (!games.containsKey(gameCode))
			return;
		
		Game game = games.get(gameCode);
		if (game.hasGameFinished(player))
			return;
		Task task = game.getCurrentTask(player);
		task.acceptAnswer(answer);
		game.advance(player);
	}
	
	private Account getAccount()
	{
		return accServ.getAuthenticatedAccount()
				.orElseThrow(() -> new IllegalArgumentException("Not authenticated."));
	}
}
