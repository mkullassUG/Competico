package com.projteam.app.dto.game.tasks;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import com.projteam.app.domain.game.tasks.ChoiceWordFillElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChoiceWordFillElementDTO implements TaskDTO
{
	private List<String> text;
	private List<List<String>> wordChoices;
	private boolean startWithText;

	public ChoiceWordFillElementDTO(ChoiceWordFillElement elem)
	{
		text = new ArrayList<>(elem.getText());
		wordChoices = elem.getWordChoices()
				.stream()
				.map(wc -> 
				{
					List<String> ret = new ArrayList<>(wc.getInncorrectAnswers());
					ret.add(wc.getCorrectAnswer());
					return ret;
				})
				.collect(Collectors.toList());
		startWithText = elem.isStartWithText();
	}
}
