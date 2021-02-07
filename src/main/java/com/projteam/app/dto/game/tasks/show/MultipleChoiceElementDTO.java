package com.projteam.app.dto.game.tasks.show;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.projteam.app.domain.game.tasks.MultipleChoiceElement;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
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
