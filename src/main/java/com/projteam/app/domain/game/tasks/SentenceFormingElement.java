package com.projteam.app.domain.game.tasks;

import java.util.List;
import java.util.UUID;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class SentenceFormingElement
{
	private @Id UUID id;
	private @ElementCollection List<String> words;
	
	public SentenceFormingElement()
	{}
	public SentenceFormingElement(UUID id, List<String> words)
	{
		this.id = id;
		this.words = words;
	}
	
	public UUID getId()
	{
		return id;
	}
	public List<String> getWords()
	{
		return words;
	}
	public void setId(UUID id)
	{
		this.id = id;
	}
	public void setWords(List<String> words)
	{
		this.words = words;
	}
}
