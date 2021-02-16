package com.projteam.app.api.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Controller
@Api(value = "ProfileAPI")
public class ProfileController
{
	@GetMapping("/profile")
	@ApiOperation(value = "Display profile information of the current user", code = 200)
	public String homePage()
	{
		return "profile";
	}
}