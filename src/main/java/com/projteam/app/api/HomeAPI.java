package com.projteam.app.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class HomeAPI
{
	@GetMapping("/")
	public ModelAndView homePage()
	{
		return new ModelAndView("index");
	}
}
