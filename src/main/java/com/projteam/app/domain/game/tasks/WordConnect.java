package com.projteam.app.domain.game.tasks;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.UUID;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OrderColumn;
import com.projteam.app.domain.game.tasks.answers.TaskAnswer;
import com.projteam.app.domain.game.tasks.answers.WordConnectAnswer;
import com.projteam.app.dto.game.tasks.show.TaskInfoDTO;
import com.projteam.app.dto.game.tasks.show.WordConnectDTO;
import com.projteam.app.utils.Initializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Access(AccessType.FIELD)
public class WordConnect implements Task
{
	private @Id UUID id;
	private String instruction;
	private @ElementCollection List<String> tags;
	private @ElementCollection @OrderColumn List<String> leftWords;
	private @ElementCollection @OrderColumn List<String> rightWords;
	private @ElementCollection Map<Integer, Integer> correctMapping;
	
	private double difficulty;
	
	@Override
	public double acceptAnswer(TaskAnswer answer)
	{
		if (!(answer instanceof WordConnectAnswer))
			throw new IllegalArgumentException("Invalid answer type: " + answer.getClass().getTypeName());
		
		Map<String, String> answerMapping = ((WordConnectAnswer) answer).getAnswerMapping();
		
		if (answerMapping == null)
			return 0;
		
		int l = correctMapping.size();
		if (l == 0)
			return 1;
		
		long score = 0;
		
		for (Entry<String, String> e: answerMapping.entrySet())
		{
			Integer leftIndex = Optional.ofNullable(e.getKey())
					.map(s -> leftWords.indexOf(s))
					.orElse(-1);
			Integer rightIndex = Optional.ofNullable(e.getValue())
					.map(s -> rightWords.indexOf(s))
					.orElse(-1);
			if (correctMapping.containsKey(leftIndex)
					&& (correctMapping.get(leftIndex) == rightIndex))
				score++;
		}
		
		return ((double) score) / l;
	}
	@Override
	public Class<? extends TaskAnswer> getAnswerType()
	{
		return WordConnectAnswer.class;
	}
	@Override
	public TaskInfoDTO prepareTaskInfo(int currentTaskNumber, int taskCount)
	{
		return new TaskInfoDTO("WordConnect", currentTaskNumber, taskCount, instruction,
				new WordConnectDTO(this));
	}
	@Override
	public void initialize()
	{
		Initializable.initialize(leftWords, rightWords, correctMapping);
	}
}
