package com.projteam.app.api;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.projteam.app.dto.game.tasks.create.TaskDTO;
import com.projteam.app.service.game.GameTaskDataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(value = "TaskDataAPI", tags = "API managing access to task data")
public class TaskDataAPI
{
	private GameTaskDataService gtdService;
	
	private final ObjectMapper mapper = new ObjectMapper();
	
	@Autowired
	public TaskDataAPI(GameTaskDataService gtdService)
	{
		this.gtdService = gtdService;
	}
	
	@GetMapping("/api/v1/tasks/all/json")
	@ApiOperation(value = "Return a list of all tasks in JSON", code = 200)
	public List<Map<String, ?>> getTasks()
	{
		return taskDTOsWithName(gtdService.getAllTasks());
	}
	@GetMapping(value = "/api/v1/tasks/all/json/file",
			produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	@ApiOperation(value = "Return a file containing all tasks in JSON", code = 200)
	public Object getTasksAsJsonFile()
	{
		String filename = "tasks.json";
		byte[] ret = mapper.valueToTree(
				taskDTOsWithName(
						gtdService.getAllTasks()))
				.toPrettyString()
				.getBytes();
		return ResponseEntity.ok()
				.header("Content-Disposition",
						"attachment; filename=\"" + filename + "\"")
				.body(ret);
	}
	
	@GetMapping("/api/v1/tasks/imported/count")
	@ApiOperation(value = "Return the number imported tasks", code = 200)
	public int getImportedTaskCount()
	{
		return gtdService.getImportedGlobalTaskCount();
	}
	@GetMapping("/api/v1/tasks/imported/json")
	@ApiOperation(value = "Return a list of all imported tasks in JSON", code = 200)
	public List<Map<String, ?>> getImportedTasks()
	{
		return taskDTOsWithName(gtdService.getImportedGlobalTasks());
	}
	@GetMapping("/api/v1/tasks/imported/info")
	@ApiOperation(value = "Return a list containing the names and IDs of all imported tasks", code = 200)
	public List<Map<String, String>> getImportedTaskInfo()
	{
		return gtdService.getImportedGlobalTaskInfo();
	}
	@GetMapping(value = "/api/v1/tasks/imported/json/file",
			produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	@ApiOperation(value = "Return a file containing all imported tasks in JSON", code = 200)
	public Object getImportedTasksAsJsonFile()
	{
		String filename = "tasks.json";
		byte[] ret = mapper.valueToTree(
				taskDTOsWithName(
						gtdService.getImportedGlobalTasks()))
					.toPrettyString()
					.getBytes();
		return ResponseEntity.ok()
				.header("Content-Disposition",
						"attachment; filename=\"" + filename + "\"")
				.body(ret);
	}
	@PostMapping("/api/v1/tasks/imported")
	@ApiOperation(value = "Create a new task", code = 200)
	public ResponseEntity<?> importTask(@RequestBody JsonNode taskData)
	{
		try
		{
			gtdService.importGlobalTask(taskData);
			return ResponseEntity.ok().build();
		}
		catch (Exception e)
		{
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	@PostMapping(value = "/api/v1/tasks/imported/json/file",
			produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	@ApiOperation(value = "Return a file containing all imported tasks in JSON", code = 200)
	public ResponseEntity<?> importTasksAsJsonFile(@RequestParam("file") MultipartFile file)
	{
		try
		{
			gtdService.importGlobalTasks(file);
			return ResponseEntity.ok().build();
		}
		catch (Exception e)
		{
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	@GetMapping("/api/v1/tasks/imported/{id}")
	@ApiOperation(value = "Delete an inported task with the given id", code = 200)
	public Object getImportedTask(@PathVariable UUID id)
	{
		return gtdService.getImportedGlobalTask(id)
				.map(t -> taskDTOwithName(t))
				.map(t -> (Object) t)
				.orElse(Map.of("taskExists", "false"));
	}
	@PutMapping("/api/v1/tasks/imported/{id}")
	@ApiOperation(value = "Edit an inported task with the given id", code = 200)
	public ResponseEntity<?> editImportedTask(@PathVariable UUID id, @RequestBody JsonNode newTaskData)
	{
		try
		{
			if (gtdService.editImportedGlobalTask(id, newTaskData))
				return ResponseEntity.ok().build();
			return ResponseEntity.badRequest().body("ID does not match any imported task");
		}
		catch (Exception e)
		{
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	@DeleteMapping("/api/v1/tasks/imported/{id}")
	@ApiOperation(value = "Delete an inported task with the given id", code = 200)
	public boolean deleteImportedTask(@PathVariable UUID id)
	{
		return gtdService.removeImportedGlobalTask(id);
	}
	@DeleteMapping("/api/v1/tasks/imported")
	@ApiOperation(value = "Delete all inported tasks", code = 200)
	public void deleteImportedTasks()
	{
		gtdService.removeAllImportedGlobalTasks();
	}
	
	private List<Map<String, ?>> taskDTOsWithName(List<TaskDTO> dtoList)
	{
		return dtoList.stream()
			.map(t -> Map.of(
					"taskName", gtdService.getTaskDtoName(t),
					"taskContent", t))
			.collect(Collectors.toList());
	}
	private Map<String, ?> taskDTOwithName(TaskDTO dto)
	{
		return Map.of(
					"taskName", gtdService.getTaskDtoName(dto),
					"taskContent", dto);
	}
}
