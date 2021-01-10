package com.projteam.app.domain.game.tasks.answers;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WordConnectAnswer implements TaskAnswer
{
	private Map<Integer, Integer> answerMapping;
}
