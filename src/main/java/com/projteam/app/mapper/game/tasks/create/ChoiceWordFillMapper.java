package com.projteam.app.mapper.game.tasks.create;

import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import com.projteam.app.domain.game.tasks.ChoiceWordFill;
import com.projteam.app.domain.game.tasks.ChoiceWordFillElement;
import com.projteam.app.domain.game.tasks.ChoiceWordFillElement.WordChoice;
import com.projteam.app.domain.game.tasks.Task;
import com.projteam.app.dto.game.tasks.create.ChoiceWordFillDTO;
import com.projteam.app.dto.game.tasks.create.ChoiceWordFillElementDTO;
import com.projteam.app.dto.game.tasks.create.ChoiceWordFillElementDTO.WordChoiceDTO;
import com.projteam.app.dto.game.tasks.create.TaskDTO;

@Service
public class ChoiceWordFillMapper implements TaskMapper
{
	@Override
	public boolean canConvertDTO(TaskDTO dto)
	{
		return dto.getClass() == ChoiceWordFillDTO.class;
	}
	@Override
	public boolean canConvertEntity(Task entity)
	{
		return entity.getClass() == ChoiceWordFill.class;
	}
	
	@Override
	public Task toEntity(TaskDTO dto)
	{
		ChoiceWordFillDTO cwfDto = (ChoiceWordFillDTO) dto;
		ChoiceWordFillElementDTO cwfeDto = cwfDto.getContent();
		ChoiceWordFillElement content = new ChoiceWordFillElement(
				UUID.randomUUID(), cwfeDto.getText(),
				cwfeDto.getWordChoices()
					.stream()
					.map(wcDto -> new WordChoice(UUID.randomUUID(),
							wcDto.getCorrectAnswer(),
							wcDto.getIncorrectAnswers()))
					.collect(Collectors.toList()),
				cwfeDto.isStartWithText());
		return new ChoiceWordFill(UUID.randomUUID(),
				cwfDto.getInstruction(), cwfDto.getTags(),
				content, cwfDto.getDifficulty());
	}
	@Override
	public TaskDTO toDTO(Task entity)
	{
		ChoiceWordFill cwfEntity = (ChoiceWordFill) entity;
		ChoiceWordFillElement cwfeEntity = cwfEntity.getContent();
		ChoiceWordFillElementDTO content = new ChoiceWordFillElementDTO(
				cwfeEntity.getText(),
				cwfeEntity.getWordChoices()
					.stream()
					.map(wcDto -> new WordChoiceDTO(
							wcDto.getCorrectAnswer(),
							wcDto.getIncorrectAnswers()))
					.collect(Collectors.toList()),
				cwfeEntity.isStartWithText());
		return new ChoiceWordFillDTO(cwfEntity.getInstruction(),
				cwfEntity.getTags(),
				cwfEntity.getDifficulty(),
				content);
	}
}
