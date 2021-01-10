package com.projteam.app.domain.game.tasks.answers;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ListSentenceFormingAnswer implements TaskAnswer
{
	private List<List<String>> answers;
}
