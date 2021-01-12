package com.projteam.app.domain.game.tasks.answers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SingleChoiceAnswer implements TaskAnswer
{
	private String answer;
}
