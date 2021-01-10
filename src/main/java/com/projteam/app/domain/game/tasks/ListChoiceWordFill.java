package com.projteam.app.domain.game.tasks;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import com.projteam.app.domain.game.tasks.answers.ListChoiceWordFillAnswer;
import com.projteam.app.domain.game.tasks.answers.TaskAnswer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ListChoiceWordFill implements Task
{
	private @Id UUID id;
	private @ManyToMany List<ChoiceWordFillElement> rows;
	
	private double difficulty;
	
	@Override
	public double acceptAnswer(TaskAnswer answer)
	{
		if (!(answer instanceof ListChoiceWordFillAnswer))
			throw new IllegalArgumentException("Invalid answer type: " + answer.getClass().getTypeName());
		
		List<List<String>> answers = ((ListChoiceWordFillAnswer) answer).getAnswers();
		
		long l = answers.stream()
				.mapToLong(list -> list.size())
				.sum();
		
		Iterator<Iterator<String>> iter = rows.stream()
				.map(row -> row.getWordChoices()
						.stream()
						.map(wc -> wc.getCorrectAnswer())
						.iterator())
				.iterator();
		
		long l2 = rows.stream()
				.mapToLong(row -> row.getWordChoices().size())
				.sum();
		
		if (l != l2)
			throw new IllegalArgumentException("Answer length differs from task size: "
					+ l + ", " + l2);
		
		long score = 0;
		
		for (List<String> row: answers)
		{
			Iterator<String> currIt = iter.next();
			for (String ans: row)
			{
				if (currIt.next().equals(ans))
					score++;
			}
		}
		
		return ((double) score) / l;
	}
}