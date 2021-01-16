package com.projteam.app.dto.lobby;

import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

class LobbyOptionsDTOTest
{
	@Test
	public void shouldCreateObjectWithCorrectData()
	{
		int maxPlayers = 25;
		boolean allowsRandomPlayers = true;
		LobbyOptionsDTO loDto = new LobbyOptionsDTO(maxPlayers, allowsRandomPlayers);
		
		assertEquals(loDto.getMaxPlayers(), maxPlayers);
		assertEquals(loDto.isAllowsRandomPlayers(), allowsRandomPlayers);
		assertEquals(loDto.allowsRandomPlayers(), allowsRandomPlayers);
	}
	
	@ParameterizedTest
	@ValueSource(ints = {10, 15, 20, 25, 50})
	public void shouldChangeMaxPlayersOnSet(int maxPlayers)
	{
		LobbyOptionsDTO loDto = new LobbyOptionsDTO();
		loDto.setMaxPlayers(maxPlayers);
		
		assertEquals(loDto.getMaxPlayers(), maxPlayers);
	}
	@ParameterizedTest
	@ValueSource(booleans = {false, true})
	public void shouldChangeMaxPlayersOnSet(boolean allowsRandomPlayers)
	{
		LobbyOptionsDTO loDto = new LobbyOptionsDTO();
		loDto.setAllowsRandomPlayers(allowsRandomPlayers);
		
		assertEquals(loDto.isAllowsRandomPlayers(), allowsRandomPlayers);
		assertEquals(loDto.allowsRandomPlayers(), allowsRandomPlayers);
	}
	
	@ParameterizedTest
	@MethodSource("mockTwoEqualLobbyOptionsDTOs")
	public void shouldBeEqualWhenEqualObjectsCompared(LobbyOptionsDTO loDto1,
			LobbyOptionsDTO loDto2)
	{
		assertTrue(loDto1.equals(loDto2));
		assertEquals(loDto1.hashCode(), loDto2.hashCode());
	}
	@ParameterizedTest
	@MethodSource("mockTwoUnequalLobbyOptionsDTOs")
	public void shouldNotBeEqualWhenUnequalObjectsCompared(LobbyOptionsDTO loDto1,
			LobbyOptionsDTO loDto2)
	{
		assertFalse(loDto1.equals(loDto2));
		assertDoesNotThrow(() -> loDto1.hashCode());
		assertDoesNotThrow(() -> loDto2.hashCode());
	}
	@Test
	public void shouldBeEqualWhenComparedToItself()
	{
		int maxPlayers = 25;
		boolean allowsRandomPlayers = true;
		LobbyOptionsDTO loDto = new LobbyOptionsDTO(maxPlayers, allowsRandomPlayers);
		
		assertTrue(loDto.equals(loDto));
	}
	@Test
	public void shouldNotBeEqualWhenComparedToDifferentType()
	{
		int maxPlayers = 25;
		boolean allowsRandomPlayers = true;
		LobbyOptionsDTO loDto = new LobbyOptionsDTO(maxPlayers, allowsRandomPlayers);
		
		assertFalse(loDto.equals(new Object()));
	}
	@Test
	public void shouldNotBeEqualWhenComparedToNull()
	{
		int maxPlayers = 25;
		boolean allowsRandomPlayers = true;
		LobbyOptionsDTO loDto = new LobbyOptionsDTO(maxPlayers, allowsRandomPlayers);
		
		assertFalse(loDto.equals(null));
	}
	
	@Test
	public void shouldConvertToStringSuccessfully()
	{
		int maxPlayers = 25;
		boolean allowsRandomPlayers = true;
		LobbyOptionsDTO loDto = new LobbyOptionsDTO(maxPlayers, allowsRandomPlayers);
		
		assertNotNull(loDto.toString());
	}
	
	//---Sources---
	
	public static List<Arguments> mockTwoEqualLobbyOptionsDTOs()
	{
		return List.of(
				Arguments.of(
					new LobbyOptionsDTO(10, true),
					new LobbyOptionsDTO(10, true)),
				Arguments.of(
						new LobbyOptionsDTO(5, false),
						new LobbyOptionsDTO(5, false)),
				Arguments.of(
						new LobbyOptionsDTO(15, true),
						new LobbyOptionsDTO(15, true)),
				Arguments.of(
						new LobbyOptionsDTO(25, false),
						new LobbyOptionsDTO(25, false)));
	}
	public static List<Arguments> mockTwoUnequalLobbyOptionsDTOs()
	{
		return List.of(
				Arguments.of(
					new LobbyOptionsDTO(10, true),
					new LobbyOptionsDTO(10, false)),
				Arguments.of(
						new LobbyOptionsDTO(5, false),
						new LobbyOptionsDTO(15, false)),
				Arguments.of(
						new LobbyOptionsDTO(15, false),
						new LobbyOptionsDTO(15, true)),
				Arguments.of(
						new LobbyOptionsDTO(20, true),
						new LobbyOptionsDTO(25, true)));
	}
}
