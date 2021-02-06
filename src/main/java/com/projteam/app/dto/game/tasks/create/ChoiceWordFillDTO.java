package com.projteam.app.dto.game.tasks.create;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChoiceWordFillDTO implements TaskDTO
{
	private String instruction;
	private List<String> tags;
	private double difficulty;
	
	private ChoiceWordFillElementDTO content;
}