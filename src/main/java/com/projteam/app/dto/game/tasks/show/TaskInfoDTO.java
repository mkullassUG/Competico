package com.projteam.app.dto.game.tasks.show;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskInfoDTO
{
	private String taskName;
	public int currentTaskNumber;
	public int taskCount;
	private String instruction;
	public TaskDTO task;
}