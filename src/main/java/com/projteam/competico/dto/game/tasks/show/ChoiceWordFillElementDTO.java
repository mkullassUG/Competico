package com.projteam.competico.dto.game.tasks.show;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import com.projteam.competico.domain.game.tasks.ChoiceWordFillElement;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
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
					List<String> ret = new ArrayList<>(wc.getIncorrectAnswers());
					ret.add(wc.getCorrectAnswer());
					Collections.shuffle(ret);
					return ret;
				})
				.collect(Collectors.toList());
		startWithText = elem.isStartWithText();
	}
}
