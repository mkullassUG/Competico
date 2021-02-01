package com.projteam.app.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
	
	@GetMapping("/tasks/import/global")
	@ApiOperation(value = "Display a list of task data templates for global import")
	public ModelAndView gameHistory()
	{
		return new ModelAndView("taskImportList");
	}
}
