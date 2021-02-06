package com.projteam.app.mapper.game.tasks.create;

import java.util.UUID;
import org.springframework.stereotype.Service;
import com.projteam.app.domain.game.tasks.MultipleChoice;
import com.projteam.app.domain.game.tasks.MultipleChoiceElement;
import com.projteam.app.domain.game.tasks.Task;
import com.projteam.app.dto.game.tasks.create.MultipleChoiceDTO;
import com.projteam.app.dto.game.tasks.create.MultipleChoiceElementDTO;
import com.projteam.app.dto.game.tasks.create.TaskDTO;

@Service
public class MultipleChoiceMapper implements TaskMapper
{
	@Override
	public boolean canConvertDTO(TaskDTO dto)
	{
		return dto.getClass() == MultipleChoiceDTO.class;
	}
	@Override
	public boolean canConvertEntity(Task entity)
	{
		return entity.getClass() == MultipleChoice.class;
	}
	
	@Override
	public Task toEntity(TaskDTO dto)
	{
		MultipleChoiceDTO mcDto = (MultipleChoiceDTO) dto;
		MultipleChoiceElementDTO mceDto = mcDto.getContent();
		MultipleChoiceElement content = new MultipleChoiceElement(UUID.randomUUID(),
				mceDto.getContent(),
				mceDto.getCorrectAnswers(),
				mceDto.getIncorrectAnswers());
		return new MultipleChoice(UUID.randomUUID(),
				mcDto.getInstruction(),
				mcDto.getTags(),
				content,
				mcDto.getDifficulty());
	}
	@Override
	public TaskDTO toDTO(Task entity)
	{
		MultipleChoice mcEntity = (MultipleChoice) entity;
		MultipleChoiceElement mceEntity = mcEntity.getContent();
		MultipleChoiceElementDTO content = new MultipleChoiceElementDTO(
				mceEntity.getContent(),
				mceEntity.getCorrectAnswers(),
				mceEntity.getIncorrectAnswers());
		return new MultipleChoiceDTO(
				mcEntity.getInstruction(),
				mcEntity.getTags(),
				mcEntity.getDifficulty(),
				content);
	}
}
