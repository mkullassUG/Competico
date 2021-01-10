package com.projteam.app.domain.game.tasks;

import java.util.List;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import com.projteam.app.domain.game.tasks.answers.TaskAnswer;

@Entity
public class ListWordFill implements Task
{
	private @Id UUID id;
	private @OneToMany List<WordFillElement> rows;
	
	private double difficulty;

	public ListWordFill()
	{}
	public ListWordFill(UUID id, List<WordFillElement> rows, double difficulty)
	{
		this.id = id;
		this.rows = rows;
		this.difficulty = difficulty;
	}

	public UUID getId()
	{
		return id;
	}
	public List<WordFillElement> getRows()
	{
		return rows;
	}
	
	public void setId(UUID id)
	{
		this.id = id;
	}
	public void setRows(List<WordFillElement> rows)
	{
		this.rows = rows;
	}
	public void setDifficulty(double difficulty)
	{
		this.difficulty = difficulty;
	}

	@Override
	public double getDifficulty()
	{
		return difficulty;
	}
	@Override
	public void acceptAnswer(TaskAnswer answer)
	{
		//TODO implement
	}
}