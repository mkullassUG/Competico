package com.projteam.app.domain.game.tasks.answers;

import java.util.List;

public class WordFillAnswer implements TaskAnswer
{
	private List<String> answer;

	public WordFillAnswer(List<String> answer)
	{
		this.answer = answer;
	}
	
	public List<String> getAnswer()
	{
		return answer;
	}
	public void setAnswer(List<String> answer)
	{
		this.answer = answer;
	}
}