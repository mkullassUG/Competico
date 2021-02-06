package com.projteam.app.mapper.game.tasks.create;

import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import com.projteam.app.domain.game.tasks.ListSentenceForming;
import com.projteam.app.domain.game.tasks.SentenceFormingElement;
import com.projteam.app.domain.game.tasks.Task;
import com.projteam.app.dto.game.tasks.create.ListSentenceFormingDTO;
import com.projteam.app.dto.game.tasks.create.SentenceFormingElementDTO;
import com.projteam.app.dto.game.tasks.create.TaskDTO;

@Service
public class ListSentenceFormingMapper implements TaskMapper
{
	@Override
	public boolean canConvertDTO(TaskDTO dto)
	{
		return dto.getClass() == ListSentenceFormingDTO.class;
	}
	@Override
	public boolean canConvertEntity(Task entity)
	{
		return entity.getClass() == ListSentenceForming.class;
	}
	
	@Override
	public Task toEntity(TaskDTO dto)
	{
		ListSentenceFormingDTO lsfDto = (ListSentenceFormingDTO) dto;
		return new ListSentenceForming(UUID.randomUUID(),
				lsfDto.getInstruction(),
				lsfDto.getTags(),
				lsfDto.getRows()
					.stream()
					.map(sfeDto -> new SentenceFormingElement(UUID.randomUUID(),
							sfeDto.getWords()))
					.collect(Collectors.toList()), 
				lsfDto.getDifficulty());
	}
	@Override
	public TaskDTO toDTO(Task entity)
	{
		ListSentenceForming lsfEntity = (ListSentenceForming) entity;
		return new ListSentenceFormingDTO(
				lsfEntity.getInstruction(),
				lsfEntity.getTags(),
				lsfEntity.getDifficulty(),
				lsfEntity.getRows()
					.stream()
					.map(sfeEntity -> new SentenceFormingElementDTO(
							sfeEntity.getWords()))
					.collect(Collectors.toList()));
	}
}
