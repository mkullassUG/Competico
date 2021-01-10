package com.projteam.app.domain.game.tasks;

import java.util.List;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import com.projteam.app.domain.game.tasks.answers.TaskAnswer;

@Entity
public class ListSentenceForming implements Task
{
	private @Id UUID id;
	private @OneToMany List<SentenceFormingElement> rows;

	private double difficulty;
	
	public ListSentenceForming()
	{}
	public ListSentenceForming(UUID id, List<SentenceFormingElement> rows, double difficulty)
	{
		this.id = id;
		this.rows = rows;
		this.difficulty = difficulty;
	}
	
	public UUID getId()
	{
		return id;
	}
	public List<SentenceFormingElement> getRows()
	{
		return rows;
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
	public void setRows(List<SentenceFormingElement> rows)
	{
		this.rows = rows;
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
