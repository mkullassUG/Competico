package com.projteam.competico.api;

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
import com.projteam.competico.dto.game.TasksetChangeDTO;
import com.projteam.competico.dto.game.TasksetNameDTO;
import com.projteam.competico.dto.game.tasks.create.TaskDTO;
import com.projteam.competico.service.game.GameTaskDataService;
import com.projteam.competico.service.game.TaskSetDataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(value = "TaskDataAPI", tags = "Task data access")
public class TaskDataAPI
{
	private GameTaskDataService gtdService;
	private TaskSetDataService tsdService;
	
	private final ObjectMapper mapper = new ObjectMapper();
	
	@Autowired
	public TaskDataAPI(GameTaskDataService gtdService,
			TaskSetDataService tsdService)
	{
		this.gtdService = gtdService;
		this.tsdService = tsdService;
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
	@ApiOperation(value = "Return the number of imported tasks", code = 200)
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
	public ResponseEntity<?> importGlobalTask(@RequestBody JsonNode taskData)
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
	
	@PostMapping(value = "/api/v1/tasks/convert",
			produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	@ApiOperation(value = "Convert a file containing tasks in DTO format to Entity format", code = 200)
	public Object convertDtoFileToEntityFile(@RequestParam("file") MultipartFile file)
	{
		try
		{
			String filename = "tasks.json";
			byte[] ret = gtdService.convertDtoToEntity(file)
						.toPrettyString()
						.getBytes();
			return ResponseEntity.ok()
					.header("Content-Disposition",
							"attachment; filename=\"" + filename + "\"")
					.body(ret);
		}
		catch (Exception e)
		{
			return ResponseEntity.badRequest().body(
					e.getClass().getTypeName()
					+ ": " + e.getMessage());
		}
	}
	
	@GetMapping("/api/v1/tasksets/info")
	@ApiOperation(value = "Return a map containing information about all tasks for all tasksets", code = 200)
	public Map<String, List<Map<String, String>>> getTaskInfo()
	{
		return tsdService.getTaskInfo();
	}
	@GetMapping("/api/v1/tasksets/names")
	@ApiOperation(value = "Return a list containing the names of all tasksets", code = 200)
	public List<String> getTasksetInfo()
	{
		return tsdService.getTasksetInfo();
	}
	@GetMapping("/api/v1/taskset/info")
	@ApiOperation(value = "Return a list containing the names and IDs of tasks from given taskset", code = 200)
	public List<Map<String, String>> getTaskInfo(@RequestBody TasksetNameDTO tnDto)
	{
		return tsdService.getTaskInfo(tnDto.getTasksetName());
	}
	@GetMapping(value = "/api/v1/tasksets/json/file",
			produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	@ApiOperation(value = "Return a file containing all imported tasks in JSON", code = 200)
	public Object getTasksetsAsJsonFile(@RequestParam String tasksetName)
	{
		String filename = "tasks.json";
		byte[] ret = mapper.valueToTree(
				Map.<String, Object>of(
					"tasksetName", tasksetName,
					"tasksetContent", taskDTOsWithName(
							tsdService.getTasks(tasksetName))))
				.toPrettyString()
				.getBytes();
		return ResponseEntity.ok()
				.header("Content-Disposition",
						"attachment; filename=\"" + filename + "\"")
				.body(ret);
	}
	@GetMapping(value = "/api/v1/tasksets/all/json/file",
			produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	@ApiOperation(value = "Return a file containing all imported tasks from all tasksets in JSON",
			code = 200)
	public Object getAllTasksetsAsJsonFile()
	{
		String filename = "tasks.json";
		byte[] ret = mapper.valueToTree(tsdService.getAllTasks()
					.entrySet()
					.stream()
					.map(en -> Map.<String, Object>of(
							"tasksetName", en.getKey(),
							"tasksetContent", taskDTOsWithName(en.getValue())))
					.collect(Collectors.toList()))
				.toPrettyString()
				.getBytes();
		return ResponseEntity.ok()
				.header("Content-Disposition",
						"attachment; filename=\"" + filename + "\"")
				.body(ret);
	}
	@PostMapping("/api/v1/taskset")
	@ApiOperation(value = "Create a new task", code = 200)
	public ResponseEntity<?> createTaskset(@RequestBody TasksetNameDTO tnDto)
	{
		try
		{
			tsdService.createTaskset(tnDto.getTasksetName());
			return ResponseEntity.ok().build();
		}
		catch (Exception e)
		{
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	@PutMapping("/api/v1/taskset")
	@ApiOperation(value = "Create a new task", code = 200)
	public ResponseEntity<?> changeTasksetName(@RequestBody TasksetChangeDTO tcDto)
	{
		try
		{
			if (tsdService.changeTasksetName(tcDto.getTasksetName(),
					tcDto.getNewTasksetName()))
				return ResponseEntity.ok().build();
			else
				return ResponseEntity.notFound().build();
		}
		catch (Exception e)
		{
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	@DeleteMapping("/api/v1/taskset")
	@ApiOperation(value = "Create a new task", code = 200)
	public ResponseEntity<?> deleteTaskset(@RequestBody TasksetNameDTO tnDto)
	{
		try
		{
			tsdService.removeTaskset(tnDto.getTasksetName());
			return ResponseEntity.ok().build();
		}
		catch (Exception e)
		{
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	@PostMapping("/api/v1/tasksets/task")
	@ApiOperation(value = "Create a new task", code = 200)
	public ResponseEntity<?> importTask(@RequestBody JsonNode taskSetData)
	{
		try
		{
			List<UUID> ret = tsdService.importTask(taskSetData);
			return ResponseEntity.ok().body(ret);
		}
		catch (Exception e)
		{
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	@PostMapping(value = "/api/v1/tasksets/json/file",
			produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	@ApiOperation(value = "Return a file containing all imported tasks in JSON", code = 200)
	public ResponseEntity<?> importTasksetsAsJsonFile(@RequestParam("file") MultipartFile file)
	{
		try
		{
			tsdService.importTasks(file);
			return ResponseEntity.ok().build();
		}
		catch (Exception e)
		{
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	@PostMapping(value = "/api/v1/tasksets/json/file/withname",
			produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	@ApiOperation(value = "Return a file containing all imported tasks in JSON", code = 200)
	public ResponseEntity<?> importToTasksetAsJsonFile(
			@RequestParam("file") MultipartFile file,
			@RequestParam("json") String tasksetName)
	{
		try
		{
			tsdService.importTasksToTaskset(file, tasksetName);
			return ResponseEntity.ok().build();
		}
		catch (Exception e)
		{
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	@GetMapping("/api/v1/task/{id}")
	@ApiOperation(value = "Get task with given id from given taskset", code = 200)
	public Object getTask(@PathVariable UUID id)
	{
		return tsdService.getTask(id)
				.map(t -> taskDTOwithName(t))
				.map(t -> (Object) t)
				.orElse(Map.of("taskExists", "false"));
	}
	@PutMapping("/api/v1/task/{id}")
	@ApiOperation(value = "Edit task with given id from given taskset", code = 200)
	public ResponseEntity<?> editTask(@PathVariable UUID id, @RequestBody JsonNode newTaskData)
	{
		try
		{
			if (tsdService.editTask(id, newTaskData))
				return ResponseEntity.ok().build();
			return ResponseEntity.badRequest().body("ID does not match any imported task");
		}
		catch (Exception e)
		{
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	@DeleteMapping("/api/v1/task/{id}")
	@ApiOperation(value = "Delete an inported task with the given id", code = 200)
	public boolean deleteTask(@PathVariable UUID id)
	{
		return tsdService.removeTask(id);
	}
	@DeleteMapping("/api/v1/tasksets/all/tasks")
	@ApiOperation(value = "Delete all inported tasks", code = 200)
	public void deleteAllTasks()
	{
		tsdService.removeAllTasks();
	}
	@DeleteMapping("/api/v1/tasksets/tasks")
	@ApiOperation(value = "Delete all inported tasks", code = 200)
	public void deleteAllTasksFromTaskset(@RequestBody TasksetNameDTO tnDto)
	{
		tsdService.removeAllTasksFromTaskset(tnDto.getTasksetName());
	}
	@PutMapping("/api/v1/task/{id}/move")
	@ApiOperation(value = "Delete all inported tasks", code = 200)
	public void moveTask(@PathVariable UUID id, @RequestBody TasksetNameDTO tnDto)
	{
		tsdService.moveTask(id, tnDto.getTasksetName());
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
