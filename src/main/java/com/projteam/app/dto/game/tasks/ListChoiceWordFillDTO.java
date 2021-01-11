package com.projteam.app.dto.game.tasks;

import java.util.List;
import java.util.stream.Collectors;
import com.projteam.app.domain.game.tasks.ListChoiceWordFill;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListChoiceWordFillDTO implements TaskDTO
{
	private List<List<String>> text;
	private List<List<List<String>>> wordChoices;
	private List<Boolean> startWithText;

	public ListChoiceWordFillDTO(ListChoiceWordFill task)
	{
		List<ChoiceWordFillElementDTO> rows = task.getRows().stream()
				.map(row -> new ChoiceWordFillElementDTO(row))
				.collect(Collectors.toList());
		text = rows.stream()
				.map(wc -> wc.getText())
				.collect(Collectors.toList());
		wordChoices = rows.stream()
				.map(wc -> wc.getWordChoices())
				.collect(Collectors.toList());
		startWithText = rows.stream()
				.map(wc -> wc.isStartWithText())
				.collect(Collectors.toList());
	}
}
