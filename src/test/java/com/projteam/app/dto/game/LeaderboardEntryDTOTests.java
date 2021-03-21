package com.projteam.app.dto.game;

import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

class LeaderboardEntryDTOTests
{

	@ParameterizedTest
	@ValueSource(strings = {"username", "testAccount", "gracz123"})
	public void canSetUsername(String username)
	{
		LeaderboardEntryDTO dto = new LeaderboardEntryDTO();
		
		dto.setUsername(username);
		
		assertEquals(dto.getUsername(), username);
	}
	@ParameterizedTest
	@ValueSource(strings = {"nickname", "testAccountNick", "gracz123", "gracz456"})
	public void canSetNickname(String nickname)
	{
		LeaderboardEntryDTO dto = new LeaderboardEntryDTO();
		
		dto.setNickname(nickname);
		
		assertEquals(dto.getNickname(), nickname);
	}
	@ParameterizedTest
	@ValueSource(ints = {0, 1, 2, 5, 10, 100, 1000, 999999, 7896})
	public void canSetPosition(int position)
	{
		LeaderboardEntryDTO dto = new LeaderboardEntryDTO();
		
		dto.setPosition(position);
		
		assertEquals(dto.getPosition(), position);
	}
	@ParameterizedTest
	@ValueSource(ints = {0, 1, 2, 5, 10, 100, 1000, 999999, 7896})
	public void canSetRating(int rating)
	{
		LeaderboardEntryDTO dto = new LeaderboardEntryDTO();
		
		dto.setRating(rating);
		
		assertEquals(dto.getRating(), rating);
	}
	
	@ParameterizedTest
	@MethodSource("equalDTOs")
	public void shouldBeEqualsForEqualObjects(
			LeaderboardEntryDTO dto1,
			LeaderboardEntryDTO dto2)
	{
		assertEquals(dto1, dto2);
		assertEquals(dto1.hashCode(), dto2.hashCode());
	}
	@ParameterizedTest
	@MethodSource("unequalDTOs")
	public void shouldNotBeEqualsForUnequalObjects(
			LeaderboardEntryDTO dto1,
			LeaderboardEntryDTO dto2)
	{
		assertNotEquals(dto1, dto2);
		assertDoesNotThrow(() -> dto1.hashCode());
		assertDoesNotThrow(() -> dto2.hashCode());
	}
	@ParameterizedTest
	@MethodSource({"emptyDTO", "fullDTO"})
	public void canConvertToString(LeaderboardEntryDTO dto)
	{
		assertNotNull(dto.toString());
	}
	
	//---Sources---
	
	public static List<Arguments> equalDTOs()
	{
		LeaderboardEntryDTO sameObj = new LeaderboardEntryDTO("us", "ni", 9, 87);
		return List.of(Arguments.of(
					new LeaderboardEntryDTO(),
					new LeaderboardEntryDTO()),
				Arguments.of(
					new LeaderboardEntryDTO("user", "nick", 123, 1234),
					new LeaderboardEntryDTO("user", "nick", 123, 1234)),
				Arguments.of(
					new LeaderboardEntryDTO(null, "nickname", 456, 5678),
					new LeaderboardEntryDTO(null, "nickname", 456, 5678)),
				Arguments.of(
					new LeaderboardEntryDTO("username", null, 789, 9123),
					new LeaderboardEntryDTO("username", null, 789, 9123)),
				Arguments.of(sameObj, sameObj));
	}
	public static List<Arguments> unequalDTOs()
	{
		return List.of(Arguments.of(
					new LeaderboardEntryDTO("user", "nick", 123, 1234),
					new LeaderboardEntryDTO("username", "nick", 123, 1234)),
				Arguments.of(
					new LeaderboardEntryDTO("user", "nick", 456, 5678),
					new LeaderboardEntryDTO("user", "nickname", 456, 5678)),
				Arguments.of(
					new LeaderboardEntryDTO("username", "nickname", 789, 9123),
					new LeaderboardEntryDTO("username", "nickname", 987, 9123)),
				Arguments.of(
					new LeaderboardEntryDTO("us", "ni", 234, 7412),
					new LeaderboardEntryDTO("us", "ni", 234, 2147)),
				Arguments.of(
					new LeaderboardEntryDTO("gracz123", "gracz123", 234, 7412),
					new LeaderboardEntryDTO("gracz456", "gracz456", 456, 456)));
	}
	public static List<Arguments> emptyDTO()
	{
		return List.of(Arguments.of(
				new LeaderboardEntryDTO(
						null, null, 0, 0)));
	}
	public static List<Arguments> fullDTO()
	{
		return List.of(Arguments.of(
				new LeaderboardEntryDTO(
						"someUser", "someNickname", 2000, 2500)));
	}
}
