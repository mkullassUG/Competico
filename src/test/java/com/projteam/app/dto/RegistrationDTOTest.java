package com.projteam.app.dto;

import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class RegistrationDTOTest
{
	@Test
	public void shouldCreateObjectWithCorrectData()
	{
		String email = "email";
		String username = "username";
		String password = "pass";
		boolean isPlayer = true;
		RegistrationDTO regDTO = new RegistrationDTO(email, username, password, isPlayer);
		
		assertEquals(regDTO.getEmail(), email);
		assertEquals(regDTO.getPassword(), password);
	}

	@Test
	public void shouldChangeEmailOnSet()
	{
		String email = "email";
		
		RegistrationDTO regDTO = new RegistrationDTO("e", "u", "p", false);
		regDTO.setEmail(email);
		assertEquals(regDTO.getEmail(), email);
	}
	@Test
	public void shouldChangeUsernameOnSet()
	{
		String username = "username";
		
		RegistrationDTO regDTO = new RegistrationDTO("e", "u", "p", false);
		regDTO.setUsername(username);
		assertEquals(regDTO.getUsername(), username);
	}
	@Test
	public void shouldChangePasswordOnSet()
	{
		String password = "pass";
		
		RegistrationDTO regDTO = new RegistrationDTO("e", "u", "p", false);
		regDTO.setPassword(password);
		assertEquals(regDTO.getPassword(), password);
	}
	@Test
	public void shouldChangePlayerStatusOnSet()
	{
		boolean isPlayer = true;
		
		RegistrationDTO regDTO = new RegistrationDTO("e", "u", "p", false);
		regDTO.setPlayer(isPlayer);
		assertEquals(regDTO.isPlayer(), isPlayer);
	}
	
	@ParameterizedTest
	@MethodSource("mockRegistrationDTO")
	void shouldBeEqualWhenSameObjectCompared(RegistrationDTO regDto)
	{
		assertTrue(regDto.equals(regDto));
		assertEquals(regDto.hashCode(), regDto.hashCode());
	}
	@ParameterizedTest
	@MethodSource("mockTwoEqualRegistrationDTOs")
	void shouldBeEqualWhenEqualObjectsCompared(RegistrationDTO regDto1, RegistrationDTO regDto2)
	{
		assertTrue(regDto1.equals(regDto2));
		assertEquals(regDto1.hashCode(), regDto2.hashCode());
	}
	@ParameterizedTest
	@MethodSource("mockTwoUnequalRegistrationDTOs")
	void shouldNotBeEqualWhenUnequalObjectsCompared(RegistrationDTO regDto1, RegistrationDTO regDto2)
	{
		assertFalse(regDto1.equals(regDto2));
	}
	
	//---Sources---
	
	public static List<Arguments> mockRegistrationDTO()
	{
		return List.of(Arguments.of(new RegistrationDTO("email", "username", "password", true)));
	}
	public static List<Arguments> mockTwoEqualRegistrationDTOs()
	{
		String email = "email";
		String username = "username";
		String password = "password";
		boolean isPlayer = true;
		
		return List.of(
				Arguments.of(
						new RegistrationDTO(email, username, password, isPlayer),
						new RegistrationDTO(email, username, password, isPlayer)),
				Arguments.of(
						new RegistrationDTO(null, null, null, false),
						new RegistrationDTO(null, null, null, false)),
				Arguments.of(
						new RegistrationDTO(null, null, null, true),
						new RegistrationDTO(null, null, null, true)));
	}
	public static List<Arguments> mockTwoUnequalRegistrationDTOs()
	{
		String email1 = "email1";
		String email2 = "email2";
		String username1 = "username1";
		String username2 = "username2";
		String password1 = "pass1";
		String password2 = "pass2";
		boolean isPlayer1 = true;
		boolean isPlayer2 = false;
		
		return List.of(
				Arguments.of(
					new RegistrationDTO(email1, username1, password1, isPlayer1),
					new RegistrationDTO(email2, username2, password2, isPlayer2)),
				Arguments.of(
						new RegistrationDTO(null, username1, password1, isPlayer1),
						new RegistrationDTO(email2, username2, password2, isPlayer2)),
				Arguments.of(
						new RegistrationDTO(email1, username1, password1, isPlayer1),
						new RegistrationDTO(null, username2, password2, isPlayer2)),
				Arguments.of(
						new RegistrationDTO(null, username1, password1, isPlayer1),
						new RegistrationDTO(null, username2, password2, isPlayer2)),
				Arguments.of(
						new RegistrationDTO(email1, null, password1, isPlayer1),
						new RegistrationDTO(email2, username2, password2, isPlayer2)),
				Arguments.of(
						new RegistrationDTO(email1, username1, password1, isPlayer1),
						new RegistrationDTO(email2, null, password2, isPlayer2)),
				Arguments.of(
						new RegistrationDTO(email1, null, password1, isPlayer1),
						new RegistrationDTO(email2, null, password2, isPlayer2)),
				Arguments.of(
						new RegistrationDTO(email1, username1, null, isPlayer1),
						new RegistrationDTO(email2, username2, password2, isPlayer2)),
				Arguments.of(
						new RegistrationDTO(email1, username1, password1, isPlayer1),
						new RegistrationDTO(email2, username2, null, isPlayer2)),
				Arguments.of(
						new RegistrationDTO(email1, username1, null, isPlayer1),
						new RegistrationDTO(email2, username2, null, isPlayer2)));
	}
}
