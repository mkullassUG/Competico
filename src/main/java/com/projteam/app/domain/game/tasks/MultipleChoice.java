package com.projteam.app.domain.game.tasks;

import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import com.projteam.app.domain.game.tasks.answers.TaskAnswer;

@Entity
public class MultipleChoice implements Task
{
	private @Id UUID id;
	private @ManyToOne MultipleChoiceElement content;
	
	private double difficulty;

	public MultipleChoice()
	{}
	public MultipleChoice(UUID id, MultipleChoiceElement content, double difficulty)
	{
		this.id = id;
		this.content = content;
		this.difficulty = difficulty;
	}
	
	public UUID getId()
	{
		return id;
	}
	public MultipleChoiceElement getContent()
	{
		return content;
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
	public void setContent(MultipleChoiceElement content)
	{
		this.content = content;
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
