package com.projteam.app.dto.game.tasks.create;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChronologicalOrderDTO implements TaskDTO
{
	private String instruction;
	private List<String> tags;
	private double difficulty;
	
	private List<String> sentences;
}