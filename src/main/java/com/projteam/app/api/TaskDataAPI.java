package com.projteam.app.api;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
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
	public void importTask(@RequestBody JsonNode task) throws ClassNotFoundException, IOException
	{
		gtdService.importGlobalTask(task);
	}
	
	@GetMapping("/tasks/import/global")
	@ApiOperation(value = "Display a list of task data templates for global import")
	public ModelAndView taskImportList()
	{
		return new ModelAndView("taskcreator");
	}
	
	private List<Map<String, ?>> taskDTOsWithName(List<TaskDTO> dtoList)
	{
		return dtoList.stream()
			.map(t -> Map.of(
					"taskName", gtdService.getTaskDtoName(t),
					"taskContent", t
					))
			.collect(Collectors.toList());
	}
}
