package com.projteam.app.dto.game.tasks.create;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SingleChoiceDTO implements TaskDTO
{
	private String instruction;
	private List<String> tags;
	private double difficulty;
	
	private String content;
	private String answer;
	private List<String> incorrectAnswers;
}
