package com.projteam.app.dto;

import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import com.projteam.app.domain.Account;

class LoginDTOTest
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
	void shouldBeEqualWhenSameObjectCompared(LoginDTO logDto)
	{
		assertTrue(logDto.equals(logDto));
		assertEquals(logDto.hashCode(), logDto.hashCode());
	}
	@ParameterizedTest
	@MethodSource("mockTwoEqualLoginDTOs")
	void shouldBeEqualWhenEqualObjectsCompared(LoginDTO logDto1, LoginDTO logDto2)
	{
		assertTrue(logDto1.equals(logDto2));
		assertEquals(logDto1.hashCode(), logDto2.hashCode());
	}
	@ParameterizedTest
	@MethodSource("mockTwoUnequalLoginDTOs")
	void shouldNotBeEqualWhenUnequalObjectsCompared(LoginDTO logDto1, LoginDTO logDto2)
	{
		assertFalse(logDto1.equals(logDto2));
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
		String email1 = "email1";
		String email2 = "email2";
		String password1 = "pass1";
		String password2 = "pass2";
		
		return List.of(
				Arguments.of(
					new LoginDTO(email1, password1),
					new LoginDTO(email2, password2)),
				Arguments.of(
						new LoginDTO(null, password1),
						new LoginDTO(null, password2)),
				Arguments.of(
						new LoginDTO(email1, null),
						new LoginDTO(email2, null)),
				Arguments.of(
						new LoginDTO(null, null),
						new LoginDTO(email2, password2)),
				Arguments.of(
						new LoginDTO(email1, password1),
						new LoginDTO(null, null)));
	}
}
