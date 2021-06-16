package com.projteam.competico.dto.game.tasks.create;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WordConnectDTO implements TaskDTO
{
	private String instruction;
	private List<String> tags;
	private double difficulty;
	
	private List<String> leftWords;
	private List<String> rightWords;
	private Map<Integer, Integer> correctMapping;
}
