package com.projteam.competico.api;

import static com.projteam.competico.domain.Account.PLAYER_ROLE;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import com.projteam.competico.domain.Account;
import com.projteam.competico.dto.EmailChangeDTO;
import com.projteam.competico.dto.LoginDTO;
import com.projteam.competico.dto.PasswordChangeDTO;
import com.projteam.competico.dto.RegistrationDTO;
import com.projteam.competico.service.AccountService;
import com.projteam.competico.service.game.GameService;
import com.projteam.competico.service.game.GameTaskDataService;
import com.projteam.competico.service.game.LobbyService;

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
	
	@Test
	public void shouldReturnAccountInfo() throws Exception
	{
		UUID id = UUID.randomUUID();
		String email = "testAcc@test.pl";
		boolean emailVerified = true;
		String username = "TestAccount";
		String nickname = "TestAccount";
		String password = "QWERTY";
		Date creationDate = new Date();
		List<String> roles = List.of(PLAYER_ROLE);
		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		
		when(accountService.getAuthenticatedAccount())
			.thenReturn(Optional.of(new Account.Builder()
					.withID(id)
					.withEmail(email)
					.withEmailVerified(emailVerified)
					.withUsername(username)
					.withNickname(nickname)
					.withPassword(password)
					.withCreationDate(creationDate)
					.withRoles(roles)
					.build()));
		
		mvc.perform(get("/api/v1/account/info"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.authenticated", is(true)))
			.andExpect(jsonPath("$.email", is(email)))
			.andExpect(jsonPath("$.emailVerified", is(emailVerified)))
			.andExpect(jsonPath("$.username", is(username)))
			.andExpect(jsonPath("$.nickname", is(nickname)))
			.andExpect(jsonPath("$.creationDate", is(df.format(creationDate))))
			.andExpect(jsonPath("$.roles", hasSize(roles.size())))
			.andExpect(jsonPath("$.roles", containsInAnyOrder(roles.stream()
					.map(item -> is(item))
					.collect(Collectors.toList()))));
	}
	@Test
	public void shouldNotReturnAccountInfoWhenNotAuthenticated() throws Exception
	{
		when(accountService.getAuthenticatedAccount())
			.thenReturn(Optional.empty());
		
		mvc.perform(get("/api/v1/account/info"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.authenticated", is(false)));
	}
	@Test
	public void shouldReturnAccountInfoForGivenUser() throws Exception
	{
		UUID id = UUID.randomUUID();
		String email = "testAcc@test.pl";
		String username = "TestAccount";
		String nickname = "TestAccount";
		String password = "QWERTY";
		List<String> roles = List.of(PLAYER_ROLE);
		
		when(accountService.findByUsername(username))
			.thenReturn(Optional.of(new Account.Builder()
					.withID(id)
					.withEmail(email)
					.withUsername(username)
					.withNickname(nickname)
					.withPassword(password)
					.withRoles(roles)
					.build()));
		
		mvc.perform(get("/api/v1/account/" + username + "/info"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.username", is(username)))
			.andExpect(jsonPath("$.nickname", is(nickname)))
			.andExpect(jsonPath("$.roles", hasSize(roles.size())))
			.andExpect(jsonPath("$.roles", containsInAnyOrder(roles.stream()
					.map(item -> is(item))
					.collect(Collectors.toList()))));
	}
	@Test
	public void shouldNotifyWhenAccountInfoDoesNotExist() throws Exception
	{
		String username = "TestAccount";
		
		when(accountService.findByUsername(username))
			.thenReturn(Optional.empty());
		
		mvc.perform(get("/api/v1/account/" + username + "/info"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.exists", is(false)));
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
		doThrow(new IllegalArgumentException("DATA_ALREADY_USED"))
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
	
	@ParameterizedTest
	@ValueSource(booleans = {true, false})
	public void shouldUpdateEmail(boolean success) throws Exception
	{
		EmailChangeDTO eDto = new EmailChangeDTO("mockEmail@mock.org", "password123456");
		
		if (!success)
			Mockito.doThrow(new IllegalArgumentException())
				.when(accountService)
				.changeEmail(eDto.getEmail(), eDto.getPassword());
		
		mvc.perform(put("/api/v1/account/email")
				.contentType(APPLICATION_JSON_UTF8)
				.content(mapper.valueToTree(eDto).toString()))
			.andExpect(success?status().isOk():status().isBadRequest());
		
		verify(accountService, times(1)).changeEmail(eDto.getEmail(), eDto.getPassword());
	}
	@ParameterizedTest
	@ValueSource(booleans = {true, false})
	public void shouldUpdateNickname(boolean success) throws Exception
	{
		String nickname = "mockAccount";
		
		if (!success)
			Mockito.doThrow(new IllegalArgumentException())
				.when(accountService)
				.changeNickname(nickname);
		
		mvc.perform(put("/api/v1/account/nickname")
				.contentType(APPLICATION_JSON_UTF8)
				.content("" + nickname))
			.andExpect(success?status().isOk():status().isBadRequest());
		
		verify(accountService, times(1)).changeNickname(nickname);
	}
	@ParameterizedTest
	@ValueSource(booleans = {true, false})
	public void shouldUpdatePassword(boolean success) throws Exception
	{
		PasswordChangeDTO pcDto = new PasswordChangeDTO("oldPass123", "newPass456");
		
		if (!success)
			Mockito.doThrow(new IllegalArgumentException())
				.when(accountService)
				.changePassword(
						pcDto.getOldPassword(),
						pcDto.getNewPassword());
		
		mvc.perform(put("/api/v1/account/password")
				.contentType(APPLICATION_JSON_UTF8)
				.content(mapper.valueToTree(pcDto).toString()))
			.andExpect(success?status().isOk():status().isBadRequest());
		
		verify(accountService, times(1)).changePassword(
				pcDto.getOldPassword(),
				pcDto.getNewPassword());
	}
	
	@Test
	public void shouldRequestPasswordReset() throws Exception
	{
		String username = "TestAccount";
		
		mvc.perform(post("/api/v1/forgotpassword")
				.contentType(APPLICATION_JSON_UTF8)
				.content("" + username))
			.andExpect(status().isOk());
		
		verify(accountService, times(1)).requestPasswordReset(username);
	}
	@Test
	public void shouldReturnBadRequestWhenCannotRequestPasswordReset() throws Exception
	{
		String username = "TestAccount";
		
		String exceptionMessage = "Mock exception";
		doThrow(new IllegalArgumentException(exceptionMessage))
			.when(accountService)
			.requestPasswordReset(username);
		
		mvc.perform(post("/api/v1/forgotpassword")
				.contentType(APPLICATION_JSON_UTF8)
				.content("" + username))
			.andExpect(status().isBadRequest())
			.andExpect(content().string(containsString(exceptionMessage)));
		
		verify(accountService, times(1)).requestPasswordReset(username);
	}
	
	@Test
	public void shouldResetPassword() throws Exception
	{
		String token = UUID.randomUUID().toString();
		CharSequence newPassword = "testPass123";
		
		mvc.perform(post("/api/v1/resetpassword/" + token)
				.contentType(APPLICATION_JSON_UTF8)
				.content("" + newPassword))
			.andExpect(status().isOk());
		
		verify(accountService, times(1)).resetPassword(token, newPassword);
	}
	@Test
	public void shouldReturnBadRequestWhenCannotResetPassword() throws Exception
	{
		String token = UUID.randomUUID().toString();
		CharSequence newPassword = "testPass123";
		
		String exceptionMessage = "Mock exception";
		doThrow(new IllegalArgumentException(exceptionMessage))
			.when(accountService)
			.resetPassword(token, newPassword);
		
		mvc.perform(post("/api/v1/resetpassword/" + token)
				.contentType(APPLICATION_JSON_UTF8)
				.content("" + newPassword))
			.andExpect(status().isBadRequest())
			.andExpect(content().string(containsString(exceptionMessage)));
		
		verify(accountService, times(1)).resetPassword(token, newPassword);
	}
	
	@Test
	public void shouldRequestEmailVerification() throws Exception
	{
		mvc.perform(post("/api/v1/emailverification"))
			.andExpect(status().isOk());
		
		verify(accountService, times(1)).requestEmailVerification();
	}
	@Test
	public void shouldReturnBadRequestWhenCannotRequestEmailVerification() throws Exception
	{
		String exceptionMessage = "Mock exception";
		doThrow(new IllegalArgumentException(exceptionMessage))
			.when(accountService)
			.requestEmailVerification();
		
		mvc.perform(post("/api/v1/emailverification"))
			.andExpect(status().isBadRequest())
			.andExpect(content().string(containsString(exceptionMessage)));
		
		verify(accountService, times(1)).requestEmailVerification();
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