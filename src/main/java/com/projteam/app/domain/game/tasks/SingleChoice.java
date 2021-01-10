package com.projteam.app.domain.game.tasks;

import java.util.List;
import java.util.UUID;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import com.projteam.app.domain.game.tasks.answers.TaskAnswer;

@Entity
public class SingleChoice implements Task
{
	private @Id UUID id;
	private String content;
	private String answer;
	private @ElementCollection List<String> incorrectAnswers;
	
	private double difficulty;

	public SingleChoice()
	{}
	public SingleChoice(UUID id, String content, String answer, List<String> incorrectAnswers, double difficulty)
	{
		this.id = id;
		this.content = content;
		this.answer = answer;
		this.incorrectAnswers = incorrectAnswers;
		this.difficulty = difficulty;
	}

	public UUID getId()
	{
		return id;
	}
	public String getContent()
	{
		return content;
	}
	public String getAnswer()
	{
		return answer;
	}
	public List<String> getIncorrectAnswers()
	{
		return incorrectAnswers;
	}
	public void setId(UUID id)
	{
		this.id = id;
	}
	public void setContent(String content)
	{
		this.content = content;
	}
	public void setAnswer(String answer)
	{
		this.answer = answer;
	}
	public void setIncorrectAnswers(List<String> incorrectAnswers)
	{
		this.incorrectAnswers = incorrectAnswers;
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
