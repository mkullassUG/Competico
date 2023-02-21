package com.projteam.competico.api;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;
import com.projteam.competico.service.game.LobbyService;
import com.projteam.competico.service.group.GroupService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@Api(value = "GroupAPI", tags = "Access to lecturer groups")
public class GroupAPI
{
	private GroupService groupService;
	private LobbyService lobbyService;
	
	@Autowired
	public GroupAPI(GroupService groupService,
			LobbyService lobbyService)
	{
		this.groupService = groupService;
		this.lobbyService = lobbyService;
	}
	
	@PostMapping("/api/v1/groups")
	@ApiOperation(value = "Create a new group", code = 200)
	public ResponseEntity<?> createGroup(@RequestBody Map<String, String> map)
	{
		try
		{
			String groupCode = groupService.createGroup(map.get("groupName"));
			return ResponseEntity.ok().body(groupCode);
		}
		catch (Exception e)
		{
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	@GetMapping("/api/v1/groups/{page}")
	@ApiOperation(value = "Retrieve a list of groups that the current user is a member of", code = 200)
	public ResponseEntity<?> getGroupList(@PathVariable int page)
	{
		try
		{
			return ResponseEntity.ok().body(groupService.getGroupList(page - 1));
		}
		catch (Exception e)
		{
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	@GetMapping("/api/v1/groups/names")
	@ApiOperation(value = "Retrieve a list of names of groups that the current user is a member of", code = 200)
	public ResponseEntity<?> getListOfGroupNames()
	{
		try
		{
			return ResponseEntity.ok().body(groupService.getGroupNameList());
		}
		catch (Exception e)
		{
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	@PutMapping("/api/v1/groups/{code}/name")
	@ApiOperation(value = "Change the name of a group", code = 200)
	public ResponseEntity<?> changeGroupName(
			@PathVariable("code") String groupCode,
			@RequestBody Map<String, String> map)
	{
		try
		{
			groupService.changeGroupName(groupCode, map.get("newGroupName"));
			return ResponseEntity.ok().build();
		}
		catch (Exception e)
		{
			switch (Optional
					.ofNullable(e.getMessage())
					.orElse(""))
			{
				case "GROUP_NOT_FOUND":
					return ResponseEntity.notFound().build();
				case "ACCESS_DENIED":
					return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
				default:
					return ResponseEntity.badRequest().body(e.getMessage());
			}
		}
	}
	@GetMapping("/api/v1/groups/{code}/info")
	@ApiOperation(value = "Get information about a group", code = 200)
	public Object getGroupInfo(@PathVariable("code") String groupCode)
	{
		try
		{
			return groupService.getGroupInfo(groupCode);
		}
		catch (Exception e)
		{
			switch (Optional
					.ofNullable(e.getMessage())
					.orElse(""))
			{
				case "GROUP_NOT_FOUND":
					return ResponseEntity.notFound().build();
				case "NOT_IN_GROUP":
					return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
				default:
					return ResponseEntity.badRequest().body(e.getMessage());
			}
		}
	}
	@DeleteMapping("/api/v1/groups/{code}")
	@ApiOperation(value = "Delete a group", code = 200)
	public ResponseEntity<?> deleteGroup(@PathVariable("code") String groupCode)
	{
		try
		{
			groupService.deleteGroup(groupCode);
			return ResponseEntity.ok().build();
		}
		catch (Exception e)
		{
			switch (Optional
					.ofNullable(e.getMessage())
					.orElse(""))
			{
				case "GROUP_NOT_FOUND":
					return ResponseEntity.notFound().build();
				case "ACCESS_DENIED":
					return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
				default:
					return ResponseEntity.badRequest().body(e.getMessage());
			}
		}
	}
	@PostMapping("/api/v1/groups/{code}/leave")
	@ApiOperation(value = "Leave a group", code = 200)
	public ResponseEntity<?> leaveGroup(@PathVariable("code") String groupCode)
	{
		try
		{
			groupService.leaveGroup(groupCode);
			return ResponseEntity.ok().build();
		}
		catch (Exception e)
		{
			switch (Optional
					.ofNullable(e.getMessage())
					.orElse(""))
			{
				case "GROUP_NOT_FOUND":
					return ResponseEntity.notFound().build();
				case "NOT_IN_GROUP":
					return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
				default:
					return ResponseEntity.badRequest().body(e.getMessage());
			}
		}
	}
	@DeleteMapping("/api/v1/groups/{code}/user/{username}")
	@ApiOperation(value = "Remove a user from a group", code = 200)
	public ResponseEntity<?> removeUserFromGroup(
			@PathVariable("code") String groupCode,
			@PathVariable("username") String username)
	{
		try
		{
			return ResponseEntity.ok().body(
					groupService.removeUserFromGroup(groupCode, username));
		}
		catch (Exception e)
		{
			switch (Optional
					.ofNullable(e.getMessage())
					.orElse(""))
			{
				case "GROUP_NOT_FOUND":
					return ResponseEntity.notFound().build();
				case "ACCESS_DENIED":
					return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
				default:
					return ResponseEntity.badRequest().body(e.getMessage());
			}
		}
	}
	
	@PostMapping("/api/v1/groups/join/{code}")
	@ApiOperation(value = "Send a request to join a group", code = 200)
	public ResponseEntity<?> requestToJoinGroup(
			@PathVariable("code") String groupCode)
	{
		try
		{
			groupService.requestToJoinGroup(groupCode);
			return ResponseEntity.ok().build();
		}
		catch (Exception e)
		{
			switch (Optional
					.ofNullable(e.getMessage())
					.orElse(""))
			{
				case "GROUP_NOT_FOUND":
					return ResponseEntity.notFound().build();
				default:
					return ResponseEntity.badRequest().body(e.getMessage());
			}
		}
	}
	@DeleteMapping("/api/v1/groups/join/{code}")
	@ApiOperation(value = "Delete a request to join a group", code = 200)
	public ResponseEntity<?> deleteRequestToJoinGroup(
			@PathVariable("code") String groupCode)
	{
		try
		{
			groupService.deleteRequestToJoinGroup(groupCode);
			return ResponseEntity.ok().build();
		}
		catch (Exception e)
		{
			switch (Optional
					.ofNullable(e.getMessage())
					.orElse(""))
			{
				case "GROUP_NOT_FOUND":
				case "REQUEST_NOT_FOUND":
					return ResponseEntity.notFound().build();
				default:
					return ResponseEntity.badRequest().body(e.getMessage());
			}
		}
	}
	@GetMapping("/api/v1/groups/{code}/requests")
	@ApiOperation(value = "Get requests to join a group", code = 200)
	public Object getGroupJoinRequests(@PathVariable("code") String groupCode)
	{
		try
		{
			return groupService.getGroupJoinRequests(groupCode);
		}
		catch (Exception e)
		{
			switch (Optional
					.ofNullable(e.getMessage())
					.orElse(""))
			{
				case "GROUP_NOT_FOUND":
					return ResponseEntity.notFound().build();
				case "NOT_IN_GROUP":
					return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
				default:
					return ResponseEntity.badRequest().body(e.getMessage());
			}
		}
	}
	@GetMapping("/api/v1/groups/requests/my")
	@ApiOperation(value = "Get personal requests to join a group", code = 200)
	public Object getOwnGroupJoinRequests()
	{
		try
		{
			return groupService.getOwnGroupJoinRequests();
		}
		catch (Exception e)
		{
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	@GetMapping("/api/v1/groups/requests/my/{page}")
	@ApiOperation(value = "Get personal requests to join a group, with paging", code = 200)
	public Object getOwnGroupJoinRequests(@PathVariable int page)
	{
		try
		{
			return groupService.getOwnGroupJoinRequests(page);
		}
		catch (Exception e)
		{
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	@GetMapping("/api/v1/groups/requests/all")
	@ApiOperation(value = "Get requests to join a group from all groups", code = 200)
	public Object getGroupJoinRequests()
	{
		try
		{
			return groupService.getGroupJoinRequests();
		}
		catch (Exception e)
		{
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	@GetMapping("/api/v1/groups/requests/all/{page}")
	@ApiOperation(value = "Get requests to join a group from all groups, with paging", code = 200)
	public Object getGroupJoinRequests(@PathVariable int page)
	{
		try
		{
			return groupService.getGroupJoinRequests(page - 1);
		}
		catch (Exception e)
		{
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	@PostMapping("/api/v1/groups/requests/{id}/respond")
	@ApiOperation(value = "Respond to a request to join a group", code = 200)
	public ResponseEntity<?> removeUserFromGroup(
			@PathVariable("id") UUID requestId,
			@RequestBody Map<String, String> map)
	{
		try
		{
			groupService.processGroupJoinRequest(requestId,
					Boolean.parseBoolean(map.get("accept")));
			return ResponseEntity.ok().build();
		}
		catch (Exception e)
		{
			switch (Optional
					.ofNullable(e.getMessage())
					.orElse(""))
			{
				case "REQUEST_NOT_FOUND":
					return ResponseEntity.notFound().build();
				case "ACCESS_DENIED":
					return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
				default:
					return ResponseEntity.badRequest().body(e.getMessage());
			}
		}
	}
	
	@PostMapping("/api/v1/groups/{code}/messages")
	@ApiOperation(value = "Post a new group message", code = 200)
	public ResponseEntity<?> postGroupMessage(
			@PathVariable("code") String groupCode,
			@RequestBody Map<String, String> map)
	{
		try
		{
			groupService.postGroupMessage(groupCode,
					map.get("title"),
					map.get("content"));
			return ResponseEntity.ok().build();
		}
		catch (Exception e)
		{
			switch (Optional
					.ofNullable(e.getMessage())
					.orElse(""))
			{
				case "GROUP_NOT_FOUND":
					return ResponseEntity.notFound().build();
				case "NOT_IN_GROUP":
					return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
				default:
					return ResponseEntity.badRequest().body(e.getMessage());
			}
		}
	}
	@GetMapping("/api/v1/groups/{code}/messages")
	@ApiOperation(value = "Get group messages", code = 200)
	public Object postGroupMessage(
			@PathVariable("code") String groupCode)
	{
		try
		{
			return groupService.getGroupMessages(groupCode);
		}
		catch (Exception e)
		{
			switch (Optional
					.ofNullable(e.getMessage())
					.orElse(""))
			{
				case "GROUP_NOT_FOUND":
					return ResponseEntity.notFound().build();
				case "NOT_IN_GROUP":
					return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
				default:
					return ResponseEntity.badRequest().body(e.getMessage());
			}
		}
	}
	@PostMapping("/api/v1/groups/message/{id}/read")
	@ApiOperation(value = "Change whether the current user has read a group message", code = 200)
	public ResponseEntity<?> setReadStatusOnGroupMessage(
			@PathVariable("id") UUID messageId,
			@RequestBody Map<String, String> map)
	{
		try
		{
			groupService.setReadStatusOnGroupMessage(messageId,
					Boolean.parseBoolean(map.get("messageRead")));
			return ResponseEntity.ok().build();
		}
		catch (Exception e)
		{
			switch (Optional
					.ofNullable(e.getMessage())
					.orElse(""))
			{
				case "MESSAGE_NOT_FOUND":
					return ResponseEntity.notFound().build();
				case "ACCESS_DENIED":
					return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
				default:
					return ResponseEntity.badRequest().body(e.getMessage());
			}
		}
	}
	@PutMapping("/api/v1/groups/message/{id}")
	@ApiOperation(value = "Edit a group message", code = 200)
	public ResponseEntity<?> editGroupMessage(
			@PathVariable("id") UUID messageId,
			@RequestBody Map<String, String> map)
	{
		try
		{
			groupService.editGroupMessage(messageId,
					map.get("title"),
					map.get("content"));
			return ResponseEntity.ok().build();
		}
		catch (Exception e)
		{
			switch (Optional
					.ofNullable(e.getMessage())
					.orElse(""))
			{
				case "MESSAGE_NOT_FOUND":
					return ResponseEntity.notFound().build();
				case "ACCESS_DENIED":
					return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
				default:
					return ResponseEntity.badRequest().body(e.getMessage());
			}
		}
	}
	
	@ApiOperation(value = "Get a list of history of the games played in a group", code = 200)
	@ApiResponses(
	{
		@ApiResponse(code = 200, message = "List retrieved successfully")
	})
	@GetMapping("api/v1/groups/{code}/game/history/{page}")
	public Object getGameHistory(
			@PathVariable("code") String groupCode,
			@PathVariable int page)
	{
		try
		{
			if (page < 1)
				return new RedirectView("api/v1/groups/{code}/game/history/1");
			return groupService.getGameHistory(groupCode, page - 1);
		}
		catch (Exception e)
		{
			switch (Optional
					.ofNullable(e.getMessage())
					.orElse(""))
			{
				case "GROUP_NOT_FOUND":
					return ResponseEntity.notFound().build();
				case "NOT_IN_GROUP":
					return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
				default:
					return ResponseEntity.badRequest().body(e.getMessage());
			}
		}
	}
	
	@ResponseStatus(HttpStatus.CREATED)
	@ApiOperation(value = "Create a new group lobby", code = 201)
	@ApiResponses(
	{
		@ApiResponse(code = 201, message = "Lobby created successfully"),
		@ApiResponse(code = 400, message = "Lobby could not be created")
	})
	@PostMapping("api/v1/groups/{code}/lobby")
	public ResponseEntity<String> createLobby(
			@PathVariable("code") String groupCode)
	{
		try
		{
			return new ResponseEntity<String>(
					lobbyService.createGroupLobby(groupCode), HttpStatus.CREATED);
		}
		catch (Exception e)
		{
			return ResponseEntity.badRequest().body("Nie udało się stworzyć lobby");
		}
	}
	
	@GetMapping("/api/v1/groups/joinrequests/count")
	@ApiOperation(value = "Get the number of group messages", code = 200)
	public Object getTotalJoinRequestCount()
	{
		try
		{
			return groupService.getTotalJoinRequestCount();
		}
		catch (Exception e)
		{

			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	@GetMapping("/api/v1/groups/messages/unread/count")
	@ApiOperation(value = "Get group messages", code = 200)
	public Object getTotalUnreadMessagesCount()
	{
		try
		{
			return groupService.getTotalUnreadMessagesCount();
		}
		catch (Exception e)
		{

			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	@GetMapping("/api/v1/groups/messages/unread")
	@ApiOperation(value = "Get unread group messages from all groups", code = 200)
	public Object getTotalUnreadMessages()
	{
		try
		{
			return groupService.getTotalUnreadMessages();
		}
		catch (Exception e)
		{

			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	@GetMapping("/api/v1/groups/{code}/lobbies")
	@ApiOperation(value = "Get a list of group lobbies from a group", code = 200)
	public Object getGroupLobbies(@PathVariable("code") String groupCode)
	{
		try
		{
			return groupService.getGroupLobbies(groupCode);
		}
		catch (Exception e)
		{

			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	@GetMapping("/api/v1/groups/lobbies")
	@ApiOperation(value = "Get a list all group lobbies", code = 200)
	public Object getAllGroupLobbies()
	{
		try
		{
			return groupService.getAllGroupLobbiesDto();
		}
		catch (Exception e)
		{

			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
}
