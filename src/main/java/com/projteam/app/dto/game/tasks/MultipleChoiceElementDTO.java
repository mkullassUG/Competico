package com.projteam.app.dto.game.tasks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.projteam.app.domain.game.tasks.MultipleChoiceElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MultipleChoiceElementDTO implements TaskDTO
{
	private String content;
	private List<String> answers;
	
	public MultipleChoiceElementDTO(MultipleChoiceElement elem)
	{
		content = elem.getContent();
		answers = new ArrayList<>(elem.getCorrectAnswers());
		answers.addAll(elem.getIncorrectAnswers());
		Collections.shuffle(answers);
	}
}
