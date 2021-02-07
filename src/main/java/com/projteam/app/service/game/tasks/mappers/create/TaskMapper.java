package com.projteam.app.service.game.tasks.mappers.create;

import com.projteam.app.domain.game.tasks.Task;
import com.projteam.app.dto.game.tasks.create.TaskDTO;

public interface TaskMapper
{
	public boolean canConvertDTO(TaskDTO dto);
	public boolean canConvertEntity(Task entity);
	
	public Task toEntity(TaskDTO dto);
	public TaskDTO toDTO(Task entity);
}
