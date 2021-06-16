package com.projteam.competico.dto;

import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class LoginDTOTests
{
	@Test
	public void shouldCreateObjectWithCorrectData()
	{
		String email = "email";
		String password = "pass";
		LoginDTO loginDto = new LoginDTO(email, password);
		
		assertEquals(loginDto.getEmail(), email);
		assertEquals(loginDto.getPassword(), password);
	}

	@Test
	public void shouldChangeEmailOnSet()
	{
		String email = "email";
		
		LoginDTO loginDto = new LoginDTO("e", "p");
		loginDto.setEmail(email);
		assertEquals(loginDto.getEmail(), email);
	}
	@Test
	public void shouldChangePasswordOnSet()
	{
		String password = "pass";
		
		LoginDTO loginDto = new LoginDTO("e", "p");
		loginDto.setPassword(password);
		assertEquals(loginDto.getPassword(), password);
	}
	
	@ParameterizedTest
	@MethodSource("mockLoginDTO")
	public void shouldBeEqualWhenSameObjectCompared(LoginDTO logDto)
	{
		assertTrue(logDto.equals(logDto));
		assertEquals(logDto.hashCode(), logDto.hashCode());
	}
	@ParameterizedTest
	@MethodSource("mockTwoEqualLoginDTOs")
	public void shouldBeEqualWhenEqualObjectsCompared(LoginDTO logDto1, LoginDTO logDto2)
	{
		assertTrue(logDto1.equals(logDto2));
		assertEquals(logDto1.hashCode(), logDto2.hashCode());
	}
	@ParameterizedTest
	@MethodSource("mockTwoUnequalLoginDTOs")
	public void shouldNotBeEqualWhenUnequalObjectsCompared(LoginDTO logDto1, LoginDTO logDto2)
	{
		assertFalse(logDto1.equals(logDto2));
		assertDoesNotThrow(() -> logDto1.hashCode());
		assertDoesNotThrow(() -> logDto2.hashCode());
	}
	@ParameterizedTest
	@MethodSource("mockLoginDTO")
	public void shouldNotBeEqualWhenComparingToNull(LoginDTO logDto1)
	{
		assertFalse(logDto1.equals(null));
		assertDoesNotThrow(() -> logDto1.hashCode());
	}
	@ParameterizedTest
	@MethodSource("mockLoginDTO")
	public void shouldNotBeEqualWhenComparingToDifferentType(LoginDTO logDto1)
	{
		assertFalse(logDto1.equals(new Object()));
		assertDoesNotThrow(() -> logDto1.hashCode());
	}
	
	//---Sources---
	
	public static List<Arguments> mockLoginDTO()
	{
		return List.of(Arguments.of(new LoginDTO("email", "password")));
	}
	public static List<Arguments> mockTwoEqualLoginDTOs()
	{
		String email = "email";
		String password = "password";
		
		return List.of(
				Arguments.of(
						new LoginDTO(email, password),
						new LoginDTO(email, password)),
				Arguments.of(
						new LoginDTO(null, null),
						new LoginDTO(null, null)));
	}
	public static List<Arguments> mockTwoUnequalLoginDTOs()
	{
		String email = "email1";
		String otherEmail = "email2";
		String password = "pass1";
		String otherPassword = "pass2";
		
		return List.of(
				Arguments.of(
					new LoginDTO(email, password),
					new LoginDTO(otherEmail, otherPassword)),
				Arguments.of(
						new LoginDTO(null, password),
						new LoginDTO(null, otherPassword)),
				Arguments.of(
						new LoginDTO(email, null),
						new LoginDTO(otherEmail, null)),
				Arguments.of(
						new LoginDTO(null, null),
						new LoginDTO(email, password)),
				Arguments.of(
						new LoginDTO(email, password),
						new LoginDTO(null, null)),
				Arguments.of(
						new LoginDTO(email, password),
						new LoginDTO(null, password)),
				Arguments.of(
						new LoginDTO(null, password),
						new LoginDTO(otherEmail, password)),
				Arguments.of(
						new LoginDTO(email, password),
						new LoginDTO(email, null)),
				Arguments.of(
						new LoginDTO(email, null),
						new LoginDTO(email, password)));
	}
}
