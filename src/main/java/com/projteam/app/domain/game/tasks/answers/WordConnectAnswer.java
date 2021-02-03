package com.projteam.app.domain.game.tasks.answers;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WordConnectAnswer implements TaskAnswer
{
	private Map<String, String> answerMapping;
}
