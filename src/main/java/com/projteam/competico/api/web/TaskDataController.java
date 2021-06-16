package com.projteam.competico.api.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import io.swagger.annotations.ApiOperation;

@Controller
public class TaskDataController
{
	@GetMapping("lecturer/taskmanager")
    @ApiOperation(value = "Display Task Manager for lecturers")
    public String taskManager()
    {
        return "task-manager-lektor";
    }
}
