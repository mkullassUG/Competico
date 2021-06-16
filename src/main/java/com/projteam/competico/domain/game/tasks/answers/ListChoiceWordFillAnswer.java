package com.projteam.competico.domain.game.tasks.answers;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListChoiceWordFillAnswer implements TaskAnswer
{
	private List<List<String>> answers;
}
