package com.projteam.app.domain.game.tasks;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import com.projteam.app.domain.game.tasks.answers.OptionSelectAnswer;
import com.projteam.app.domain.game.tasks.answers.TaskAnswer;
import com.projteam.app.dto.game.tasks.show.OptionSelectElementDTO;
import com.projteam.app.dto.game.tasks.show.TaskInfoDTO;
import com.projteam.app.utils.Initializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Access(AccessType.FIELD)
public class OptionSelect implements Task
{
	private @Id UUID id;
	private String instruction;
	private @ElementCollection List<String> tags;
	private @ManyToOne OptionSelectElement content;
	
	private double difficulty;
	
	@Override
	public double acceptAnswer(TaskAnswer answer)
	{
		if (!(answer instanceof OptionSelectAnswer))
			throw new IllegalArgumentException("Invalid answer type: " + answer.getClass().getTypeName());
		
		List<String> ansList = ((OptionSelectAnswer) answer).getAnswers();
		Iterator<String> iter = content.getCorrectAnswers()
				.iterator();
		
		if (ansList == null)
			return 0;
		
		int aL = ansList.size();
		
		if (aL != content.getCorrectAnswers().size())
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
		return OptionSelectAnswer.class;
	}
	@Override
	public TaskInfoDTO prepareTaskInfo(int currentTaskNumber, int taskCount)
	{
		return new TaskInfoDTO("OptionSelect", currentTaskNumber, taskCount, instruction,
				new OptionSelectElementDTO(content));
	}
	@Override
	public void initialize()
	{
		Initializable.initialize(content);
	}
}
