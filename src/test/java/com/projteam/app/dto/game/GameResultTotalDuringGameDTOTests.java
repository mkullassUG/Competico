package com.projteam.app.dto.game;

import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

class GameResultTotalDuringGameDTOTests
{

	@ParameterizedTest
	@ValueSource(strings = {"username", "mockUsername", "player1"})
	public void canSetUsername(String username)
	{
		GameResultTotalDuringGameDTO dto =
				new GameResultTotalDuringGameDTO();
		dto.setUsername(username);
		
		assertEquals(dto.getUsername(), username);
	}
	@ParameterizedTest
	@ValueSource(strings = {"username", "mockUsername", "player1"})
	public void canSetNickname(String nickname)
	{
		GameResultTotalDuringGameDTO dto =
				new GameResultTotalDuringGameDTO();
		dto.setNickname(nickname);
		
		assertEquals(dto.getNickname(), nickname);
	}
	@ParameterizedTest
	@ValueSource(ints = {0, 1, 2, 5, 10, 125, 1080, 99999})
	public void canSetTotalScore(long totalScore)
	{
		GameResultTotalDuringGameDTO dto =
				new GameResultTotalDuringGameDTO();
		dto.setTotalScore(totalScore);
		
		assertEquals(dto.getTotalScore(), totalScore);
	}
	@ParameterizedTest
	@ValueSource(ints = {0, 1, 2, 5, 10, 125, 1080, 99999})
	public void canSetTotalTime(long totalTime)
	{
		GameResultTotalDuringGameDTO dto =
				new GameResultTotalDuringGameDTO();
		dto.setTotalTime(totalTime);
		
		assertEquals(dto.getTotalTime(), totalTime);
	}
	@ParameterizedTest
	@ValueSource(booleans = {false, true})
	public void canSetHasFinished(boolean hasFinished)
	{
		GameResultTotalDuringGameDTO dto =
				new GameResultTotalDuringGameDTO();
		dto.setHasFinished(hasFinished);
		
		assertEquals(dto.isHasFinished(), hasFinished);
	}
	@ParameterizedTest
	@ValueSource(booleans = {false, true})
	public void canSetRemovedForInactivity(boolean removedForInactivity)
	{
		GameResultTotalDuringGameDTO dto =
				new GameResultTotalDuringGameDTO();
		dto.setRemovedForInactivity(removedForInactivity);
		
		assertEquals(dto.isRemovedForInactivity(), removedForInactivity);
	}
	
	@ParameterizedTest
	@MethodSource("equalDTOs")
	public void shouldBeEqualsForEqualObjects(
			GameResultTotalDuringGameDTO dto1,
			GameResultTotalDuringGameDTO dto2)
	{
		assertEquals(dto1, dto2);
		assertEquals(dto1.hashCode(), dto2.hashCode());
	}
	@ParameterizedTest
	@MethodSource("unequalDTOs")
	public void shouldNotBeEqualsForUnequalObjects(
			GameResultTotalDuringGameDTO dto1,
			GameResultTotalDuringGameDTO dto2)
	{
		assertNotEquals(dto1, dto2);
		assertDoesNotThrow(() -> dto1.hashCode());
		assertDoesNotThrow(() -> dto2.hashCode());
	}
	@ParameterizedTest
	@MethodSource({"emptyDTO", "fullDTO"})
	public void canConvertToString(GameResultTotalDuringGameDTO dto)
	{
		assertNotNull(dto.toString());
	}
	
	//---Sources---
	
