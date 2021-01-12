package com.projteam.app.domain.game;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.MapKeyColumn;
import lombok.Data;

@Data
@Entity
public class GameResults
{
	private @Id @Column(name = "gameID", unique = true) UUID gameID;
	
	@ElementCollection
	@CollectionTable(name = "result_mapping")
	@MapKeyColumn(name = "playerID")
	@Column(name = "results")
	private Map<UUID, GameResult> results;

	public GameResults()
	{
		results = new HashMap<>();
	}
	public GameResults(UUID gameID)
	{
		this.gameID = gameID;
		results = new HashMap<>();
	}
	public GameResults(List<GameResult> results)
	{
		this.results = new HashMap<>(results.stream()
				.collect(Collectors.toMap(GameResult::getPlayerID, gr -> gr)));
	}
	public GameResults(UUID gameID, List<GameResult> results)
	{
		this.gameID = gameID;
		this.results = new HashMap<>(results.stream()
				.collect(Collectors.toMap(GameResult::getPlayerID, gr -> gr)));
	}
	public GameResults(Map<UUID, GameResult> results)
	{
		this.results = new HashMap<>(results);
	}
	public GameResults(UUID gameID, Map<UUID, GameResult> results)
	{
		this.gameID = gameID;
		this.results = new HashMap<>(results);
	}
	
	public void addResult(GameResult gr)
	{
		results.put(gr.getPlayerID(), gr);
	}
	public void removeResult(UUID playerID)
	{
		results.remove(playerID);
	}
}
