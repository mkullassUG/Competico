package com.projteam.competico.api.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import io.swagger.annotations.ApiOperation;

@Controller
public class GroupController
{
	@GetMapping("/groups/{page}")
	@ApiOperation(value = "Display a list of groups")
	public String lobbyPage(@PathVariable int page)
	{
		return "group-list";
	}
	@GetMapping("/group/{groupCode}")
	@ApiOperation(value = "Display a group")
	public String gameHistory(@PathVariable String groupCode)
	{
		return "group";
	}
}
