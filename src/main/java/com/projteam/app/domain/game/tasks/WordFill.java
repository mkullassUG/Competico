package com.projteam.app.domain.game.tasks;

import java.util.List;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import com.projteam.app.domain.game.tasks.WordFillElement.EmptySpace;
import com.projteam.app.domain.game.tasks.answers.TaskAnswer;

@Entity
public class WordFill implements Task
{
	private @Id UUID id;
	private @ManyToOne WordFillElement content;
	
	private double difficulty;
	
	public WordFill()
	{}
	public WordFill(UUID id, WordFillElement content, double difficulty)
	{
		this.id = id;
		this.content = content;
		this.difficulty = difficulty;
	}

	public UUID getId()
	{
		return id;
	}
	public WordFillElement getContent()
	{
		return content;
	}
	public void setId(UUID id)
	{
		this.id = id;
	}
	public void setContent(WordFillElement content)
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
	public List<EmptySpace> getEmptySpaces()
	{
		return content.getEmptySpaces();
	}
	public List<String> getPossibleAnswers()
	{
		return content.getPossibleAnswers();
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
