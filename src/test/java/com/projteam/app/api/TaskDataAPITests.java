package com.projteam.app.api;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.nio.charset.Charset;
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
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.projteam.app.dto.game.tasks.create.TaskDTO;
import com.projteam.app.dto.game.tasks.create.ChronologicalOrderDTO;
import com.projteam.app.service.AccountService;
import com.projteam.app.service.game.GameService;
import com.projteam.app.service.game.GameTaskDataService;
import com.projteam.app.service.game.LobbyService;

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
	
	private final ObjectMapper mapper = new ObjectMapper();
	
	private static final MediaType APPLICATION_JSON_UTF8 =
			new MediaType(MediaType.APPLICATION_JSON.getType(),
					MediaType.APPLICATION_JSON.getSubtype(),
					Charset.forName("utf8"));
	
	@ParameterizedTest
	@MethodSource("mockTaskDTOs")
	public void canGetTasksAsJson(List<TaskDTO> taskDTOs) throws Exception
	{
		when(gtdService.getAllTasks()).thenReturn(taskDTOs);
		when(gtdService.getTaskDtoName(any())).thenReturn("MockTask");
		
		mvc.perform(get("/api/v1/tasks/all/json"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$", hasSize(taskDTOs.size())))
			.andExpect(jsonPath("$", not(hasItem(nullValue()))));
	}
	@ParameterizedTest
	@MethodSource("mockTaskDTOs")
	public void canGetTasksAsJsonFile(List<TaskDTO> taskDTOs) throws Exception
	{
		when(gtdService.getAllTasks()).thenReturn(taskDTOs);
		when(gtdService.getTaskDtoName(any())).thenReturn("MockTask");
		
		mvc.perform(get("/api/v1/tasks/all/json/file"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$", hasSize(taskDTOs.size())))
			.andExpect(jsonPath("$", not(hasItem(nullValue()))));
	}
	@ParameterizedTest
	@ValueSource(ints = {0, 1, 2, 15, 250, 9999})
	public void canGetImportedTaskCount(int taskCount) throws Exception
	{
		when(gtdService.getImportedGlobalTaskCount()).thenReturn(taskCount);
		when(gtdService.getTaskDtoName(any())).thenReturn("MockTask");
		
		mvc.perform(get("/api/v1/tasks/imported/count"))
			.andExpect(status().isOk())
			.andExpect(content().string("" + taskCount));
	}
	@ParameterizedTest
	@MethodSource("mockTaskDTOs")
	public void canGetImportedTasks(List<TaskDTO> taskDTOs) throws Exception
	{
		when(gtdService.getImportedGlobalTasks()).thenReturn(taskDTOs);
		when(gtdService.getTaskDtoName(any())).thenReturn("MockTask");
		
		mvc.perform(get("/api/v1/tasks/imported/json"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$", hasSize(taskDTOs.size())))
			.andExpect(jsonPath("$", not(hasItem(nullValue()))));
	}
	@ParameterizedTest
	@MethodSource("mockTaskDTOs")
	public void canGetImportedTasksAsJsonFile(List<TaskDTO> taskDTOs) throws Exception
	{
		when(gtdService.getImportedGlobalTasks()).thenReturn(taskDTOs);
		when(gtdService.getTaskDtoName(any())).thenReturn("MockTask");
		
		mvc.perform(get("/api/v1/tasks/imported/json/file"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$", hasSize(taskDTOs.size())))
			.andExpect(jsonPath("$", not(hasItem(nullValue()))));
	}
	
	@ParameterizedTest
	@MethodSource("mockTaskDTOs")
	public void canImportTask(List<TaskDTO> taskDTOs) throws Exception
	{
		JsonNode content = mapper.valueToTree(taskDTOs);
		
		mvc.perform(post("/api/v1/tasks/imported")
				.contentType(APPLICATION_JSON_UTF8)
				.content(content.toString()))
			.andExpect(status().isOk());
		
		verify(gtdService, times(1)).importGlobalTask(content);
	}
	
	@Test
	public void canGetTaskCreatorPage() throws Exception
	{
		mvc.perform(get("/tasks/import/global"))
			.andExpect(status().isOk());
	}
	
	//---Sources---
	
	public static List<Arguments> mockTaskDTOs()
	{
		return List.of(
				Arguments.of(
						List.of(new ChronologicalOrderDTO(
								"Test instruction",
								List.of("tag1", "Tag-2", "tag 3"),
								100,
								List.of(
									"Sen1",
									"Sen 2",
									"sentence 3"))
						)));
	}
}
