package com.projteam.app.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class TemplateTests
{
	@Autowired
	private MockMvc mvc;

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
}
