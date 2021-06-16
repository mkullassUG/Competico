package com.projteam.competico.dto.game;

import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

class GameResultPersonalDTOTests
{
	@ParameterizedTest
	@ValueSource(doubles = {0, 0.1, 0.25, 0.5, 0.75, 0.8, 0.9, 0.95, 1})
	public void canSetCompletion(double completion)
	{
		GameResultPersonalDTO dto =
				new GameResultPersonalDTO();
		dto.setCompletion(completion);
		
		assertEquals(dto.getCompletion(), completion);
	}
	@ParameterizedTest
	@ValueSource(longs = {0, 1, 2, 5, 10, 100, 1000, 2500, 9999})
	public void canSetTimeTaken(long timeTaken)
	{
		GameResultPersonalDTO dto =
				new GameResultPersonalDTO();
		dto.setTimeTaken(timeTaken);
		
		assertEquals(dto.getTimeTaken(), timeTaken);
	}
	@ParameterizedTest
	@ValueSource(doubles = {0, 0.1, 0.25, 0.5, 0.75, 0.8, 0.9, 0.95, 1})
	public void canSetDifficulty(double difficulty)
	{
		GameResultPersonalDTO dto =
				new GameResultPersonalDTO();
		dto.setDifficulty(difficulty);
		
		assertEquals(dto.getDifficulty(), difficulty);
	}
	
	@ParameterizedTest
	@MethodSource("equalDTOs")
	public void shouldBeEqualsForEqualObjects(
			GameResultPersonalDTO dto1,
			GameResultPersonalDTO dto2)
	{
		assertEquals(dto1, dto2);
		assertEquals(dto1.hashCode(), dto2.hashCode());
	}
	@ParameterizedTest
	@MethodSource("unequalDTOs")
	public void shouldNotBeEqualsForUnequalObjects(
			GameResultPersonalDTO dto1,
			GameResultPersonalDTO dto2)
	{
		assertNotEquals(dto1, dto2);
		assertDoesNotThrow(() -> dto1.hashCode());
		assertDoesNotThrow(() -> dto2.hashCode());
	}
	@ParameterizedTest
	@MethodSource("mockDTO")
	public void canConvertToString(GameResultPersonalDTO dto)
	{
		assertNotNull(dto.toString());
	}
	
	//---Sources---
	
	public static List<Arguments> equalDTOs()
	{
		GameResultPersonalDTO sameObj = new GameResultPersonalDTO(0.75, 2000, 100);
		return List.of(Arguments.of(
						new GameResultPersonalDTO(),
						new GameResultPersonalDTO()),
				Arguments.of(
						new GameResultPersonalDTO(1, 1000, 100),
						new GameResultPersonalDTO(1, 1000, 100)),
				Arguments.of(
						new GameResultPersonalDTO(0.5, 2500, 125),
						new GameResultPersonalDTO(0.5, 2500, 125)),
				Arguments.of(sameObj, sameObj));
	}
	public static List<Arguments> unequalDTOs()
	{
		return List.of(Arguments.of(
						new GameResultPersonalDTO(1, 1000, 100),
						new GameResultPersonalDTO(0.8, 1000, 100)),
				Arguments.of(
						new GameResultPersonalDTO(0.5, 2400, 125),
						new GameResultPersonalDTO(0.5, 2500, 125)),
				Arguments.of(
						new GameResultPersonalDTO(0.5, 2500, 110),
						new GameResultPersonalDTO(0.5, 2500, 125)));
	}
	public static List<Arguments> mockDTO()
	{
		return List.of(
				Arguments.of(new GameResultPersonalDTO()),
				Arguments.of(new GameResultPersonalDTO(1, 1000, 100)));
	}
}
