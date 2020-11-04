package com.projteam.app.api;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/")
@RestController
public class HomeController
{
	private final Resource indexPageResource = new ClassPathResource("static/index.html");
	
	@RequestMapping("/")
	public ResponseEntity<Resource> homePage()
	{
		return ResponseEntity.ok(indexPageResource);
	}
}
