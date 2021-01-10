package com.projteam.app.domain.game.tasks;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import com.projteam.app.domain.game.tasks.answers.ListWordFillAnswer;
import com.projteam.app.domain.game.tasks.answers.TaskAnswer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ListWordFill implements Task
{
	private @Id UUID id;
	private @ManyToMany List<WordFillElement> rows;
	
	private double difficulty;

	@Override
	public double acceptAnswer(TaskAnswer answer)
	{
		if (!(answer instanceof ListWordFillAnswer))
			throw new IllegalArgumentException("Invalid answer type: " + answer.getClass().getTypeName());
		
		List<List<String>> answers = ((ListWordFillAnswer) answer).getAnswers();
		
		long l = answers.stream()
				.mapToLong(list -> list.size())
				.sum();
		
		Iterator<Iterator<String>> iter = rows.stream()
				.map(row -> row.getEmptySpaces()
						.stream()
						.map(wc -> wc.getAnswer())
						.iterator())
				.iterator();
		
		long l2 = rows.stream()
				.mapToLong(row -> row.getEmptySpaces().size())
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