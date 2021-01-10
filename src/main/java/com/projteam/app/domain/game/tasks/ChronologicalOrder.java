package com.projteam.app.domain.game.tasks;

import java.util.List;
import java.util.UUID;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import com.projteam.app.domain.game.tasks.answers.TaskAnswer;

@Entity
public class ChronologicalOrder implements Task
{
	private @Id UUID id;
	private @ElementCollection List<String> sentences;
	
	private double difficulty;

	public ChronologicalOrder()
	{}
	public ChronologicalOrder(UUID id, List<String> sentences, double difficulty)
	{
		this.id = id;
		this.sentences = sentences;
		this.difficulty = difficulty;
	}
	
	public UUID getId()
	{
		return id;
	}
	public List<String> getSentences()
	{
		return sentences;
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
	public void setSentences(List<String> sentences)
	{
		this.sentences = sentences;
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
