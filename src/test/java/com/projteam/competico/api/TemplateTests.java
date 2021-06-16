package com.projteam.competico.api;

import static com.projteam.competico.domain.Account.PLAYER_ROLE;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import com.projteam.competico.domain.Account;
import com.projteam.competico.domain.TokenStatus;
import com.projteam.competico.service.AccountService;
import com.projteam.competico.service.game.GameService;
import com.projteam.competico.service.game.GameTaskDataService;
import com.projteam.competico.service.game.LobbyService;

@SpringBootTest
@ContextConfiguration(name = "API-tests")
@AutoConfigureMockMvc(addFilters = false)
class TemplateTests
{
	@Autowired
	private MockMvc mvc;

	private @MockBean AccountService accountService;
	private @MockBean LobbyService lobbyService;
	private @MockBean GameService gameService;
	private @MockBean GameTaskDataService gtdService;
	
	@Test
	public void shouldReturnHomePage() throws Exception
	{
		mvc.perform(get("/"))
			.andExpect(status().isOk());
	}
	@Test
	public void shouldReturnDashboardPage() throws Exception
	{
		mvc.perform(get("/dashboard"))
			.andExpect(status().isOk());
	}
	@Test
	public void shouldReturnProfilePage() throws Exception
	{
		mvc.perform(get("/profile"))
			.andExpect(status().isOk());
	}
	@Test
	public void shouldReturnProfilePageOfUser() throws Exception
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
		
		mvc.perform(get("/profile/" + username))
			.andExpect(status().isOk());
	}	
	@Test
	public void shouldReturnLobbyJoinPage() throws Exception
	{
		UUID id = UUID.randomUUID();
		String email = "testAcc@test.pl";
		String username = "TestAccount";
		String nickname = "TestAccount";
		String password = "QWERTY";
		List<String> roles = List.of(PLAYER_ROLE);
		
		when(accountService.getAuthenticatedAccount())
			.thenReturn(Optional.of(new Account.Builder()
					.withID(id)
					.withEmail(email)
					.withUsername(username)
					.withNickname(nickname)
					.withPassword(password)
					.withRoles(roles)
					.build()));
		
		mvc.perform(get("/lobby"))
			.andExpect(status().isOk());
	}
	@Test
	public void shouldRedirectWhenNotLoggedInOnLobbyJoinPage() throws Exception
	{
		when(accountService.getAuthenticatedAccount())
			.thenReturn(Optional.empty());
		
		mvc.perform(get("/lobby"))
			.andExpect(redirectedUrl("login"));
	}
	@Test
	public void shouldReturnGamePage() throws Exception
	{
		mvc.perform(get("/game/gameCode"))
			.andExpect(status().isOk());
	}
	@Test
	public void shouldReturnForgotPasswordPage() throws Exception
	{
		mvc.perform(get("/forgotpassword"))
			.andExpect(status().isOk());
	}
	@ParameterizedTest
	@EnumSource(TokenStatus.class)
	public void shouldReturnPasswordResetOrInvalidTokenPage(TokenStatus status) throws Exception
	{
		String token = UUID.randomUUID().toString();
		when(accountService.getPasswordResetTokenStatus(token))
			.thenReturn(status);
		
		mvc.perform(get("/resetpassword/" + token))
			.andExpect(status().isOk());
	}
	@ParameterizedTest
	@EnumSource(TokenStatus.class)
	public void shouldReturnEmailVerifiedOrInvalidTokenPage(TokenStatus status) throws Exception
	{
		String token = UUID.randomUUID().toString();
		when(accountService.getEmailVerificationTokenStatus(token))
			.thenReturn(status);
		
		mvc.perform(get("/verifyemail/" + token))
			.andExpect(status().isOk());
	}
	@Test
	public void shouldReturnTaskManagerForLecturer() throws Exception
	{
		mvc.perform(get("/lecturer/taskmanager"))
			.andExpect(status().isOk());
	}
}
