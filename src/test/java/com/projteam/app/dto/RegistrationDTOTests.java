package com.projteam.app.dto;

import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class RegistrationDTOTests
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
		boolean isPlayer1 = true;
		boolean isPlayer2 = false;
		
		RegistrationDTO regDTO = new RegistrationDTO("e", "u", "p", false);
		regDTO.setPlayer(isPlayer1);
		assertEquals(regDTO.isPlayer(), isPlayer1);
		regDTO.setIsPlayer(isPlayer2);
		assertEquals(regDTO.isPlayer(), isPlayer2);
	}
	
	@ParameterizedTest
	@MethodSource("mockRegistrationDTO")
	public void shouldBeEqualWhenSameObjectCompared(RegistrationDTO regDto)
	{
		assertTrue(regDto.equals(regDto));
		assertEquals(regDto.hashCode(), regDto.hashCode());
	}
	@ParameterizedTest
	@MethodSource("mockTwoEqualRegistrationDTOs")
	public void shouldBeEqualWhenEqualObjectsCompared(RegistrationDTO regDto1, RegistrationDTO regDto2)
	{
		assertTrue(regDto1.equals(regDto2));
		assertEquals(regDto1.hashCode(), regDto2.hashCode());
	}
	@ParameterizedTest
	@MethodSource("mockTwoUnequalRegistrationDTOs")
	public void shouldNotBeEqualWhenUnequalObjectsCompared(RegistrationDTO regDto1, RegistrationDTO regDto2)
	{
		assertFalse(regDto1.equals(regDto2));
		assertDoesNotThrow(() -> regDto1.hashCode());
		assertDoesNotThrow(() -> regDto2.hashCode());
	}
	@ParameterizedTest
	@MethodSource("mockRegistrationDTO")
	public void shouldNotBeEqualWhenComparingToNull(RegistrationDTO regDto1)
	{
		assertFalse(regDto1.equals(null));
		assertDoesNotThrow(() -> regDto1.hashCode());
	}
	@ParameterizedTest
	@MethodSource("mockRegistrationDTO")
	public void shouldNotBeEqualWhenComparingToDifferentType(RegistrationDTO regDto1)
	{
		assertFalse(regDto1.equals(new Object()));
		assertDoesNotThrow(() -> regDto1.hashCode());
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
		String email = "email1";
		String otherEmail = "email2";
		String username = "username1";
		String otherUsername = "username2";
		String password = "pass1";
		String otherPassword = "pass2";
		boolean isPlayer = true;
		boolean otherIsPlayer = false;
		
		return List.of(
				Arguments.of(
					new RegistrationDTO(email, username, password, isPlayer),
					new RegistrationDTO(otherEmail, otherUsername, otherPassword, otherIsPlayer)),
				Arguments.of(
						new RegistrationDTO(email, username, password, isPlayer),
						new RegistrationDTO(otherEmail, username, password, isPlayer)),
				Arguments.of(
						new RegistrationDTO(email, username, password, isPlayer),
						new RegistrationDTO(email, otherUsername, password, isPlayer)),
				Arguments.of(
						new RegistrationDTO(email, username, password, isPlayer),
						new RegistrationDTO(email, username, otherPassword, isPlayer)),
				Arguments.of(
						new RegistrationDTO(email, username, password, isPlayer),
						new RegistrationDTO(email, username, password, otherIsPlayer)),
				Arguments.of(
						new RegistrationDTO(null, username, password, isPlayer),
						new RegistrationDTO(email, username, password, isPlayer)),
				Arguments.of(
						new RegistrationDTO(email, username, password, isPlayer),
						new RegistrationDTO(null, username, password, isPlayer)),
				Arguments.of(
						new RegistrationDTO(null, username, password, isPlayer),
						new RegistrationDTO(null, otherUsername, password, isPlayer)),
				Arguments.of(
						new RegistrationDTO(null, username, password, isPlayer),
						new RegistrationDTO(null, username, otherPassword, isPlayer)),
				Arguments.of(
						new RegistrationDTO(null, username, password, isPlayer),
						new RegistrationDTO(null, username, password, otherIsPlayer)),
				Arguments.of(
						new RegistrationDTO(email, null, password, isPlayer),
						new RegistrationDTO(email, username, password, isPlayer)),
				Arguments.of(
						new RegistrationDTO(email, username, password, isPlayer),
						new RegistrationDTO(email, null, password, isPlayer)),
				Arguments.of(
						new RegistrationDTO(email, null, password, isPlayer),
						new RegistrationDTO(otherEmail, null, password, isPlayer)),
				Arguments.of(
						new RegistrationDTO(email, null, password, isPlayer),
						new RegistrationDTO(email, null, otherPassword, isPlayer)),
				Arguments.of(
						new RegistrationDTO(email, null, password, isPlayer),
						new RegistrationDTO(email, null, password, otherIsPlayer)),
				Arguments.of(
						new RegistrationDTO(email, username, null, isPlayer),
						new RegistrationDTO(email, username, password, isPlayer)),
				Arguments.of(
						new RegistrationDTO(email, username, password, isPlayer),
						new RegistrationDTO(email, username, null, isPlayer)),
				Arguments.of(
						new RegistrationDTO(email, username, null, isPlayer),
						new RegistrationDTO(otherEmail, username, null, isPlayer)),
				Arguments.of(
						new RegistrationDTO(email, username, null, isPlayer),
						new RegistrationDTO(email, otherUsername, null, isPlayer)),
				Arguments.of(
						new RegistrationDTO(email, username, null, isPlayer),
						new RegistrationDTO(email, username, null, otherIsPlayer)));
	}
}
