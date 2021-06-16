package com.projteam.competico.api.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Controller
@Api(value = "DashboardAPI")
public class DashboardController
{
	@GetMapping("dashboard")
	@ApiOperation(value = "Display the dashboard of the current user", code = 200)
	public String registerPage()
	{
		return "dashboard";
	}
}
