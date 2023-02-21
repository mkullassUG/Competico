package com.projteam.competico.domain.game;

import static java.util.Collections.synchronizedList;
import static java.util.Collections.synchronizedMap;
import static java.util.stream.Collectors.toMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import com.projteam.competico.domain.Account;
import com.projteam.competico.domain.game.tasks.Task;
import com.projteam.competico.dto.game.GameResultPersonalDTO;
import com.projteam.competico.dto.game.GameResultTotalDuringGameDTO;

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
	
	private int stateChangeCount;
	private Map<UUID, Integer> lastResultCheckForAccount;
	
	private Map<UUID, Long> lastInteractions;
	
	private Map<UUID, Optional<Long>> taskStartTime;
	private Map<UUID, Map<Integer, Long>> timeTakenForTasks;
	
	private UUID groupID;
	
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
		
		stateChangeCount = 0;
		lastResultCheckForAccount = syncMap();
		
		lastInteractions = syncMap();
		players.forEach(p -> noteInteraction(p));
		
		taskStartTime = syncMap(players.stream()
				.collect(Collectors.toMap(p -> p.getId(), p -> Optional.empty())));
		timeTakenForTasks = syncMap(players.stream()
				.collect(Collectors.toMap(p -> p.getId(), p -> syncMap())));
	}
	public Game(List<Account> players,
			List<Account> spectators,
			int taskCount,
			Map<UUID, List<Task>> taskMap,
			UUID groupId)
	{
		this(players, spectators, taskCount, taskMap);
		
		this.groupID = Objects.requireNonNull(groupId);
	}
	
	public UUID getID()
	{
		return gameID;
	}
	public Task getCurrentTask(Account player)
	{
		UUID id = player.getId();
		noteInteraction(id);
		if (taskStartTime.get(id).isEmpty())
			taskStartTime.put(id, Optional.of(System.nanoTime()));
		return taskMap.get(id).get(currentTaskNumber.get(id));
	}

	public void advance(Account player, double completion)
	{
		UUID playerId = player.getId();
		noteInteraction(playerId);
		Integer taskNumber = currentTaskNumber.get(playerId);
		
		taskCompletionMap.get(playerId).put(taskNumber, completion);
		currentTaskNumber.put(playerId, taskNumber + 1);
		
		timeTakenForTasks.get(playerId).put(taskNumber,
				taskStartTime.get(playerId)
					.map(t -> (System.nanoTime() - t) / NANOS_IN_MILLI)
					.orElse(0l));
		taskStartTime.put(playerId, Optional.empty());
		
		stateChangeCount++;
	}

	public boolean hasGameFinishedFor(Account player)
	{
		UUID id = player.getId();
		noteInteraction(id);
		return currentTaskNumber.containsKey(id)?
				(currentTaskNumber.get(id) >= taskCount):true;
	}
	public boolean hasGameFinished()
	{
		Set<UUID> activePlayerIDs = new HashSet<>(activePlayers
				.stream()
				.map(acc -> acc.getId())
				.collect(Collectors.toSet()));
		return currentTaskNumber.entrySet()
				.stream()
				.filter(e -> activePlayerIDs.contains(e.getKey()))
				.map(e -> e.getValue())
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
	public Optional<UUID> getGroupId()
	{
		return Optional.ofNullable(groupID);
	}
	public GameResult createGameResult()
	{
		GameResult gr = new GameResult(gameID);
		for (Account player: originalPlayers)
		{
			UUID gameResultId = UUID.randomUUID();
			UUID playerId = player.getId();
			Map<Integer, Double> completion = new HashMap<>(taskCompletionMap.get(playerId));
			Map<Integer, Double> difficulty = new HashMap<>();
			int i = 0;
			for (Task t: taskMap.get(playerId))
			{
				difficulty.put(i, t.getDifficulty());
				i++;
			}
			Map<Integer, Long> timeTaken = new HashMap<>(timeTakenForTasks.get(playerId));
			
			boolean removedForInactivity = !activePlayers.contains(player);
			
			PlayerResult pr = new PlayerResult(
					gameResultId, playerId, completion,
					difficulty, timeTaken, removedForInactivity);
			gr.addResult(pr);
		}
		return gr;
	}

	public List<GameResultTotalDuringGameDTO> getCurrentResults()
	{
		return new ArrayList<>(getCurrentResultsWithIDs()
				.entrySet()
				.stream()
				.map(e -> e.getValue())
				.sorted((r1, r2) -> -Long.compare(
						r1.getTotalScore(),
						r2.getTotalScore()))
				.collect(Collectors.toList()));
	}
	public Map<UUID, GameResultTotalDuringGameDTO> getCurrentResultsWithIDs()
	{
		Map<UUID, GameResultTotalDuringGameDTO> ret = new HashMap<>();
		for (Account player: originalPlayers)
		{
			UUID playerId = player.getId();
			int currentTask = currentTaskNumber.get(playerId);
			
			List<Task> tasks = taskMap.get(playerId);
			Map<Integer, Double> completion = taskCompletionMap.get(playerId);
			double score = 0;
			
			Map<Integer, Long> timeTaken = timeTakenForTasks.get(playerId);
			long totalTime = 0;
			for (int i = 0; i < currentTask; i++)
			{
				long time =  timeTaken.get(i);
				score += calculateScore(completion.get(i), tasks.get(i).getDifficulty(), time);
				totalTime += time;
			}
			boolean hasFinished = currentTask >= taskCount;
			boolean isActive = activePlayers.contains(player);
			
			ret.put(playerId,
					new GameResultTotalDuringGameDTO(
						player.getUsername(),
						player.getNickname(), 
						(long) score,
						totalTime,
						hasFinished,
						!isActive));
		}
		return ret;
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
		
		Map<Integer, Long> timeTaken = timeTakenForTasks.get(playerId);
		int l = Math.min(completion.size(),
				Math.min(difficulty.size(),
						timeTaken.size()));
		
		//TODO include in game result
//		boolean isActive = activePlayers.contains(player);
		
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
		return (1 + timeBonus(timeTaken)) * completion * difficulty;
	}
	private static double timeBonus(long time)
	{
		return limit(1 - ((time - 1000) / 59000.0), 0, 1);
	}
	private static double limit(double x, double min, double max)
	{
		return Math.max(Math.min(x, max), min);
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
	public Optional<Boolean> hasStateChanged(UUID gameID, Account acc)
	{
		if (!containsPlayerOrSpectator(acc))
			return Optional.empty();
		
		UUID id = acc.getId();
		if (lastResultCheckForAccount.containsKey(id))
		{
			int ret = lastResultCheckForAccount.get(id);
			lastResultCheckForAccount.put(id, stateChangeCount);
			return Optional.of(ret != stateChangeCount);
		}
		lastResultCheckForAccount.put(id, stateChangeCount);
		return Optional.of(true);
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
				{
					it.remove();
					stateChangeCount++;
				}
			}
		}
	}
	private boolean isPlayerInactive(Account acc, long maxTimeSinceLastInteractionMilli)
	{
		UUID id = acc.getId();
		if (!lastInteractions.containsKey(id))
			return true;
		long diff = System.nanoTime() - lastInteractions.get(id);
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
