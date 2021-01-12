package com.projteam.app.domain.game.tasks;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OrderColumn;
import com.projteam.app.domain.game.tasks.answers.ListWordFillAnswer;
import com.projteam.app.domain.game.tasks.answers.TaskAnswer;
import com.projteam.app.dto.game.tasks.ListWordFillDTO;
import com.projteam.app.dto.game.tasks.TaskInfoDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Access(AccessType.FIELD)
public class ListWordFill implements Task
{
	private @Id UUID id;
	private @ManyToMany @OrderColumn List<WordFillElement> rows;
	
	private double difficulty;

	@Override
	public double acceptAnswer(TaskAnswer answer)
	{
		if (!(answer instanceof ListWordFillAnswer))
			throw new IllegalArgumentException("Invalid answer type: " + answer.getClass().getTypeName());
		
		List<List<String>> answers = ((ListWordFillAnswer) answer).getAnswers();
		if (answers == null)
			return 0;
		
		Iterator<List<String>> iter = rows.stream()
				.map(row -> row.getEmptySpaces()
						.stream()
						.map(wc -> wc.getAnswer())
						.collect(Collectors.toList()))
				.iterator();
		
		long l = rows.stream()
				.mapToLong(row -> row.getEmptySpaces().size())
				.sum();
		
		long score = 0;
		
		for (List<String> row: answers)
		{
			if (row == null)
				continue;
			List<String> currList = iter.next();
			if (currList.size() != row.size())
				throw new IllegalArgumentException("Answer length differs from task size: ");
			Iterator<String> currIt = currList.iterator();
			for (String ans: row)
			{
				if (currIt.next().equals(ans))
					score++;
			}
		}
		
		return ((double) score) / l;
	}
	@Override
	public Class<? extends TaskAnswer> getAnswerType()
	{
		return ListWordFillAnswer.class;
	}
	@Override
	public TaskInfoDTO toDTO(int taskNumber)
	{
		return new TaskInfoDTO("ListWordFill", taskNumber,
				new ListWordFillDTO(this));
	}
}