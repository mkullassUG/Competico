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
import javax.persistence.ManyToOne;
import com.projteam.competico.domain.game.tasks.WordFillElement.EmptySpace;
import com.projteam.competico.domain.game.tasks.answers.TaskAnswer;
import com.projteam.competico.domain.game.tasks.answers.WordFillAnswer;
import com.projteam.competico.dto.game.tasks.show.TaskInfoDTO;
import com.projteam.competico.dto.game.tasks.show.WordFillElementDTO;
import com.projteam.competico.utils.Initializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Access(AccessType.FIELD)
public class WordFill implements Task
{
	private @Id UUID id;
	private @Column(length = 1024) String instruction;
	private @ElementCollection List<String> tags;
	private @ManyToOne WordFillElement content;
	
	private double difficulty;
	
	public List<String> getText()
	{
		return content.getText();
	}
	public List<EmptySpace> getEmptySpaces()
	{
		return content.getEmptySpaces();
	}
	public List<String> getPossibleAnswers()
	{
		return content.getPossibleAnswers();
	}
	
	@Override
	public double acceptAnswer(TaskAnswer answer)
	{
		if (!(answer instanceof WordFillAnswer))
			throw new IllegalArgumentException("Invalid answer type: " + answer.getClass().getTypeName());
		
		List<String> ansList = ((WordFillAnswer) answer).getAnswers();
		Iterator<String> iter = getEmptySpaces()
				.stream()
				.map(wc -> wc.getAnswer())
				.iterator();
		
		if (ansList == null)
			return 0;
		
		int aL = ansList.size();
		
		if (aL != getEmptySpaces().size())
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
		return WordFillAnswer.class;
	}
	@Override
	public TaskInfoDTO prepareTaskInfo(int currentTaskNumber, int taskCount)
	{
		return new TaskInfoDTO("WordFill", currentTaskNumber, taskCount, instruction,
				new WordFillElementDTO(content));
	}
	@Override
	public void initialize()
	{
		Initializable.initialize(tags, content);
	}
}
