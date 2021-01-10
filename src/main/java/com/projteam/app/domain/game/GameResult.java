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

@Entity
public class GameResult
{
	private @Id @Column(name = "gameID", unique = true) UUID playerID;
	
	@ElementCollection
	@CollectionTable(name = "score_mapping")
	@MapKeyColumn(name = "taskID")
	@Column(name = "scores")
	private Map<String, Long> scores;

	public GameResult()
	{
		scores = new HashMap<>();
	}
	public GameResult(UUID playerID)
	{
		this.playerID = playerID;
		scores = new HashMap<>();
	}
	public GameResult(Map<String, Long> scores)
	{
		this.scores = new HashMap<>(scores);
	}
	public GameResult(UUID playerID, Map<String, Long> scores)
	{
		this.playerID = playerID;
		this.scores = new HashMap<>(scores);
	}
	
	public Map<String, Long> getScores()
	{
		return scores;
	}
	public void setScores(Map<String, Long> scores)
	{
		this.scores = new HashMap<>(scores);
	}
	
	public void addScore(String taskName, long score)
	{
		scores.put(taskName, score);
	}
	public boolean removeScore(String taskName)
	{
		return scores.remove(taskName) != null;
	}
	public boolean containsScoreForTask(String taskName)
	{
		return scores.containsKey(taskName);
	}
	public long getScore(String taskName)
	{
		return scores.get(taskName);
	}
	public long getTotalScore()
	{
		return scores.values()
				.stream()
				.reduce((s1, s2) -> s1 + s2)
				.orElse(0l);
	}
	public UUID getPlayerID()
	{
		return playerID;
	}
	public void setPlayerID(UUID playerID)
	{
		this.playerID = playerID;
	}
}
