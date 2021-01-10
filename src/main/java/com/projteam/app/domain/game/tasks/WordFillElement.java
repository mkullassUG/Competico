package com.projteam.app.domain.game.tasks;

import java.util.List;
import java.util.UUID;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class WordFillElement
{
	private @Id UUID id;
	private @ElementCollection List<String> text;
	private @ElementCollection List<EmptySpace> emptySpaces;
	private boolean startWithText;
	private @ElementCollection List<String> possibleAnswers;
	
	public WordFillElement()
	{}
	public WordFillElement(UUID id,
			List<String> text,
			List<EmptySpace> emptySpaces,
			boolean startWithText,
			List<String> possibleAnswers)
	{
		this.id = id;
		this.text = text;
		this.emptySpaces = emptySpaces;
		this.startWithText = startWithText;
		this.possibleAnswers = possibleAnswers;
	}
	
	public UUID getId()
	{
		return id;
	}
	public List<String> getText()
	{
		return text;
	}
	public List<EmptySpace> getEmptySpaces()
	{
		return emptySpaces;
	}
	public List<String> getPossibleAnswers()
	{
		return possibleAnswers;
	}
	public boolean isStartWithText()
	{
		return startWithText;
	}

	public void setId(UUID id)
	{
		this.id = id;
	}
	public void setText(List<String> text)
	{
		this.text = text;
	}
	public void setEmptySpaces(List<EmptySpace> emptySpaces)
	{
		this.emptySpaces = emptySpaces;
	}
	public void setPossibleAnswers(List<String> possibleAnswers)
	{
		this.possibleAnswers = possibleAnswers;
	}
	public void setStartWithText(boolean startWithText)
	{
		this.startWithText = startWithText;
	}
	
	@Embeddable
	public static class EmptySpace
	{
		private String answer;
		
		public EmptySpace()
		{}
		public EmptySpace(String answer)
		{
			this.answer = answer;
		}
		
		public String getAnswer()
		{
			return answer;
		}
		public void setAnswer(String answer)
		{
			this.answer = answer;
		}
	}
}
