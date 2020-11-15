package com.projteam.app.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class DashboardAPI
{
	@GetMapping("dashboard")
	public ModelAndView registerPage()
	{
		return new ModelAndView("dashboard");
	}
}
