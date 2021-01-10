package com.projteam.app.domain.game.tasks;

import java.util.List;
import java.util.UUID;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class MultipleChoiceElement
{
	private @Id UUID id;
	private String content;
	private @ElementCollection List<String> correctAnswers;
	private @ElementCollection List<String> incorrectAnswers;
	
	public MultipleChoiceElement()
	{}

	public MultipleChoiceElement(UUID id, String content, List<String> correctAnswers, List<String> incorrectAnswers)
	{
		this.id = id;
		this.content = content;
		this.correctAnswers = correctAnswers;
		this.incorrectAnswers = incorrectAnswers;
	}

	public UUID getId()
	{
		return id;
	}
	public String getContent()
	{
		return content;
	}
	public List<String> getCorrectAnswers()
	{
		return correctAnswers;
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
	public void setCorrectAnswers(List<String> correctAnswers)
	{
		this.correctAnswers = correctAnswers;
	}
	public void setIncorrectAnswers(List<String> incorrectAnswers)
	{
		this.incorrectAnswers = incorrectAnswers;
	}
}
