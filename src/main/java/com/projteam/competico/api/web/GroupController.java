package com.projteam.competico.api.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.projteam.competico.service.group.GroupService;
import io.swagger.annotations.ApiOperation;

@Controller
public class GroupController
{
	private GroupService groupService;
	
	@Autowired
	public GroupController(GroupService groupService)
	{
		this.groupService = groupService;
	}
	
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
		boolean groupExists = groupService.groupExists(groupCode);
		boolean isMember = groupExists && groupService.groupContainsAccount(groupCode);
		if (isMember)
			return "group";
		else if (groupExists)
			return "group-access-denied";
		return "group-not-found";
	}
}
