package com.projteam.app.domain.game;

import static java.util.Collections.synchronizedMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "GameResults")
@Access(AccessType.FIELD)
public class GameResult
{
	private @Id UUID gameID;
	
	@ElementCollection
	@CollectionTable(name = "result_mapping")
	@MapKeyColumn(name = "playerID")
	@Column(name = "results")
	private Map<UUID, PlayerResult> results = synchronizedMap(new HashMap<>());
	
	public GameResult(UUID gameID)
	{
		this.gameID = gameID;
	}
	
	public void addResult(PlayerResult pr)
	{
		results.put(pr.getPlayerID(), pr);
	}
	public void removeResult(UUID playerID)
	{
		results.remove(playerID);
	}
}
