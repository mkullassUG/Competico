package com.projteam.app.domain.game.tasks.answers;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SingleChoiceAnswer implements TaskAnswer
{
	private String answer;
}
