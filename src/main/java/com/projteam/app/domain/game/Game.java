package com.projteam.app.domain.game;

import static java.util.Collections.synchronizedList;
import static java.util.Collections.synchronizedMap;
import static java.util.stream.Collectors.toMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.stream.Collectors;
import com.projteam.app.domain.Account;
import com.projteam.app.domain.game.tasks.Task;
import com.projteam.app.dto.game.GameResultPersonalDTO;
import com.projteam.app.dto.game.GameResultTotalDuringGameDTO;

public class Game
{
	private UUID gameID;
	private List<Account> originalPlayers;
	private List<Account> activePlayers;
	private List<Account> spectators;
	private int taskCount;
	private Map<UUID, Integer> currentTaskNumber;
	private Map<UUID, List<Task>> taskMap;
	private Map<UUID, Map<Integer, Double>> taskCompletionMap;
	
	private int resultsChangeCount;
	private Map<UUID, Integer> lastResultCheckForAccount;
	
	private Map<UUID, Long> lastInteractions;
	
	private static final long NANOS_IN_MILLI = 1000000;
	
	public Game(List<Account> players,
			List<Account> spectators,
			int taskCount,
			Map<UUID, List<Task>> taskMap)
	{
		gameID = UUID.randomUUID();
		originalPlayers = syncList(players);
		activePlayers = syncList(players);
		this.spectators = syncList(spectators);
		this.taskCount = taskCount;
		currentTaskNumber = syncMap();
		currentTaskNumber.putAll(players.stream()
				.collect(toMap(k -> k.getId(), v -> 0)));
		this.taskMap = syncMap(taskMap.entrySet()
				.stream()
				.collect(toMap(Entry::getKey, e -> syncList(e.getValue()))));
		taskCompletionMap = syncMap();
		taskCompletionMap.putAll(players.stream()
				.collect(toMap(k -> k.getId(), v -> syncMap())));
		
		resultsChangeCount = 0;
		lastResultCheckForAccount = syncMap();
		
		lastInteractions = syncMap();
		players.forEach(p -> noteInteraction(p));
	}
	
	public UUID getID()
	{
		return gameID;
	}
	public Task getCurrentTask(Account player)
	{
		UUID id = player.getId();
		noteInteraction(id);
		return taskMap.get(id).get(currentTaskNumber.get(id));
	}

	public void advance(Account player, double completion)
	{
		UUID playerId = player.getId();
		noteInteraction(playerId);
		Integer taskNumber = currentTaskNumber.get(playerId);
		//TODO implement bonuses for time
		taskCompletionMap.get(playerId).put(taskNumber, completion);
		currentTaskNumber.put(playerId, taskNumber + 1);
		resultsChangeCount++;
	}

	public boolean hasGameFinishedFor(Account player)
	{
		UUID id = player.getId();
		noteInteraction(id);
		return currentTaskNumber.get(id) >= taskCount;
	}
	public boolean hasGameFinished()
	{
		return currentTaskNumber.values()
				.stream()
				.allMatch(taskNum -> taskNum >= taskCount);
	}

	public int getCurrentTaskNumber(Account player)
	{
		UUID id = player.getId();
		noteInteraction(id);
		return currentTaskNumber.get(id);
	}

