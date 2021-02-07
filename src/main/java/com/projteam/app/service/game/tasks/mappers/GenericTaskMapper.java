package com.projteam.app.service.game.tasks.mappers;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.projteam.app.domain.game.tasks.Task;
import com.projteam.app.dto.game.tasks.create.TaskDTO;
import com.projteam.app.service.game.tasks.mappers.create.TaskMapper;

@Service
public class GenericTaskMapper
{
	private List<TaskMapper> mappers;
	
	@Autowired
	public GenericTaskMapper(
			List<TaskMapper> mappers)
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
