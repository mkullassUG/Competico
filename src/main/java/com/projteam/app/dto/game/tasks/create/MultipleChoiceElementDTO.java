package com.projteam.app.dto.game.tasks.create;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MultipleChoiceElementDTO
{
	private String content;
	private List<String> correctAnswers;
	private List<String> incorrectAnswers;
}
