package com.projteam.app.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.annotations.ApiOperation;

@RestController
public class HomeAPI
{
	@GetMapping("/")
	@ApiOperation(value = "Display the home page of the application", code = 200)
	public ModelAndView homePage()
	{
		return new ModelAndView("index");
	}
}
