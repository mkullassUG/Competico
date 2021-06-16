package com.projteam.competico.dto.game.tasks.create;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChoiceWordFillElementDTO
{
	private List<String> text;
	private List<WordChoiceDTO> wordChoices;
	private boolean startWithText;
	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class WordChoiceDTO
	{
		private String correctAnswer;
		private List<String> incorrectAnswers;
	}
}
