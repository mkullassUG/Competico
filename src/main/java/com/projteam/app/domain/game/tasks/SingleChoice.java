package com.projteam.app.domain.game.tasks;

import java.util.List;
import java.util.UUID;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import com.projteam.app.domain.game.tasks.answers.SingleChoiceAnswer;
import com.projteam.app.domain.game.tasks.answers.TaskAnswer;
import com.projteam.app.dto.game.tasks.SingleChoiceDTO;
import com.projteam.app.dto.game.tasks.TaskInfoDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Access(AccessType.FIELD)
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
		
		return this.answer.equals(((SingleChoiceAnswer) answer).getAnswer())?1:0;
	}
	@Override
	public Class<? extends TaskAnswer> getAnswerType()
	{
		return SingleChoiceAnswer.class;
	}
	@Override
	public TaskInfoDTO toDTO(int taskNumber)
	{
		return new TaskInfoDTO("SingleChoice", taskNumber,
				new SingleChoiceDTO(this));
	}
}
