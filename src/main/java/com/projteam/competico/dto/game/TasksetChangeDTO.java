package com.projteam.competico.dto.game;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TasksetChangeDTO
{
	private String tasksetName;
	private String newTasksetName;
}
