package com.projteam.app.dto.game.tasks.show;

import java.util.ArrayList;
import java.util.Collections;
import com.projteam.app.domain.game.tasks.SingleChoice;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SingleChoiceDTO implements TaskDTO
{
	private String content;
	private ArrayList<String> answers;

	public SingleChoiceDTO(SingleChoice task)
	{
		content = task.getContent();
		answers = new ArrayList<>(task.getIncorrectAnswers());
		answers.add(task.getAnswer());
		Collections.shuffle(answers);
	}
}
