package com.projteam.app.domain.game;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.MapKeyColumn;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class GameResults
{
	private @Id @Column(name = "gameID", unique = true) UUID gameID;
	
	@ElementCollection
	@CollectionTable(name = "result_mapping")
	@MapKeyColumn(name = "playerID")
	@Column(name = "results")
	private Map<UUID, GameResult> results = new HashMap<>();
	
	public GameResults(UUID gameID)
	{
		this.gameID = gameID;
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
