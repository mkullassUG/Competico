package com.projteam.app.domain.game.tasks;

import java.util.List;
import java.util.UUID;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import com.projteam.app.domain.game.tasks.answers.SingleChoiceAnswer;
import com.projteam.app.domain.game.tasks.answers.TaskAnswer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class SingleChoice implements Task
{
	private @Id UUID id;
	private String content;
	private String answer;
	private @ElementCollection List<String> incorrectAnswers;
	
	private double difficulty;

	@Override
	public double acceptAnswer(TaskAnswer answer)
	{
		if (!(answer instanceof SingleChoiceAnswer))
			throw new IllegalArgumentException("Invalid answer type: " + answer.getClass().getTypeName());
		
		return ((SingleChoiceAnswer) answer).getAnswer().equals(this.answer)?1:0;
	}
}
