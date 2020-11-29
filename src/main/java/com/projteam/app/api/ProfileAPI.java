package com.projteam.app.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(value = "ProfileAPI")
public class ProfileAPI
{
	@GetMapping("/profile")
	@ApiOperation(value = "Display profile information of the current user", code = 200)
	public ModelAndView homePage()
	{
		return new ModelAndView("profile");
	}
}