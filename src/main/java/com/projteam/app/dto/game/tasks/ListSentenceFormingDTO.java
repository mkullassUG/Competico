package com.projteam.app.dto.game.tasks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import com.projteam.app.domain.game.tasks.ListSentenceForming;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ListSentenceFormingDTO implements TaskDTO
{
	private List<List<String>> words;

	public ListSentenceFormingDTO(ListSentenceForming task)
	{
		words = task.getRows().stream()
			.map(row -> shuffle(row.getWords()))
			.collect(Collectors.toList());
	}
	private static <T> List<T> shuffle(List<T> list)
	{
		List<T> ret = new ArrayList<>(list);
		Collections.shuffle(ret);
		return ret;
	}
}
