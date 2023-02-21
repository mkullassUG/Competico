package com.projteam.competico.domain.game;

import static com.projteam.competico.domain.Account.PLAYER_ROLE;
import static com.projteam.competico.domain.Account.LECTURER_ROLE;
import static java.util.Collections.synchronizedList;
import static java.util.Collections.synchronizedMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import com.projteam.competico.domain.Account;
import com.projteam.competico.domain.group.GroupLobbySettings;

public class Lobby
{
	private String gameCode;
	private Account host;
	private List<Account> players;
	private int maxPlayerCount;
	private GroupLobbySettings groupLobbySettings;
	
	private int changeCount;
	private Map<UUID, Integer> playerLastChangeCounts;
	
	private Map<UUID, Long> lastInteractions;
	
	private static final long NANOS_IN_MILLI = 1000000;
	private static final int DEFAULT_MAX_PLAYER_COUNT = 20;
	
	public Lobby(String gameCode, Account host)
	{
		this(gameCode, host, DEFAULT_MAX_PLAYER_COUNT);
	}
	public Lobby(String gameCode, Account host, int maxPlayerCount)
	{
		this.gameCode = gameCode;
		this.host = host;
		players = syncList();
		
		this.maxPlayerCount = maxPlayerCount;
		
		changeCount = 0;
		playerLastChangeCounts = syncMap();
		
		lastInteractions = syncMap();
		
		noteInteraction(host);
	}
	
	public Lobby(String gameCode, Account host, UUID groupId, String groupCode)
	{
		this(gameCode, host, DEFAULT_MAX_PLAYER_COUNT, groupId, groupCode);
	}
	public Lobby(String gameCode, Account host,
			int maxPlayerCount, UUID groupId, String groupCode)
	{
		this(gameCode, host, maxPlayerCount);
		
		groupLobbySettings = new GroupLobbySettings(groupId, groupCode, null);
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
		if (players.contains(player))
			return false;
		if (!canAcceptPlayer())
			return false;
		synchronized (players)
		{
			if ((player.hasRole(PLAYER_ROLE)
				|| player.hasRole(LECTURER_ROLE))
					&& players.add(player))
			{
				changeOccurred();
				noteInteraction(player.getId());
				return true;
			}
			return false;
		}
	}
	public boolean removePlayer(Account player)
	{
		if (players.remove(player))
		{
			UUID id = player.getId();
			playerLastChangeCounts.remove(id);
			lastInteractions.remove(id);
			changeOccurred();
			return true;
		}
		return false;
	}
	public boolean removePlayer(Account player, Account requestSource)
	{
		noteInteraction(requestSource.getId());
		if (host.equals(requestSource))
			return removePlayer(player);
		return false;
	}
	public int getMaximumPlayerCount()
	{
		return maxPlayerCount;
	}
	public boolean setMaximumPlayerCount(int maxPlayers, Account requestSource)
	{
		if (!isHost(requestSource))
			return false;
		if (players.size() <= maxPlayers)
		{
			maxPlayerCount = maxPlayers;
			return true;
		}
		return false;
	}
	public boolean isGroupLobby()
	{
		return groupLobbySettings != null;
	}
	public List<String> getTasksetNames()
	{
		return Optional.ofNullable(groupLobbySettings)
				.map(gls -> Optional.ofNullable(gls.getSelectedTasksets()))
				.map(v -> v.stream())
				.map(v -> v.flatMap(list -> list.stream()))
				.map(v -> v.map(ts -> ts.getName()))
				.map(v -> v.collect(Collectors.toList()))
				.orElse(null);
	}
	public List<TaskSet> getTasksets()
	{
		return Optional.ofNullable(groupLobbySettings)
				.map(gls -> Optional.ofNullable(gls.getSelectedTasksets()))
				.map(v -> v.stream())
				.map(v -> v.flatMap(list -> list.stream()))
				.map(v -> v.collect(Collectors.toList()))
				.orElse(null);
	}
	@Transactional
	public void setTasksets(List<TaskSet> tasksets)
	{
		GroupLobbySettings gls = Optional.ofNullable(groupLobbySettings)
				.orElseThrow(() -> new IllegalArgumentException("NOT_GROUP_LOBBY"));
		
		gls.setSelectedTasksets(new ArrayList<>(tasksets));
	}
	public Optional<UUID> getGroupId()
	{
		return Optional.ofNullable(groupLobbySettings)
				.map(gls -> gls.getGroupId());
	}
	public Optional<String> getGroupCode()
	{
		return Optional.ofNullable(groupLobbySettings)
				.map(gls -> gls.getGroupCode());
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
		noteInteraction(accountID);
		
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
	
	public void noteInteraction(Account account)
	{
		noteInteraction(account.getId());
	}
	private void noteInteraction(UUID accountID)
	{
		synchronized (players)
		{
			if (host.getId().equals(accountID)
					|| players.stream()
						.anyMatch(acc -> acc.getId().equals(accountID)))
				lastInteractions.put(accountID, System.nanoTime());
		}
	}
	
	private <T, U> Map<T, U> syncMap()
	{
		return synchronizedMap(new HashMap<>());
	}
	private <T> List<T> syncList()
	{
		return synchronizedList(new ArrayList<>());
	}
	
	public void removeInactivePlayers(long maxTimeSinceLastInteractionMilli)
	{
		synchronized (players)
		{
			Iterator<Account> it = players.iterator();
			while (it.hasNext())
			{
				Account player = it.next();
				if (isAccountInactive(player, maxTimeSinceLastInteractionMilli))
				{
					it.remove();
					UUID id = player.getId();
					playerLastChangeCounts.remove(id);
					lastInteractions.remove(id);
					changeOccurred();
				}
			}
		}
	}
	public boolean isInactive(long maxTimeSinceLastInteractionMilli)
	{
		return isAccountInactive(host,maxTimeSinceLastInteractionMilli);
	}
	public void markInactive(Account acc)
	{
		synchronized (players)
		{
			lastInteractions.remove(acc.getId());
		}
	}
	private boolean isAccountInactive(Account acc, long maxTimeSinceLastInteractionMilli)
	{
		UUID id = acc.getId();
		if (!lastInteractions.containsKey(id))
			return true;
		long diff = System.nanoTime() - lastInteractions.get(id);
		return (diff / NANOS_IN_MILLI) > maxTimeSinceLastInteractionMilli;
	}
}
