package com.projteam.app.api;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.projteam.app.service.AccountService;
import com.projteam.app.service.GameService;
import com.projteam.app.service.GameTaskDataService;
import com.projteam.app.service.LobbyService;

@SpringBootTest
@ContextConfiguration(name = "API-tests")
@AutoConfigureMockMvc(addFilters = false)
class TaskDataAPITests
{
	@Autowired
	private MockMvc mvc;
	
	private @MockBean AccountService accountService;
	private @MockBean LobbyService lobbyService;
	private @MockBean GameService gameService;
	private @MockBean GameTaskDataService gtdService;
	
	@Test
	public void canGetTasksAsJson() throws Exception
	{
		when(gtdService.getAllTasksAsJson()).thenReturn(
				JsonNodeFactory.instance.arrayNode());
		
		mvc.perform(get("/api/v1/tasks/all/json"))
			.andExpect(status().isOk());
	}
	@Test
	public void canGetTasksAsJsonFile() throws Exception
	{
		when(gtdService.getAllTasksAsJson()).thenReturn(
				JsonNodeFactory.instance.arrayNode());
		
		mvc.perform(get("/api/v1/tasks/all/json/file"))
			.andExpect(status().isOk());
	}
}
