package com.projteam.app.api;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import com.fasterxml.jackson.databind.JsonNode;
import com.projteam.app.service.game.GameTaskDataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(value = "TaskDataAPI", tags = "API managing access to task data")
public class TaskDataAPI
{
	private GameTaskDataService gtdService;
	
	@Autowired
	public TaskDataAPI(GameTaskDataService gtdService)
	{
		this.gtdService = gtdService;
	}
	
	@GetMapping("/api/v1/tasks/all/json")
	@ApiOperation(value = "Return a list of all tasks in JSON", code = 200)
	public JsonNode getTasksAsJson()
	{
		return gtdService.getAllTasksAsJson();
	}
	@GetMapping(value = "/api/v1/tasks/all/json/file",
			produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	@ApiOperation(value = "Return a file containing all tasks in JSON", code = 200)
	public Object getTasksAsJsonFile()
	{
		String filename = "tasks.json";
		byte[] ret = getTasksAsJson().toPrettyString().getBytes();
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
	public JsonNode getImportedTasksAsJson()
	{
		return gtdService.getImportedGlobalTasksAsJson();
	}
	@GetMapping(value = "/api/v1/tasks/imported/json/file",
			produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	@ApiOperation(value = "Return a file containing all imported tasks in JSON", code = 200)
	public Object getImportedTasksAsJsonFile()
	{
		String filename = "tasks.json";
		byte[] ret = getTasksAsJson().toPrettyString().getBytes();
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
	public ModelAndView gameHistory()
	{
		return new ModelAndView("taskImportList");
	}
}
