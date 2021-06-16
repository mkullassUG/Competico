package com.projteam.competico.service.game.tasks.mappers.create;

import java.util.UUID;
import org.springframework.stereotype.Service;
import com.projteam.competico.domain.game.tasks.Task;
import com.projteam.competico.domain.game.tasks.WordConnect;
import com.projteam.competico.dto.game.tasks.create.TaskDTO;
import com.projteam.competico.dto.game.tasks.create.WordConnectDTO;

@Service
public class WordConnectMapper implements TaskMapper
{
	@Override
	public boolean canConvertDTO(TaskDTO dto)
	{
		return dto.getClass() == WordConnectDTO.class;
	}
	@Override
	public boolean canConvertEntity(Task entity)
	{
		return entity.getClass() == WordConnect.class;
	}
	
	@Override
	public Task toEntity(TaskDTO dto)
	{
		WordConnectDTO wcDto = (WordConnectDTO) dto;
		return new WordConnect(UUID.randomUUID(),
				wcDto.getInstruction(),
				wcDto.getTags(),
				wcDto.getLeftWords(),
				wcDto.getRightWords(),
				wcDto.getCorrectMapping(),
				wcDto.getDifficulty());
	}
	@Override
	public TaskDTO toDTO(Task entity)
	{
		WordConnect wcEntity = (WordConnect) entity;
		return new WordConnectDTO(
				wcEntity.getInstruction(),
				wcEntity.getTags(),
				wcEntity.getDifficulty(),
				wcEntity.getLeftWords(),
				wcEntity.getRightWords(),
				wcEntity.getCorrectMapping());
	}
}
