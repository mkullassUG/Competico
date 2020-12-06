package com.projteam.app.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.Assert;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import com.projteam.app.dto.LoginDTO;
import com.projteam.app.dto.RegistrationDTO;
import com.projteam.app.service.AccountService;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class AccountAPITests
{
	@Autowired
	private MockMvc mvc;
	
	@MockBean
	private AccountService accServ;
	
	private static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));
	
	@BeforeEach
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void contextLoads() throws Exception
	{
		Assert.notNull(mvc, "Mock MVC not loaded");
	}
	
	@ParameterizedTest
	@MethodSource("mockRegistrationData")
	public void shouldRegisterSuccessfully(RegistrationDTO mockRegDto) throws Exception
	{
		mvc.perform(post("/api/v1/register")
				.contentType(APPLICATION_JSON_UTF8)
				.content(toJson(mockRegDto)))
			.andExpect(status().isCreated());
		
		verify(accServ).register(any(), eq(mockRegDto), eq(true));
		verifyNoMoreInteractions(accServ);
	}
	@ParameterizedTest
	@MethodSource("mockRegistrationData")
	public void shouldReturnBadRequestWhenUserAlreadyRegistered(RegistrationDTO mockRegDto) throws Exception
	{
		Mockito.doThrow(new IllegalStateException("Account with provided data already exists"))
			.when(accServ)
			.register(any(), eq(mockRegDto), eq(true));
		
		mvc.perform(post("/api/v1/register")
				.contentType(APPLICATION_JSON_UTF8)
				.content(toJson(mockRegDto)))
			.andExpect(status().isBadRequest());
		
		verify(accServ).register(any(), eq(mockRegDto), eq(true));
		verifyNoMoreInteractions(accServ);
	}
	
	@ParameterizedTest
	@MethodSource("mockLoginData")
	public void shouldLoginSuccessfully(LoginDTO mockLoginDto) throws Exception
	{
		when(accServ.login(any(), eq(mockLoginDto)))
			.thenReturn(true);
		
		mvc.perform(post("/api/v1/login")
				.contentType(APPLICATION_JSON_UTF8)
				.content(toJson(mockLoginDto)))
			.andExpect(status().isOk());
		
		verify(accServ).login(any(), eq(mockLoginDto));
		verifyNoMoreInteractions(accServ);
	}
	@ParameterizedTest
	@MethodSource("mockLoginData")
	public void shouldReturnBadRequestWhenUserDoesNotExist(LoginDTO mockLoginDto) throws Exception
	{
		when(accServ.login(any(), eq(mockLoginDto)))
			.thenReturn(false);
		
		mvc.perform(post("/api/v1/login")
				.contentType(APPLICATION_JSON_UTF8)
				.content(toJson(mockLoginDto)))
			.andExpect(status().isBadRequest());
		
		verify(accServ).login(any(), eq(mockLoginDto));
		verifyNoMoreInteractions(accServ);
	}
	
	//---Sources---
	
	public static List<Arguments> mockRegistrationData()
	{
		List<Arguments> ret = new ArrayList<>();
		
		ret.add(Arguments.of(new RegistrationDTO(
				"testplayer@test.pl",
				"TestPlayer",
				"QWERTY",
				true)));
		ret.add(Arguments.of(new RegistrationDTO(
				"testlecturer@test.pl",
				"TestLecturer",
				"QWERTY",
				false)));
		
		return ret;
	}
	
	public static List<Arguments> mockLoginData()
	{
		List<Arguments> ret = new ArrayList<>();
		
		ret.add(Arguments.of(new LoginDTO(
				"testplayer@test.pl",
				"QWERTY")));
		
		return ret;
	}
	
	//---Helpers---
	
	public static String toJson(Object o) throws JsonProcessingException
	{
		return new ObjectMapper().writeValueAsString(o);
	}
}