package com.projteam.app.domain.game;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.MapKeyColumn;
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
	private Map<UUID, Map<Integer, Double>> taskCompletionMap;
	
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
		taskCompletionMap = new HashMap<>();
		taskCompletionMap.putAll(players.stream()
				.collect(Collectors.toMap(k -> k.getId(), v -> new HashMap<>())));
	}
	
	public Task getCurrentTask(Account player)
	{
		return taskMap.get(player.getId()).get(currentTaskNumber.get(player.getId()));
	}

	public void advance(Account player, double completion)
	{
		UUID playerId = player.getId();
		Integer taskNumber = currentTaskNumber.get(playerId);
		//TODO implement bonuses for time
		taskCompletionMap.get(playerId).put(taskNumber, completion);
		currentTaskNumber.put(playerId, taskNumber + 1);
	}

	public boolean hasGameFinishedFor(Account player)
	{
		return currentTaskNumber.get(player.getId()) >= taskCount;
	}
	public boolean hasGameFinished()
	{
		return currentTaskNumber.values()
				.stream()
				.allMatch(taskNum -> taskNum >= taskCount);
	}

	public int getCurrentTaskNumber(Account player)
	{
		return currentTaskNumber.get(player.getId());
	}

	public int getTaskCount(Account player)
	{
		return taskMap.get(player.getId()).size();
	}
	public GameResults createGameResult()
	{
		UUID gameId = UUID.randomUUID();
		GameResults grs = new GameResults(gameId);
		for (Account player: players)
		{
			UUID gameResultId = UUID.randomUUID();
			UUID playerId = player.getId();
			Map<Integer, Double> completion = taskCompletionMap.get(playerId);
			Map<Integer, Double> difficulty = new HashMap<>();
			int i = 0;
			for (Task t: taskMap.get(playerId))
			{
				difficulty.put(i, t.getDifficulty());
				i++;
			}
			//TODO implement bonuses for time
			Map<Integer, Long> timeTaken = difficulty.keySet()
					.stream()
					.collect(Collectors.toMap(n -> n, n -> 10000l));
			
			GameResult gr = new GameResult(gameResultId, playerId, completion, difficulty, timeTaken);
			grs.addResult(gr);
		}
		return grs;
	}
}
