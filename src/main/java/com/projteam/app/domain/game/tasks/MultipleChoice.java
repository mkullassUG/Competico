package com.projteam.app.domain.game.tasks;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import com.projteam.app.domain.game.tasks.answers.MultipleChoiceAnswer;
import com.projteam.app.domain.game.tasks.answers.TaskAnswer;
import com.projteam.app.dto.game.tasks.MultipleChoiceElementDTO;
import com.projteam.app.dto.game.tasks.TaskInfoDTO;
import com.projteam.app.utils.Initializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Access(AccessType.FIELD)
public class MultipleChoice implements Task
{
	private @Id UUID id;
	private @ManyToOne MultipleChoiceElement content;
	
	private double difficulty;

	@Override
	public double acceptAnswer(TaskAnswer answer)
	{
		if (!(answer instanceof MultipleChoiceAnswer))
			throw new IllegalArgumentException("Invalid answer type: " + answer.getClass().getTypeName());
		
		List<String> ansList = ((MultipleChoiceAnswer) answer).getAnswers();
		Iterator<String> iter = content.getCorrectAnswers()
				.iterator();
		
		if (ansList == null)
			return 0;
		if (ansList.size() != content.getCorrectAnswers().size())
			throw new IllegalArgumentException("Answer length differs from task size: "
					+ ansList.size() + ", " + content.getCorrectAnswers().size());
		
		long score = 0;
		
		for (String s: ansList)
		{
			if (iter.next().equals(s))
				score++;
		}
		
		return ((double) score) / ansList.size();
	}
	@Override
	public Class<? extends TaskAnswer> getAnswerType()
	{
		return MultipleChoiceAnswer.class;
	}
	@Override
	public TaskInfoDTO toDTO(int taskNumber)
	{
		return new TaskInfoDTO("MultipleChoice", taskNumber,
				new MultipleChoiceElementDTO(content));
	}
	@Override
	public void initialize()
	{
		Initializable.initialize(content);
	}
}
