package com.projteam.app.api.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Controller
@Api(value = "HomeAPI")
public class HomeController
{
	@GetMapping("/")
	@ApiOperation(value = "Display the home page of the application", code = 200)
	public String homePage()
	{
		return "index";
	}
}
