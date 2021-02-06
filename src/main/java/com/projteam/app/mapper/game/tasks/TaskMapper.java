package com.projteam.app.mapper.game.tasks;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.projteam.app.domain.game.tasks.Task;
import com.projteam.app.dto.game.tasks.create.TaskDTO;

@Service
public class TaskMapper
{
	private List<com.projteam.app.mapper.game.tasks.create.TaskMapper> mappers;
	
	@Autowired
	public TaskMapper(
			List<com.projteam.app.mapper.game.tasks.create.TaskMapper> mappers)
	{
		this.mappers = mappers;
	}

	public Task toEntity(TaskDTO dto)
	{
		for (var mapper: mappers)
		{
			if (mapper.canConvertDTO(dto))
				return mapper.toEntity(dto);
		}
		throw new IllegalArgumentException("No suitable mapper found for DTO " + dto.getClass());
	}
	public TaskDTO toDTO(Task entity)
	{
		for (var mapper: mappers)
		{
			if (mapper.canConvertEntity(entity))
				return mapper.toDTO(entity);
		}
		throw new IllegalArgumentException("No suitable mapper found for entity " + entity.getClass());
	}
}
