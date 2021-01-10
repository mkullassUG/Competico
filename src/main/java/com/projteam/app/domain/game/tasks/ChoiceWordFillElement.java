package com.projteam.app.domain.game.tasks;

import java.util.List;
import java.util.UUID;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class ChoiceWordFillElement
{
	private @Id UUID id;
	private @ElementCollection List<String> text;
	private @OneToMany List<WordChoice> wordChoices;
	
	public ChoiceWordFillElement()
	{}
	public ChoiceWordFillElement(UUID id, List<String> text, List<WordChoice> wordChoices)
	{
		this.id = id;
		this.text = text;
		this.wordChoices = wordChoices;
	}

	public UUID getId()
	{
		return id;
	}
	public List<String> getText()
	{
		return text;
	}
	public List<WordChoice> getWordChoices()
	{
		return wordChoices;
	}
	
	public void setId(UUID id)
	{
		this.id = id;
	}
	public void setText(List<String> text)
	{
		this.text = text;
	}
	public void setWordChoices(List<WordChoice> wordChoices)
	{
		this.wordChoices = wordChoices;
	}

	@Entity
	public static class WordChoice
	{
		private @Id UUID id;
		private String correctAnswer;
		private @ElementCollection List<String> inncorrectAnswers;
		
		public WordChoice()
		{}
		public WordChoice(UUID id, String correctAnswer, List<String> inncorrectAnswers)
		{
			this.id = id;
			this.correctAnswer = correctAnswer;
			this.inncorrectAnswers = inncorrectAnswers;
		}

		public UUID getId()
		{
			return id;
		}
		public String getCorrectAnswer()
		{
			return correctAnswer;
		}
		public List<String> getInncorrectAnswers()
		{
			return inncorrectAnswers;
		}
		public void setId(UUID id)
		{
			this.id = id;
		}
		public void setCorrectAnswer(String correctAnswer)
		{
			this.correctAnswer = correctAnswer;
		}
		public void setInncorrectAnswers(List<String> inncorrectAnswers)
		{
			this.inncorrectAnswers = inncorrectAnswers;
		}
	}
}