package com.projteam.competico.service.game.tasks.mappers.create;

import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import com.projteam.competico.domain.game.tasks.ChoiceWordFillElement;
import com.projteam.competico.domain.game.tasks.ListChoiceWordFill;
import com.projteam.competico.domain.game.tasks.Task;
import com.projteam.competico.domain.game.tasks.ChoiceWordFillElement.WordChoice;
import com.projteam.competico.dto.game.tasks.create.ChoiceWordFillElementDTO;
import com.projteam.competico.dto.game.tasks.create.ListChoiceWordFillDTO;
import com.projteam.competico.dto.game.tasks.create.TaskDTO;
import com.projteam.competico.dto.game.tasks.create.ChoiceWordFillElementDTO.WordChoiceDTO;

@Service
public class ListChoiceWordFillMapper implements TaskMapper
{
	@Override
	public boolean canConvertDTO(TaskDTO dto)
	{
		return dto.getClass() == ListChoiceWordFillDTO.class;
	}
	@Override
	public boolean canConvertEntity(Task entity)
	{
		return entity.getClass() == ListChoiceWordFill.class;
	}
	
	@Override
	public Task toEntity(TaskDTO dto)
	{
		ListChoiceWordFillDTO lcwfDto = (ListChoiceWordFillDTO) dto;
		return new ListChoiceWordFill(UUID.randomUUID(),
				lcwfDto.getInstruction(),
				lcwfDto.getTags(),
				lcwfDto.getRows()
					.stream()
					.map(cwfeDto -> new ChoiceWordFillElement(UUID.randomUUID(),
							cwfeDto.getText(),
							cwfeDto.getWordChoices()
								.stream()
								.map(wcDto -> new WordChoice(UUID.randomUUID(),
										wcDto.getCorrectAnswer(),
										wcDto.getIncorrectAnswers()))
								.collect(Collectors.toList()),
							cwfeDto.isStartWithText()))
					.collect(Collectors.toList()),
				lcwfDto.getDifficulty());
	}
	@Override
	public TaskDTO toDTO(Task entity)
	{
		ListChoiceWordFill lcwfEntity = (ListChoiceWordFill) entity;
		return new ListChoiceWordFillDTO(
				lcwfEntity.getInstruction(),
				lcwfEntity.getTags(),
				lcwfEntity.getDifficulty(),
				lcwfEntity.getRows()
					.stream()
					.map(cwfeEntity -> new ChoiceWordFillElementDTO(
							cwfeEntity.getText(),
							cwfeEntity.getWordChoices()
								.stream()
								.map(wcEntity -> new WordChoiceDTO(
										wcEntity.getCorrectAnswer(),
										wcEntity.getIncorrectAnswers()))
								.collect(Collectors.toList()),
							cwfeEntity.isStartWithText()))
					.collect(Collectors.toList()));
	}
}
