package com.projteam.competico.dto.game;

import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

class GameResultTotalDTOTests
{

	@ParameterizedTest
	@ValueSource(strings = {"username", "mockUsername", "player1"})
	public void canSetUsername(String username)
	{
		GameResultTotalDTO dto =
				new GameResultTotalDTO();
		dto.setUsername(username);
		
		assertEquals(dto.getUsername(), username);
	}
	@ParameterizedTest
	@ValueSource(strings = {"username", "mockUsername", "player1"})
	public void canSetNickname(String nickname)
	{
		GameResultTotalDTO dto =
				new GameResultTotalDTO();
		dto.setNickname(nickname);
		
		assertEquals(dto.getNickname(), nickname);
	}
	@ParameterizedTest
	@ValueSource(ints = {0, 1, 2, 5, 10, 125, 1080, 99999})
	public void canSetTotalScore(long totalScore)
	{
		GameResultTotalDTO dto =
				new GameResultTotalDTO();
		dto.setTotalScore(totalScore);
		
		assertEquals(dto.getTotalScore(), totalScore);
	}
	@ParameterizedTest
	@ValueSource(ints = {0, 1, 2, 5, 10, 125, 1080, 99999})
	public void canSetTotalTime(long totalTime)
	{
		GameResultTotalDTO dto =
				new GameResultTotalDTO();
		dto.setTotalTime(totalTime);
		
		assertEquals(dto.getTotalTime(), totalTime);
	}
	
	@ParameterizedTest
	@MethodSource("equalDTOs")
	public void shouldBeEqualsForEqualObjects(
			GameResultTotalDTO dto1,
			GameResultTotalDTO dto2)
	{
		assertEquals(dto1, dto2);
		assertEquals(dto1.hashCode(), dto2.hashCode());
	}
	@ParameterizedTest
	@MethodSource("unequalDTOs")
	public void shouldNotBeEqualsForUnequalObjects(
			GameResultTotalDTO dto1,
			GameResultTotalDTO dto2)
	{
		assertNotEquals(dto1, dto2);
		assertDoesNotThrow(() -> dto1.hashCode());
		assertDoesNotThrow(() -> dto2.hashCode());
	}
	@ParameterizedTest
	@MethodSource({"emptyDTO", "fullDTO"})
	public void canConvertToString(GameResultTotalDTO dto)
	{
		assertNotNull(dto.toString());
	}
	
	//---Sources---
	
	public static List<Arguments> emptyDTO()
	{
		return List.of(Arguments.of(
				new GameResultTotalDTO(
						null, null, 0, 0, false)));
	}
	public static List<Arguments> fullDTO()
	{
		return List.of(Arguments.of(
				new GameResultTotalDTO(
						"sameUser", "sameNickname", 2000, 2500, false)));
	}
	public static List<Arguments> equalDTOs()
	{
		GameResultTotalDTO sameObj =
				new GameResultTotalDTO(
						"sameUser", "sameNickname", 2000, 2500, false);
		return List.of(
				Arguments.of(new GameResultTotalDTO(),
						new GameResultTotalDTO()),
				Arguments.of(new GameResultTotalDTO(
							"username", "nickname", 1000, 2000, false),
						new GameResultTotalDTO(
							"username", "nickname", 1000, 2000, false)),
				Arguments.of(new GameResultTotalDTO(
						"username", "nickname", 1000, 2000, true),
					new GameResultTotalDTO(
						"username", "nickname", 1000, 2000, true)),
				Arguments.of(new GameResultTotalDTO(
							"mockUser", "mockNick", 5555, 7777, false),
						new GameResultTotalDTO(
							"mockUser", "mockNick", 5555, 7777, false)),
				Arguments.of(new GameResultTotalDTO(
							"user", "mockNick", 2345, 6789, false),
						new GameResultTotalDTO(
							"user", "mockNick", 2345, 6789, false)),
				Arguments.of(new GameResultTotalDTO(
							"mockUser", "nick", 8000, 6666, false),
						new GameResultTotalDTO(
							"mockUser", "nick", 8000, 6666, false)),
				Arguments.of(new GameResultTotalDTO(
						null, null, 0, 0, false),
					new GameResultTotalDTO(
						null, null, 0, 0, false)),
				Arguments.of(sameObj, sameObj));
	}
	public static List<Arguments> unequalDTOs()
	{
		return List.of(Arguments.of(new GameResultTotalDTO(
						"user", "nickname", 1000, 2000, false),
					new GameResultTotalDTO(
						"username", "nickname", 1000, 2000, false)),
				Arguments.of(new GameResultTotalDTO(
						"username", "nickname", 1000, 2000, false),
					new GameResultTotalDTO(
						"username", "nick", 1000, 2000, false)),
				Arguments.of(new GameResultTotalDTO(
						"username", "nickname", 100, 2000, false),
					new GameResultTotalDTO(
						"username", "nickname", 1000, 2000, false)),
				Arguments.of(new GameResultTotalDTO(
						"username", "nickname", 1000, 2000, false),
					new GameResultTotalDTO(
						"username", "nickname", 1000, 200, false)),
				Arguments.of(new GameResultTotalDTO(
						null, "nickname", 1000, 2000, false),
					new GameResultTotalDTO(
						"username", "nickname", 1000, 2000, false)),
				Arguments.of(new GameResultTotalDTO(
						"username", "nickname", 1000, 2000, false),
					new GameResultTotalDTO(
						null, "nickname", 1000, 2000, false)),
				Arguments.of(new GameResultTotalDTO(
						"username", null, 1000, 2000, false),
					new GameResultTotalDTO(
						"username", "nickname", 1000, 2000, false)),
				Arguments.of(new GameResultTotalDTO(
						"username", "nickname", 1000, 2000, false),
					new GameResultTotalDTO(
						"username", null, 1000, 2000, false)));
	}
}
