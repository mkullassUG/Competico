package com.projteam.app.api;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GameAPI
{
	@GetMapping("game/{code}")
	public ResponseEntity<Resource> gamePage(@PathVariable("code") String code)
	{
		//TODO implement
		return new ResponseEntity<Resource>(HttpStatus.NOT_FOUND);
	}
}