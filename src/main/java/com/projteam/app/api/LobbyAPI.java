package com.projteam.app.api;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.projteam.app.dto.LoginDTO;
import com.projteam.app.service.AccountService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@Api(value = "LobbyJoinAPI")
public class LobbyAPI
{
	private AccountService accServ;
	
	private Logger log = LoggerFactory.getLogger(AccountAPI.class);
	
	
	@GetMapping("/lobby")
	@ApiOperation(value = "Display profile information of the current user", code = 200)
	public ModelAndView lobbyPage()
	{
		return new ModelAndView("lobby");
	}
	@GetMapping("/lobby-join")
	@ApiOperation(value = "Display profile information of the current user", code = 200)
	public ModelAndView lobbyJoinPage()
	{
		return new ModelAndView("lobby-join");
	}
	

	@PostMapping("game/connectionLoop")
	@ApiOperation(value = "Log in the user with the provided credentials", code = 200)
	@ApiResponses(
	{
		@ApiResponse(code = 200, message = "cos1"),
		@ApiResponse(code = 400, message = "cos2")
	})
	public ResponseEntity<Object> connectionCheck(HttpServletRequest req)
	{
		log.debug("connection request received");
			return new ResponseEntity<Object>(HttpStatus.OK);
	}
	
	@PostMapping("game/start")
	@ApiOperation(value = "Log in the user with the provided credentials", code = 200)
	@ApiResponses(
	{
		@ApiResponse(code = 200, message = "cos1"),
		@ApiResponse(code = 400, message = "cos2")
	})
	public ResponseEntity<Object> startGame(HttpServletRequest req)
	{
		log.debug("start request received");
			return new ResponseEntity<Object>(HttpStatus.OK);
	}
	
	@PostMapping("game/leave")
	@ApiOperation(value = "Log in the user with the provided credentials", code = 200)
	@ApiResponses(
	{
		@ApiResponse(code = 200, message = "cos1"),
		@ApiResponse(code = 400, message = "cos2")
	})
	public ResponseEntity<Object> leaveGame(HttpServletRequest req)
	{
		log.debug("leave request received");
			return new ResponseEntity<Object>(HttpStatus.OK);
	}
}