package com.projteam.app.domain.game.tasks;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OrderColumn;
import com.projteam.app.domain.game.tasks.answers.ChronologicalOrderAnswer;
import com.projteam.app.domain.game.tasks.answers.TaskAnswer;
import com.projteam.app.dto.game.tasks.ChronologicalOrderDTO;
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
public class ChronologicalOrder implements Task
{
	private @Id UUID id;
	private String instruction;
	private @ElementCollection List<String> tags;
	private @ElementCollection @OrderColumn List<String> sentences;
	
	private double difficulty;
	
	@Override
	public double acceptAnswer(TaskAnswer answer)
	{
		if (!(answer instanceof ChronologicalOrderAnswer))
			throw new IllegalArgumentException("Invalid answer type: " + answer.getClass().getTypeName());
		
		List<String> ansList = ((ChronologicalOrderAnswer) answer).getAnswers();
		Iterator<String> iter = sentences.iterator();
		
		if (ansList == null)
			return 0;
		if (ansList.size() != sentences.size())
			throw new IllegalArgumentException("Answer length differs from task size: " + ansList.size() + ", " + sentences.size());
		
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
		return ChronologicalOrderAnswer.class;
	}
	@Override
	public TaskInfoDTO prepareTaskInfo(int currentTaskNumber, int taskCount)
	{
		return new TaskInfoDTO("ChronologicalOrder", currentTaskNumber, taskCount, instruction,
				new ChronologicalOrderDTO(sentences));
	}
	@Override
	public void initialize()
	{
		Initializable.initialize(sentences);
	}
}
