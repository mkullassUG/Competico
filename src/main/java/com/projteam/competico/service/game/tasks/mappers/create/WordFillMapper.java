package com.projteam.competico.service.game.tasks.mappers.create;

import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import com.projteam.competico.domain.game.tasks.Task;
import com.projteam.competico.domain.game.tasks.WordFill;
import com.projteam.competico.domain.game.tasks.WordFillElement;
import com.projteam.competico.domain.game.tasks.WordFillElement.EmptySpace;
import com.projteam.competico.dto.game.tasks.create.TaskDTO;
import com.projteam.competico.dto.game.tasks.create.WordFillDTO;
import com.projteam.competico.dto.game.tasks.create.WordFillElementDTO;
import com.projteam.competico.dto.game.tasks.create.WordFillElementDTO.EmptySpaceDTO;

@Service
public class WordFillMapper implements TaskMapper
{
	@Override
	public boolean canConvertDTO(TaskDTO dto)
	{
		return dto.getClass() == WordFillDTO.class;
	}
	@Override
	public boolean canConvertEntity(Task entity)
	{
		return entity.getClass() == WordFill.class;
	}
	
	@Override
	public Task toEntity(TaskDTO dto)
	{
		WordFillDTO lwfDto = (WordFillDTO) dto;
		WordFillElementDTO lwfeDto = lwfDto.getContent();
		WordFillElement content = new WordFillElement(UUID.randomUUID(),
				lwfeDto.getText(),
				lwfeDto.getEmptySpaces()
					.stream()
					.map(esDto -> new EmptySpace(esDto.getAnswer()))
					.collect(Collectors.toList()),
				lwfeDto.isStartWithText(),
				lwfeDto.getPossibleAnswers());
		return new WordFill(UUID.randomUUID(),
				lwfDto.getInstruction(),
				lwfDto.getTags(),
				content,
				lwfDto.getDifficulty());
	}
	@Override
	public TaskDTO toDTO(Task entity)
	{
		WordFill lwfEntity = (WordFill) entity;
		WordFillElement lwfeEntity = lwfEntity.getContent();
		WordFillElementDTO content = new WordFillElementDTO(
				lwfeEntity.getText(),
				lwfeEntity.getEmptySpaces()
					.stream()
					.map(esEntity -> new EmptySpaceDTO(esEntity.getAnswer()))
					.collect(Collectors.toList()),
				lwfeEntity.isStartWithText(),
				lwfeEntity.getPossibleAnswers());
		return new WordFillDTO(
				lwfEntity.getInstruction(),
				lwfEntity.getTags(),
				lwfEntity.getDifficulty(),
				content);
	}
}
