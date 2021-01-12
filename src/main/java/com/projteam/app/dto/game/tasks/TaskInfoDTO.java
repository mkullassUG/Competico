package com.projteam.app.dto.game.tasks;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskInfoDTO
{
	private String taskName;
	public int taskNumber;
	public TaskDTO task;
}