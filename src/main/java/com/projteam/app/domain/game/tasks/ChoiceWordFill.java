package com.projteam.app.domain.game.tasks;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import com.projteam.app.domain.game.tasks.ChoiceWordFillElement.WordChoice;
import com.projteam.app.domain.game.tasks.answers.ChoiceWordFillAnswer;
import com.projteam.app.domain.game.tasks.answers.TaskAnswer;
import com.projteam.app.dto.game.tasks.ChoiceWordFillElementDTO;
import com.projteam.app.dto.game.tasks.TaskInfoDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Access(AccessType.FIELD)
public class ChoiceWordFill implements Task
{
	private @Id UUID id;
	private @ManyToOne ChoiceWordFillElement content;
	
	private double difficulty;
	
	public List<String> getText()
	{
		return content.getText();
	}
	public List<WordChoice> getWordChoices()
	{
		return content.getWordChoices();
	}
	
	@Override
	public double acceptAnswer(TaskAnswer answer)
	{
		if (!(answer instanceof ChoiceWordFillAnswer))
			throw new IllegalArgumentException("Invalid answer type: " + answer.getClass().getTypeName());
		
		List<String> ansList = ((ChoiceWordFillAnswer) answer).getAnswers();
		Iterator<String> iter = getWordChoices()
				.stream()
				.map(wc -> wc.getCorrectAnswer())
				.iterator();
		
		if (ansList == null)
			return 0;
		if (ansList.size() != getWordChoices().size())
			throw new IllegalArgumentException("Answer length differs from task size: "
					+ ansList.size() + ", " + getWordChoices().size());
		
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
		return ChoiceWordFillAnswer.class;
	}
	@Override
	public TaskInfoDTO toDTO(int taskNumber)
	{
		return new TaskInfoDTO("ChoiceWordFill", taskNumber,
				new ChoiceWordFillElementDTO(content));
	}
}
