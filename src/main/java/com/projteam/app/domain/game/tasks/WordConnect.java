package com.projteam.app.domain.game.tasks;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import com.projteam.app.domain.game.tasks.answers.TaskAnswer;

@Entity
public class WordConnect implements Task
{
	private @Id UUID id;
	private @ElementCollection List<String> leftWords;
	private @ElementCollection List<String> rightWords;
	private @ElementCollection Map<Integer, Integer> correctMapping;
	
	private double difficulty;

	public WordConnect()
	{}
	public WordConnect(UUID id, List<String> leftWords, List<String> rightWords, Map<Integer, Integer> correctMapping, double difficulty)
	{
		this.id = id;
		this.leftWords = leftWords;
		this.rightWords = rightWords;
		this.correctMapping = correctMapping;
		this.difficulty = difficulty;
	}
	
	public UUID getId()
	{
		return id;
	}
	public List<String> getLeftWords()
	{
		return leftWords;
	}
	public List<String> getRightWords()
	{
		return rightWords;
	}
	public Map<Integer, Integer> getCorrectMapping()
	{
		return correctMapping;
	}
	@Override
	public double getDifficulty()
	{
		return difficulty;
	}

	public void setId(UUID id)
	{
		this.id = id;
	}
	public void setLeftWords(List<String> leftWords)
	{
		this.leftWords = leftWords;
	}
	public void setRightWords(List<String> rightWords)
	{
		this.rightWords = rightWords;
	}
	public void setCorrectMapping(Map<Integer, Integer> correctMapping)
	{
		this.correctMapping = correctMapping;
	}
	public void setDifficulty(double difficulty)
	{
		this.difficulty = difficulty;
	}
	
	@Override
	public void acceptAnswer(TaskAnswer answer)
	{
		//TODO implement
	}
}
