package com.projteam.competico.service.game.tasks.mappers.create;

import java.util.UUID;
import org.springframework.stereotype.Service;
import com.projteam.competico.domain.game.tasks.ChronologicalOrder;
import com.projteam.competico.domain.game.tasks.Task;
import com.projteam.competico.dto.game.tasks.create.ChronologicalOrderDTO;
import com.projteam.competico.dto.game.tasks.create.TaskDTO;

@Service
public class ChronologicalOrderMapper implements TaskMapper
{
	@Override
	public boolean canConvertDTO(TaskDTO dto)
	{
		return dto.getClass() == ChronologicalOrderDTO.class;
	}
	@Override
	public boolean canConvertEntity(Task entity)
	{
		return entity.getClass() == ChronologicalOrder.class;
	}
	
	@Override
	public Task toEntity(TaskDTO dto)
	{
		ChronologicalOrderDTO coDto = (ChronologicalOrderDTO) dto;
		return new ChronologicalOrder(UUID.randomUUID(),
				coDto.getInstruction(), coDto.getTags(),
				coDto.getSentences(), coDto.getDifficulty());
	}
	@Override
	public TaskDTO toDTO(Task entity)
	{
		ChronologicalOrder coEntity = (ChronologicalOrder) entity;
		return new ChronologicalOrderDTO(
				coEntity.getInstruction(), coEntity.getTags(),
				coEntity.getDifficulty(), coEntity.getSentences());
	}
}
