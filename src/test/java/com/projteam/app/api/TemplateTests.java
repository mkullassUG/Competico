package com.projteam.app.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import com.projteam.app.service.AccountService;
import com.projteam.app.service.game.GameService;
import com.projteam.app.service.game.GameTaskDataService;
import com.projteam.app.service.game.LobbyService;

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
	public void shouldReturnLobbyJoinPage() throws Exception
	{
		mvc.perform(get("/lobby"))
			.andExpect(status().isOk());
	}
	@Test
	public void shouldReturnGamePage() throws Exception
	{
		mvc.perform(get("/game/gameCode"))
			.andExpect(status().isOk());
	}
	@Test
	public void shouldReturnTaskManagerPage() throws Exception
	{
		mvc.perform(get("/tasks/import/global"))
			.andExpect(status().isOk());
	}
}
