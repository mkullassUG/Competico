package com.projteam.app.domain.game.tasks;

import java.util.List;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import com.projteam.app.domain.game.tasks.answers.TaskAnswer;

@Entity
public class ListChoiceWordFill implements Task
{
	private @Id UUID id;
	private @OneToMany List<ChoiceWordFillElement> rows;
	
	private double difficulty;
	
	public ListChoiceWordFill()
	{}
	public ListChoiceWordFill(UUID id, List<ChoiceWordFillElement> rows, double difficulty)
	{
		this.id = id;
		this.rows = rows;
		this.difficulty = difficulty;
	}
	
	public UUID getId()
	{
		return id;
	}
	public List<ChoiceWordFillElement> getRows()
	{
		return rows;
	}
	public void setId(UUID id)
	{
		this.id = id;
	}
	public void setRows(List<ChoiceWordFillElement> rows)
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