	public static List<Arguments> emptyDTO()
	{
		return List.of(Arguments.of(
				new GameResultTotalDuringGameDTO(
						null, null, 0, 0, false, true)));
	}
	public static List<Arguments> fullDTO()
	{
		return List.of(Arguments.of(
				new GameResultTotalDuringGameDTO(
						"sameUser", "sameNickname", 2000, 2500, true, false)));
	}
	public static List<Arguments> equalDTOs()
	{
		GameResultTotalDuringGameDTO sameObj =
				new GameResultTotalDuringGameDTO(
						"sameUser", "sameNickname", 2000, 2500, false, false);
		return List.of(
				Arguments.of(new GameResultTotalDuringGameDTO(),
						new GameResultTotalDuringGameDTO()),
				Arguments.of(new GameResultTotalDuringGameDTO(
							"username", "nickname", 1000, 2000, false, false),
						new GameResultTotalDuringGameDTO(
							"username", "nickname", 1000, 2000, false, false)),
				Arguments.of(new GameResultTotalDuringGameDTO(
							"mockUser", "mockNick", 5555, 7777, true, false),
						new GameResultTotalDuringGameDTO(
							"mockUser", "mockNick", 5555, 7777, true, false)),
				Arguments.of(new GameResultTotalDuringGameDTO(
							"user", "mockNick", 2345, 6789, false, true),
						new GameResultTotalDuringGameDTO(
							"user", "mockNick", 2345, 6789, false, true)),
				Arguments.of(new GameResultTotalDuringGameDTO(
							"mockUser", "nick", 8000, 6666, true, true),
						new GameResultTotalDuringGameDTO(
							"mockUser", "nick", 8000, 6666, true, true)),
				Arguments.of(new GameResultTotalDuringGameDTO(
						null, null, 0, 0, true, true),
					new GameResultTotalDuringGameDTO(
						null, null, 0, 0, true, true)),
				Arguments.of(sameObj, sameObj));
	}
	public static List<Arguments> unequalDTOs()
	{
		return List.of(Arguments.of(new GameResultTotalDuringGameDTO(
						"user", "nickname", 1000, 2000, false, false),
					new GameResultTotalDuringGameDTO(
						"username", "nickname", 1000, 2000, false, false)),
				Arguments.of(new GameResultTotalDuringGameDTO(
						"username", "nickname", 1000, 2000, false, false),
					new GameResultTotalDuringGameDTO(
						"username", "nick", 1000, 2000, false, false)),
				Arguments.of(new GameResultTotalDuringGameDTO(
						"username", "nickname", 100, 2000, false, false),
					new GameResultTotalDuringGameDTO(
						"username", "nickname", 1000, 2000, false, false)),
				Arguments.of(new GameResultTotalDuringGameDTO(
						"username", "nickname", 1000, 2000, false, false),
					new GameResultTotalDuringGameDTO(
						"username", "nickname", 1000, 200, false, false)),
				Arguments.of(new GameResultTotalDuringGameDTO(
						"username", "nickname", 1000, 2000, false, false),
					new GameResultTotalDuringGameDTO(
						"username", "nickname", 1000, 2000, true, false)),
				Arguments.of(new GameResultTotalDuringGameDTO(
						"username", "nickname", 1000, 2000, false, false),
					new GameResultTotalDuringGameDTO(
						"username", "nickname", 1000, 2000, false, true)),
				Arguments.of(new GameResultTotalDuringGameDTO(
						null, "nickname", 1000, 2000, false, false),
					new GameResultTotalDuringGameDTO(
						"username", "nickname", 1000, 2000, false, false)),
				Arguments.of(new GameResultTotalDuringGameDTO(
						"username", "nickname", 1000, 2000, false, false),
					new GameResultTotalDuringGameDTO(
						null, "nickname", 1000, 2000, false, false)),
				Arguments.of(new GameResultTotalDuringGameDTO(
						"username", null, 1000, 2000, false, false),
					new GameResultTotalDuringGameDTO(
						"username", "nickname", 1000, 2000, false, false)),
				Arguments.of(new GameResultTotalDuringGameDTO(
						"username", "nickname", 1000, 2000, false, false),
					new GameResultTotalDuringGameDTO(
						"username", null, 1000, 2000, false, false)));
	}
}
