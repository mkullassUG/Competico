package com.projteam.app.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(value = "DashboardAPI")
public class DashboardAPI
{
	@GetMapping("dashboard")
	@ApiOperation(value = "Display the dashboard of the current user", code = 200)
	public ModelAndView registerPage()
	{
		return new ModelAndView("dashboard");
	}
}
