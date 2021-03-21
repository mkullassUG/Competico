package com.projteam.app.api.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import io.swagger.annotations.ApiOperation;

@Controller
public class TaskDataController
{
	@GetMapping("/tasks/import/global")
	@ApiOperation(value = "Display a list of task data templates for global import")
	public String taskImportList()
	{
		return "taskcreator";
	}
}
