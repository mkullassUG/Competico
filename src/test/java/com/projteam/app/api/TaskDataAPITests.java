package com.projteam.app.api;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
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
import org.springframework.mock.web.MockMultipartFile;
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
	@MethodSource("mockTaskInfo")
	public void canGetImportedTaskInfo(List<Map<String, String>> taskInfo) throws Exception
	{
		when(gtdService.getImportedGlobalTaskInfo()).thenReturn(taskInfo);
		
		mvc.perform(get("/api/v1/tasks/imported/info"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$", hasSize(taskInfo.size())))
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
	public void canImportTask(List<TaskDTO> taskDTOs)
	{
		taskDTOs.forEach(task ->
		{
			try
			{
				JsonNode content = mapper.valueToTree(task);
				
				mvc.perform(post("/api/v1/tasks/imported")
						.contentType(APPLICATION_JSON_UTF8)
						.content(content.toString()))
					.andExpect(status().isOk());
				
				verify(gtdService, times(1)).importGlobalTask(content);
			}
			catch (Exception e)
			{
				fail(e);
			}
		});
	}
	@ParameterizedTest
	@MethodSource("mockTaskDTOs")
	public void shouldReturnBadRequestWhenCannotImportTask(List<TaskDTO> taskDTOs) throws Exception
	{
		taskDTOs.forEach(task ->
		{
			try
			{
				JsonNode content = mapper.valueToTree(task);
				String exceptionMessage = "Mock exception";
				doThrow(new IllegalArgumentException(exceptionMessage))
					.when(gtdService).importGlobalTask(content);
				
				mvc.perform(post("/api/v1/tasks/imported")
						.contentType(APPLICATION_JSON_UTF8)
						.content(content.toString()))
					.andExpect(status().isBadRequest())
					.andExpect(content().string(containsString(exceptionMessage)));
				
				verify(gtdService, times(1)).importGlobalTask(content);
			}
			catch (Exception e)
			{
				fail(e);
			}
		});
	}
	
	@ParameterizedTest
	@MethodSource("mockTaskDTOs")
	public void canImportTasksFromJson(List<TaskDTO> taskDTOs) throws Exception
	{
		JsonNode content = mapper.valueToTree(taskDTOs);
		MockMultipartFile file = new MockMultipartFile("file", "data.json",
				MediaType.TEXT_PLAIN_VALUE,
				content.toString().getBytes());
		
		mvc.perform(multipart("/api/v1/tasks/imported/json/file")
				.file(file))
			.andExpect(status().isOk());
		
		verify(gtdService, times(1)).importGlobalTasks(file);
	}
	@ParameterizedTest
	@MethodSource("mockTaskDTOs")
	public void shouldReturnBadRequestWhenCannotImportTasksFromJson(List<TaskDTO> taskDTOs) throws Exception
	{
		JsonNode content = mapper.valueToTree(taskDTOs);
		MockMultipartFile file = new MockMultipartFile("file", "data.json",
				MediaType.TEXT_PLAIN_VALUE,
				content.toString().getBytes());
		
		String exceptionMessage = "Mock exception";
		doThrow(new IllegalArgumentException(exceptionMessage))
			.when(gtdService).importGlobalTasks(file);
		
		mvc.perform(multipart("/api/v1/tasks/imported/json/file")
				.file(file))
			.andExpect(status().isBadRequest())
			.andExpect(content().string(containsString(exceptionMessage)));
		
		verify(gtdService, times(1)).importGlobalTasks(file);
	}
	
	@ParameterizedTest
	@MethodSource("mockTaskDTOs")
	public void canEditTask(List<TaskDTO> taskDTOs) throws Exception
	{
		UUID id = UUID.randomUUID();
		JsonNode content = mapper.valueToTree(taskDTOs);
		when(gtdService.editImportedGlobalTask(id, content)).thenReturn(true);
		
		mvc.perform(put("/api/v1/tasks/imported/" + id)
				.contentType(APPLICATION_JSON_UTF8)
				.content(content.toString()))
			.andExpect(status().isOk());
		
		verify(gtdService, times(1)).editImportedGlobalTask(id, content);
	}
	@ParameterizedTest
	@MethodSource("mockTaskDTOs")
	public void shouldReturnBadRequestWhenCannotEditTask(List<TaskDTO> taskDTOs) throws Exception
	{
		UUID id = UUID.randomUUID();
		JsonNode content = mapper.valueToTree(taskDTOs);
		when(gtdService.editImportedGlobalTask(id, content)).thenReturn(false);
		
		mvc.perform(put("/api/v1/tasks/imported/" + id)
				.contentType(APPLICATION_JSON_UTF8)
				.content(content.toString()))
			.andExpect(status().isBadRequest());
		
		verify(gtdService, times(1)).editImportedGlobalTask(id, content);
	}
	@ParameterizedTest
	@MethodSource("mockTaskDTOs")
	public void shouldReturnBadRequestWhenEditTaskThrows(List<TaskDTO> taskDTOs) throws Exception
	{
		UUID id = UUID.randomUUID();
		JsonNode content = mapper.valueToTree(taskDTOs);
		String exceptionMessage = "Mock exception";
		doThrow(new IllegalArgumentException(exceptionMessage))
			.when(gtdService).editImportedGlobalTask(id, content);
		
		mvc.perform(put("/api/v1/tasks/imported/" + id)
				.contentType(APPLICATION_JSON_UTF8)
				.content(content.toString()))
			.andExpect(status().isBadRequest())
			.andExpect(content().string(containsString(exceptionMessage)));
		
		verify(gtdService, times(1)).editImportedGlobalTask(id, content);
	}
	
	@ParameterizedTest
	@ValueSource(booleans = {true, false})
	public void canAttemptToDeleteImportedTask(boolean success) throws Exception
	{
		UUID id = UUID.randomUUID();
		when(gtdService.removeImportedGlobalTask(id)).thenReturn(success);
		mvc.perform(delete("/api/v1/tasks/imported/" + id))
			.andExpect(status().isOk())
			.andExpect(content().string("" + success));
		
		verify(gtdService, times(1)).removeImportedGlobalTask(id);
	}
	@Test
	public void canAttemptToDeleteAllImportedTasks() throws Exception
	{
		mvc.perform(delete("/api/v1/tasks/imported"))
			.andExpect(status().isOk());
		
		verify(gtdService, times(1)).removeAllImportedGlobalTasks();
	}
	
	@ParameterizedTest
	@MethodSource("mockTaskDTO")
	public void canGetTaskByID(TaskDTO taskDTO) throws Exception
	{
		UUID id = UUID.randomUUID();
		String taskName = "MockTask";
		when(gtdService.getImportedGlobalTask(id)).thenReturn(Optional.of(taskDTO));
		when(gtdService.getTaskDtoName(any())).thenReturn(taskName);
		mvc.perform(get("/api/v1/tasks/imported/" + id))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.taskName", is(taskName)))
			.andExpect(jsonPath("$.taskContent").exists());
		
		verify(gtdService, times(1)).getImportedGlobalTask(id);
	}
	@Test
	public void cannotGetTaskWithIncorrectID() throws Exception
	{
		UUID id = UUID.randomUUID();
		when(gtdService.getImportedGlobalTask(id)).thenReturn(Optional.empty());
		mvc.perform(get("/api/v1/tasks/imported/" + id))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.taskExists", is("false")));
		
		verify(gtdService, times(1)).getImportedGlobalTask(id);
	}
	
	//---Sources---
	
	public static List<Arguments> mockTaskDTOs()
	{
		return taskDTOs()
				.stream()
				.map(l -> Arguments.of(l))
				.collect(Collectors.toList());
	}
	public static List<Arguments> mockTaskDTO()
	{
		return taskDTOs()
				.stream()
				.map(l -> Arguments.of(l.get(0)))
				.collect(Collectors.toList());
	}
	public static List<Arguments> mockTaskInfo()
	{
		return taskDTOs()
				.stream()
				.map(l -> l.stream()
						.map(t -> Map.of(
								"taskID", UUID.randomUUID().toString(),
								"taskName", t.getClass().getName()))
						.collect(Collectors.toList()))
				.map(l -> Arguments.of(l))
				.collect(Collectors.toList());
	}
	
	//---Helpers---
	
	public static List<List<TaskDTO>> taskDTOs()
	{
		return List.of(
				List.of(new ChronologicalOrderDTO(
					"Test instruction",
					List.of("tag1", "Tag-2", "tag 3"),
					100,
					List.of(
						"Sen1",
						"Sen 2",
						"sentence 3"))
		));
	}
}
