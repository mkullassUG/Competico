package com.projteam.competico.domain.game.tasks;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OrderColumn;
import com.projteam.competico.domain.game.tasks.answers.ChronologicalOrderAnswer;
import com.projteam.competico.domain.game.tasks.answers.TaskAnswer;
import com.projteam.competico.dto.game.tasks.show.ChronologicalOrderDTO;
import com.projteam.competico.dto.game.tasks.show.TaskInfoDTO;
import com.projteam.competico.utils.Initializable;
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
	private @Column(length = 1024) String instruction;
	private @ElementCollection List<String> tags;
	private @Column(length = 1024) @ElementCollection @OrderColumn List<String> sentences;
	
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
		
		int aL = ansList.size();
		
		if (aL != sentences.size())
			return 0;
		if (aL == 0)
			return 1;
		
		long score = 0;
		
		for (String s: ansList)
		{
			if (iter.next().equals(s))
				score++;
		}
		
		return ((double) score) / aL;
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
		Initializable.initialize(tags, sentences);
	}
}
