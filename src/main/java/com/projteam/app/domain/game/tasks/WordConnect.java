package com.projteam.app.domain.game.tasks;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import com.projteam.app.domain.game.tasks.answers.TaskAnswer;
import com.projteam.app.domain.game.tasks.answers.WordConnectAnswer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class WordConnect implements Task
{
	private @Id UUID id;
	private @ElementCollection List<String> leftWords;
	private @ElementCollection List<String> rightWords;
	private @ElementCollection Map<Integer, Integer> correctMapping;
	
	private double difficulty;

	@Override
	public double acceptAnswer(TaskAnswer answer)
	{
		if (!(answer instanceof WordConnectAnswer))
			throw new IllegalArgumentException("Invalid answer type: " + answer.getClass().getTypeName());
		
		Map<Integer, Integer> answerMapping = ((WordConnectAnswer) answer).getAnswerMapping();
		
		if (correctMapping.size() != answerMapping.size())
			throw new IllegalArgumentException("Answer length differs from task size: "
					+ correctMapping.size() + ", " + answerMapping.size());
		
		long score = 0;
		
		for (Entry<Integer, Integer> e: correctMapping.entrySet())
		{
			if (answerMapping.get(e.getKey()) == e.getValue())
				score++;
		}
		
		return ((double) score) / correctMapping.size();
	}
}
