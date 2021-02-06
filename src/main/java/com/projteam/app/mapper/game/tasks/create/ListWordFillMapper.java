package com.projteam.app.mapper.game.tasks.create;

import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import com.projteam.app.domain.game.tasks.ListWordFill;
import com.projteam.app.domain.game.tasks.Task;
import com.projteam.app.domain.game.tasks.WordFillElement;
import com.projteam.app.domain.game.tasks.WordFillElement.EmptySpace;
import com.projteam.app.dto.game.tasks.create.ListWordFillDTO;
import com.projteam.app.dto.game.tasks.create.TaskDTO;
import com.projteam.app.dto.game.tasks.create.WordFillElementDTO;
import com.projteam.app.dto.game.tasks.create.WordFillElementDTO.EmptySpaceDTO;

@Service
public class ListWordFillMapper implements TaskMapper
{
	@Override
	public boolean canConvertDTO(TaskDTO dto)
	{
		return dto.getClass() == ListWordFillDTO.class;
	}
	@Override
	public boolean canConvertEntity(Task entity)
	{
		return entity.getClass() == ListWordFill.class;
	}
	
	@Override
	public Task toEntity(TaskDTO dto)
	{
		ListWordFillDTO lwfDto = (ListWordFillDTO) dto;
		return new ListWordFill(UUID.randomUUID(),
				lwfDto.getInstruction(),
				lwfDto.getTags(),
				lwfDto.getRows()
					.stream()
					.map(lwfeDto -> new WordFillElement(UUID.randomUUID(),
							lwfeDto.getText(),
							lwfeDto.getEmptySpaces()
								.stream()
								.map(esDto -> new EmptySpace(esDto.getAnswer()))
								.collect(Collectors.toList()),
							lwfeDto.isStartWithText(),
							lwfeDto.getPossibleAnswers()))
					.collect(Collectors.toList()),
				lwfDto.getDifficulty());
	}
	@Override
	public TaskDTO toDTO(Task entity)
	{
		ListWordFill lwfEntity = (ListWordFill) entity;
		return new ListWordFillDTO(
				lwfEntity.getInstruction(),
				lwfEntity.getTags(),
				lwfEntity.getDifficulty(),
				lwfEntity.getRows()
					.stream()
					.map(lwfeEntity -> new WordFillElementDTO(
							lwfeEntity.getText(),
							lwfeEntity.getEmptySpaces()
								.stream()
								.map(esEntity -> new EmptySpaceDTO(esEntity.getAnswer()))
								.collect(Collectors.toList()),
							lwfeEntity.isStartWithText(),
							lwfeEntity.getPossibleAnswers()))
					.collect(Collectors.toList()));
	}
}
