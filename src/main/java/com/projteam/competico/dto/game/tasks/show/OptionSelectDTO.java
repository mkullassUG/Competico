package com.projteam.competico.dto.game.tasks.show;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.projteam.competico.domain.game.tasks.OptionSelectElement;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OptionSelectDTO implements TaskDTO
{
	private String content;
	private List<String> answers;
	
	public OptionSelectDTO(OptionSelectElement elem)
	{
		content = elem.getContent();
		answers = new ArrayList<>(elem.getCorrectAnswers());
		answers.addAll(elem.getIncorrectAnswers());
		Collections.shuffle(answers);
	}
}
