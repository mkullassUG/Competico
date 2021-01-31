package com.projteam.app.api;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import com.projteam.app.dto.LoginDTO;
import com.projteam.app.dto.RegistrationDTO;
import com.projteam.app.service.AccountService;
import com.projteam.app.service.GameService;
import com.projteam.app.service.GameTaskDataService;
import com.projteam.app.service.LobbyService;

@SpringBootTest
@ContextConfiguration(name = "API-tests")
@AutoConfigureMockMvc(addFilters = false)
public class AccountAPITests
{
	@Autowired
	private MockMvc mvc;
	
	private @MockBean AccountService accountService;
	private @MockBean LobbyService lobbyService;
	private @MockBean GameService gameService;
	private @MockBean GameTaskDataService gtdService;
	
	private static final MediaType APPLICATION_JSON_UTF8 =
			new MediaType(MediaType.APPLICATION_JSON.getType(),
					MediaType.APPLICATION_JSON.getSubtype(),
					Charset.forName("utf8"));
	private static final String LOGIN_REDIRECT_URL = "/dashboard";
	
	private final ObjectMapper mapper = new ObjectMapper();
	
	@Test
	public void contextLoads() throws Exception
	{
		assertNotNull(mvc);
	}
	
	@ParameterizedTest
	@MethodSource("mockRegistrationData")
	public void shouldRegisterSuccessfully(RegistrationDTO mockRegDto) throws Exception
	{
		mvc.perform(post("/api/v1/register")
				.contentType(APPLICATION_JSON_UTF8)
				.content(toJson(mockRegDto)))
			.andExpect(status().isCreated());
		
		verify(accountService, times(1)).register(any(), eq(mockRegDto), anyBoolean());
		verifyNoMoreInteractions(accountService);
	}
	@ParameterizedTest
	@MethodSource("mockRegistrationData")
	public void shouldReturnBadRequestWhenUserAlreadyRegistered(RegistrationDTO mockRegDto) throws Exception
	{
		doThrow(new IllegalStateException("Account with provided data already exists"))
			.when(accountService)
			.register(any(), eq(mockRegDto), eq(true));
		
		mvc.perform(post("/api/v1/register")
				.contentType(APPLICATION_JSON_UTF8)
				.content(toJson(mockRegDto)))
			.andExpect(status().isBadRequest());
		
		verify(accountService, times(1)).register(any(), eq(mockRegDto), anyBoolean());
		verifyNoMoreInteractions(accountService);
	}
	
	@ParameterizedTest
	@MethodSource("mockLoginData")
	public void shouldLoginSuccessfully(LoginDTO mockLoginDto) throws Exception
	{
		when(accountService.login(any(), eq(mockLoginDto)))
			.thenReturn(true);
		
		mvc.perform(post("/api/v1/login")
				.contentType(APPLICATION_JSON_UTF8)
				.content(toJson(mockLoginDto)))
			.andExpect(status().isOk());
		
		verify(accountService, times(1)).login(any(), eq(mockLoginDto));
		verifyNoMoreInteractions(accountService);
	}
	@ParameterizedTest
	@MethodSource("mockLoginData")
	public void shouldReturnBadRequestWhenUserDoesNotExist(LoginDTO mockLoginDto) throws Exception
	{
		when(accountService.login(any(), eq(mockLoginDto)))
			.thenReturn(false);
		
		mvc.perform(post("/api/v1/login")
				.contentType(APPLICATION_JSON_UTF8)
				.content(toJson(mockLoginDto)))
			.andExpect(status().isBadRequest());
		
		verify(accountService, times(1)).login(any(), eq(mockLoginDto));
		verifyNoMoreInteractions(accountService);
	}
	
	@ParameterizedTest
	@ValueSource(booleans = {false, true})
	public void shouldReturnCheckIfUserIsAuthenticated(boolean isAuthenticated) throws Exception
	{
		when(accountService.isAuthenticated()).thenReturn(isAuthenticated);
		
		mvc.perform(get("/api/v1/authenticated")
				.contentType(APPLICATION_JSON_UTF8)
				.content("" + isAuthenticated))
			.andExpect(status().isOk())
			.andExpect(content().string("" + isAuthenticated));
	}
	
	@Test
	public void shouldRedirectFromRegisterWhenAlreadyLoggedIn() throws Exception
	{
		when(accountService.isAuthenticated())
			.thenReturn(true);
		
		mvc.perform(get("/register"))
			.andExpect(redirectedUrl(LOGIN_REDIRECT_URL));
	}
	@Test
	public void shouldRedirectFromLoginWhenAlreadyLoggedIn() throws Exception
	{
		when(accountService.isAuthenticated())
			.thenReturn(true);
		
		mvc.perform(get("/login"))
			.andExpect(redirectedUrl(LOGIN_REDIRECT_URL));
	}
	@Test
	public void shouldDisplayRegisterPageWhenAlreadyLoggedIn() throws Exception
	{
		when(accountService.isAuthenticated())
			.thenReturn(false);
		
		mvc.perform(get("/register"))
			.andExpect(status().isOk());
	}
	@Test
	public void shouldDisplayLoginPageWhenAlreadyLoggedIn() throws Exception
	{
		when(accountService.isAuthenticated())
			.thenReturn(false);
		
		mvc.perform(get("/login"))
			.andExpect(status().isOk());
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
	
	public String toJson(Object o) throws JsonProcessingException
	{
		return mapper.writeValueAsString(o);
	}
}