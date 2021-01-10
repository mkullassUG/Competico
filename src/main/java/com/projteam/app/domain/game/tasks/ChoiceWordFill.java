package com.projteam.app.domain.game.tasks;

import java.util.List;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import com.projteam.app.domain.game.tasks.ChoiceWordFillElement.WordChoice;
import com.projteam.app.domain.game.tasks.answers.TaskAnswer;

@Entity
public class ChoiceWordFill implements Task
{
	private @Id UUID id;
	private @ManyToOne ChoiceWordFillElement content;
	
	private double difficulty;
	
	public ChoiceWordFill()
	{}
	public ChoiceWordFill(UUID id, ChoiceWordFillElement content, double difficulty)
	{
		this.id = id;
		this.content = content;
		this.difficulty = difficulty;
	}

	public UUID getId()
	{
		return id;
	}
	public ChoiceWordFillElement getContent()
	{
		return content;
	}
	public void setId(UUID id)
	{
		this.id = id;
	}
	public void setContent(ChoiceWordFillElement content)
	{
		this.content = content;
	}
	public void setDifficulty(double difficulty)
	{
		this.difficulty = difficulty;
	}
	
	public List<String> getText()
	{
		return content.getText();
	}
	public List<WordChoice> getWordChoices()
	{
		return content.getWordChoices();
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
