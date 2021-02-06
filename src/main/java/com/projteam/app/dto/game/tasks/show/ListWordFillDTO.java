package com.projteam.app.dto.game.tasks.show;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import com.projteam.app.domain.game.tasks.ListWordFill;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ListWordFillDTO implements TaskDTO
{
	private List<Boolean> startWithText;
	private List<List<String>> possibleAnswers;
	private List<Integer> emptySpaceCount;
	private List<List<String>> text;

	public ListWordFillDTO(ListWordFill task)
	{
		List<WordFillElementDTO> rows = task.getRows().stream()
				.map(row -> new WordFillElementDTO(row))
				.collect(Collectors.toList());
		text = rows.stream()
			.map(row -> row.getText())
			.collect(Collectors.toList());
		emptySpaceCount = rows.stream()
			.map(row -> row.getEmptySpaceCount())
			.collect(Collectors.toList());
		possibleAnswers = rows.stream()
			.map(row -> shuffle(row.getPossibleAnswers()))
			.collect(Collectors.toList());
		startWithText = rows.stream()
			.map(row -> row.isStartWithText())
			.collect(Collectors.toList());
	}
	private static <T> List<T> shuffle(List<T> list)
	{
		List<T> ret = new ArrayList<>(list);
		Collections.shuffle(ret);
		return ret;
	}
}
