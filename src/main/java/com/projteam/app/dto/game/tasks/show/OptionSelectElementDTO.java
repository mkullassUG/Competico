package com.projteam.app.dto.game.tasks.show;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.projteam.app.domain.game.tasks.OptionSelectElement;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OptionSelectElementDTO implements TaskDTO
{
	private String content;
	private List<String> answers;
	
	public OptionSelectElementDTO(OptionSelectElement elem)
	{
		content = elem.getContent();
		answers = new ArrayList<>(elem.getCorrectAnswers());
		answers.addAll(elem.getIncorrectAnswers());
		Collections.shuffle(answers);
	}
}
