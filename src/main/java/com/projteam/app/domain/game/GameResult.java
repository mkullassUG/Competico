package com.projteam.app.domain.game;

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
@Table(name = "GameResult")
@Access(AccessType.FIELD)
public class GameResult
{
	private @Id UUID playerResultID;
	private @Column(name = "playerID") UUID playerID;
	
	@ElementCollection
	@CollectionTable(name = "completion_mapping")
	@MapKeyColumn(name = "taskNumber")
	@Column(name = "completion")
	private Map<Integer, Double> completion = new HashMap<>();
	@ElementCollection
	@CollectionTable(name = "difficulty_mapping")
	@MapKeyColumn(name = "taskNumber")
	@Column(name = "difficulty")
	private Map<Integer, Double> difficulty = new HashMap<>();
	@ElementCollection
	@CollectionTable(name = "timeTaken_mapping")
	@MapKeyColumn(name = "taskNumber")
	@Column(name = "timeTaken")
	private Map<Integer, Long> timeTaken = new HashMap<>();
}
