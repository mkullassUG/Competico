package com.projteam.app.dto.game.tasks;

import java.util.ArrayList;
import java.util.List;
import com.projteam.app.domain.game.tasks.WordFillElement;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class WordFillElementDTO implements TaskDTO
{
	private List<String> text;
	private int emptySpaceCount;
	private boolean startWithText;
	private List<String> possibleAnswers;
	
	public WordFillElementDTO(WordFillElement elem)
	{
		text = new ArrayList<>(elem.getText());
		emptySpaceCount = elem.getEmptySpaces().size();
		startWithText = elem.isStartWithText();
		possibleAnswers = elem.getPossibleAnswers();
	}
}
