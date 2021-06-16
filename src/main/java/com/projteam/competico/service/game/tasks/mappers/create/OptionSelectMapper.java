package com.projteam.competico.service.game.tasks.mappers.create;

import java.util.UUID;
import org.springframework.stereotype.Service;
import com.projteam.competico.domain.game.tasks.OptionSelect;
import com.projteam.competico.domain.game.tasks.OptionSelectElement;
import com.projteam.competico.domain.game.tasks.Task;
import com.projteam.competico.dto.game.tasks.create.OptionSelectDTO;
import com.projteam.competico.dto.game.tasks.create.OptionSelectElementDTO;
import com.projteam.competico.dto.game.tasks.create.TaskDTO;

@Service
public class OptionSelectMapper implements TaskMapper
{
	@Override
	public boolean canConvertDTO(TaskDTO dto)
	{
		return dto.getClass() == OptionSelectDTO.class;
	}
	@Override
	public boolean canConvertEntity(Task entity)
	{
		return entity.getClass() == OptionSelect.class;
	}
	
	@Override
	public Task toEntity(TaskDTO dto)
	{
		OptionSelectDTO osDto = (OptionSelectDTO) dto;
		OptionSelectElementDTO oseDto = osDto.getContent();
		OptionSelectElement content = new OptionSelectElement(UUID.randomUUID(),
				oseDto.getContent(),
				oseDto.getCorrectAnswers(),
				oseDto.getIncorrectAnswers());
		return new OptionSelect(UUID.randomUUID(),
				osDto.getInstruction(),
				osDto.getTags(),
				content,
				osDto.getDifficulty());
	}
	@Override
	public TaskDTO toDTO(Task entity)
	{
		OptionSelect osEntity = (OptionSelect) entity;
		OptionSelectElement oseEntity = osEntity.getContent();
		OptionSelectElementDTO content = new OptionSelectElementDTO(
				oseEntity.getContent(),
				oseEntity.getCorrectAnswers(),
				oseEntity.getIncorrectAnswers());
		return new OptionSelectDTO(
				osEntity.getInstruction(),
				osEntity.getTags(),
				osEntity.getDifficulty(),
				content);
	}
}