	public int getTaskCount(Account player)
	{
		UUID id = player.getId();
		noteInteraction(id);
		return taskMap.get(id).size();
	}
	public GameResults createGameResult()
	{
		GameResults grs = new GameResults(gameID);
		for (Account player: originalPlayers)
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
					.collect(toMap(n -> n, n -> 10000l));
			
			//TODO include in game result
			boolean isActive = activePlayers.contains(player);
			
			GameResult gr = new GameResult(gameResultId, playerId, completion, difficulty, timeTaken);
			grs.addResult(gr);
		}
		return grs;
	}

	public List<GameResultTotalDuringGameDTO> getCurrentResults()
	{
		List<GameResultTotalDuringGameDTO> ret = new ArrayList<>();
		for (Account player: originalPlayers)
		{
			UUID playerId = player.getId();
			int currentTask = currentTaskNumber.get(playerId);
			
			List<Task> tasks = taskMap.get(playerId);
			Map<Integer, Double> completion = taskCompletionMap.get(playerId);
			double score = 0;
			long totalTime = 0;
			for (int i = 0; i < currentTask; i++)
			{
				//TODO implement bonuses for time
				long time = 10000;
				score += calculateScore(completion.get(i), tasks.get(i).getDifficulty(), time);
				totalTime += time;
			}
			boolean hasFinished = currentTask >= taskCount;
			boolean isActive = activePlayers.contains(player);
			
			ret.add(new GameResultTotalDuringGameDTO(
					player.getUsername(),
					player.getNickname(), 
					(long) score,
					totalTime,
					hasFinished,
					!isActive));
		}
		return ret.stream()
				.sorted((r1, r2) -> -Long.compare(r1.getTotalScore(), r2.getTotalScore()))
				.collect(Collectors.toList());
	}
	public List<GameResultPersonalDTO> getPersonalResults(Account player)
	{
		UUID playerId = player.getId();
		if (!originalPlayers.contains(player))
			return null;
		
		List<GameResultPersonalDTO> ret = new ArrayList<>();
		
		List<Task> tasks = taskMap.get(playerId);
		Map<Integer, Double> completion = taskCompletionMap.get(playerId);
		
		var difficulty = tasks.stream()
				.map(task -> task.getDifficulty())
				.collect(Collectors.toList());
		//TODO implement taken time saving
		var timeTaken = tasks.stream()
				.map(task -> 10000)
				.collect(Collectors.toList());
		int l = Math.min(completion.size(),
				Math.min(difficulty.size(),
						timeTaken.size()));
		
		//TODO include in game result
		boolean isActive = activePlayers.contains(player);
		
		for (int i = 0; i < l; i++)
		{
			ret.add(new GameResultPersonalDTO(
					completion.get(i),
					timeTaken.get(i),
					difficulty.get(i)));
		}
		return ret;
	}

	public static double calculateScore(double completion, double difficulty, long timeTaken)
	{
		//TODO implement bonuses for time
		return completion * difficulty;
	}
	
	public boolean containsPlayer(Account acc)
	{
		return originalPlayers.contains(acc);
	}
	public boolean isPlayerActive(Account acc)
	{
		return activePlayers.contains(acc);
	}
	public boolean containsSpectator(Account acc)
	{
		return spectators.contains(acc);
	}
	public boolean containsPlayerOrSpectator(Account acc)
	{
		return containsPlayer(acc) || containsSpectator(acc);
	}
	public boolean haveResultsChanged(UUID gameID, Account acc)
	{
		if (!containsPlayerOrSpectator(acc))
			throw new IllegalArgumentException("Insufficient permissions to view this game");
		
		UUID id = acc.getId();
		if (lastResultCheckForAccount.containsKey(id))
		{
			int ret = lastResultCheckForAccount.get(id);
			lastResultCheckForAccount.put(id, resultsChangeCount);
			return ret != resultsChangeCount;
		}
		lastResultCheckForAccount.put(id, resultsChangeCount);
		return true;
	}
	
	public void noteInteraction(Account account)
	{
		noteInteraction(account.getId());
	}
	private void noteInteraction(UUID accountID)
	{
		synchronized (activePlayers)
		{
			if (activePlayers.stream()
					.anyMatch(acc -> acc.getId().equals(accountID))
					|| spectators.stream()
						.anyMatch(acc -> acc.getId().equals(accountID)))
				lastInteractions.put(accountID, System.nanoTime());
		}
	}
	
	public void removeInactivePlayers(long maxTimeSinceLastInteractionMilli)
	{
		synchronized (activePlayers)
		{
			Iterator<Account> it = activePlayers.iterator();
			while (it.hasNext())
			{
				Account player = it.next();
				if (isPlayerInactive(player, maxTimeSinceLastInteractionMilli)
						&& !hasGameFinishedFor(player))
					it.remove();
			}
		}
	}
	private boolean isPlayerInactive(Account acc, long maxTimeSinceLastInteractionMilli)
	{
		long diff = System.nanoTime() - lastInteractions.getOrDefault(acc.getId(), 0l);
		return (diff / NANOS_IN_MILLI) > maxTimeSinceLastInteractionMilli;
	}

	public boolean isInactive()
	{
		return activePlayers.isEmpty();
	}
	public void markInactive(Account acc)
	{
		lastInteractions.remove(acc.getId());
	}
	
	private <T, U> Map<T, U> syncMap()
	{
		return synchronizedMap(new HashMap<>());
	}
	private <T, U> Map<T, U> syncMap(Map<T, U> map)
	{
		return synchronizedMap(new HashMap<>(map));
	}
	private <T> List<T> syncList(List<T> list)
	{
		return synchronizedList(new ArrayList<>(list));
	}
}
