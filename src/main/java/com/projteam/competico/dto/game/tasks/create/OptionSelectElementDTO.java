package com.projteam.competico.dto.game.tasks.create;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OptionSelectElementDTO
{
	private String content;
	private List<String> correctAnswers;
	private List<String> incorrectAnswers;
}
