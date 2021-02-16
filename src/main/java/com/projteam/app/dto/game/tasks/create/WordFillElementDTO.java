package com.projteam.app.dto.game.tasks.create;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WordFillElementDTO
{
	private List<String> text;
	private List<EmptySpaceDTO> emptySpaces;
	private boolean startWithText;
	private List<String> possibleAnswers;
	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class EmptySpaceDTO
	{
		private String answer;
	}
}
