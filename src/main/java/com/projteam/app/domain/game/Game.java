package com.projteam.app.domain.game;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import com.projteam.app.domain.Account;
import com.projteam.app.domain.game.tasks.Task;

public class Game
{
	private List<Account> players;
	private List<Account> spectators;
	private int taskCount;
	private double targetDifficulty;
	private Map<UUID, Integer> currentTaskNumber;
	private Map<UUID, List<Task>> taskMap;
	
	public Game(List<Account> players,
			List<Account> spectators,
			int taskCount,
			double targetDifficulty,
			Map<UUID, List<Task>> taskMap)
	{
		this.players = players;
		this.spectators = spectators;
		this.taskCount = taskCount;
		this.targetDifficulty = targetDifficulty;
		currentTaskNumber = new HashMap<>();
		currentTaskNumber.putAll(players.stream()
				.collect(Collectors.toMap(k -> k.getId(), v -> 0)));
		this.taskMap = taskMap;
	}
	
	public Task getCurrentTask(Account player)
	{
		return taskMap.get(player.getId()).get(currentTaskNumber.get(player.getId()));
	}

	public void advance(Account player)
	{
		currentTaskNumber.compute(player.getId(), (p, i) -> i + 1);
	}

	public boolean hasGameFinished(Account player)
	{
		return currentTaskNumber.get(player.getId()) >= taskCount;
	}
}
