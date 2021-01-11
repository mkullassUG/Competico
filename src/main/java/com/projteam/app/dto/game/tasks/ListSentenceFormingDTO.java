package com.projteam.app.dto.game.tasks;

import java.util.List;
import java.util.stream.Collectors;
import com.projteam.app.domain.game.tasks.ListSentenceForming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListSentenceFormingDTO implements TaskDTO
{
	private List<List<String>> words;

	public ListSentenceFormingDTO(ListSentenceForming task)
	{
		words = task.getRows().stream()
			.map(row -> row.getWords())
			.collect(Collectors.toList());
	}
}
