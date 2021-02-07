package com.projteam.app.service.game.tasks.mappers.create;

import java.util.UUID;
import org.springframework.stereotype.Service;
import com.projteam.app.domain.game.tasks.SingleChoice;
import com.projteam.app.domain.game.tasks.Task;
import com.projteam.app.dto.game.tasks.create.SingleChoiceDTO;
import com.projteam.app.dto.game.tasks.create.TaskDTO;

@Service
public class SingleChoiceMapper implements TaskMapper
{
	@Override
	public boolean canConvertDTO(TaskDTO dto)
	{
		return dto.getClass() == SingleChoiceDTO.class;
	}
	@Override
	public boolean canConvertEntity(Task entity)
	{
		return entity.getClass() == SingleChoice.class;
	}
	
	@Override
	public Task toEntity(TaskDTO dto)
	{
		SingleChoiceDTO scDto = (SingleChoiceDTO) dto;
		return new SingleChoice(UUID.randomUUID(),
				scDto.getInstruction(), 
				scDto.getTags(),
				scDto.getContent(),
				scDto.getAnswer(),
				scDto.getIncorrectAnswers(),
				scDto.getDifficulty());
	}
	@Override
	public TaskDTO toDTO(Task entity)
	{
		SingleChoice scEntity = (SingleChoice) entity;
		return new SingleChoiceDTO(
				scEntity.getInstruction(), 
				scEntity.getTags(),
				scEntity.getDifficulty(),
				scEntity.getContent(),
				scEntity.getAnswer(),
				scEntity.getIncorrectAnswers());
	}
}
