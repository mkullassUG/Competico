package com.projteam.app.api;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.databind.JsonNode;
import com.projteam.app.service.GameService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@Api(value = "GameAPI", tags = "The main API managing games")
public class GameAPI
{
	private GameService gameService;
	
	@Autowired
	public GameAPI(GameService gs)
	{
		gameService = gs;
	}
	
	@ResponseStatus(HttpStatus.CREATED)
	@ApiOperation(value = "Create a new game", code = 201)
	@ApiResponses(
	{
		@ApiResponse(code = 201, message = "Game created successfully"),
		@ApiResponse(code = 400, message = "Game could not be created")
	})
	@PostMapping("api/v1/lobby/{gameCode}/start")
	public boolean createGame(@PathVariable String gameCode)
	{
		return gameService.createGameFromLobby(gameCode);
	}
	
	@ApiOperation(value = "Check game status", code = 200)
	@ApiResponses(
	{
		@ApiResponse(code = 200, message = "Current status of the game"),
	})
	@GetMapping("api/v1/game/{gameCode}")
	public Map<String, Object> gameStatus(@PathVariable String gameCode)
	{
		boolean exists = gameService.gameExists(gameCode);
		if (!exists)
			return Map.of("exists", exists);
		return Map.of("exists", exists);
	}
	
	@ApiOperation(value = "Get current task", code = 200)
	@ApiResponses(
	{
		@ApiResponse(code = 200, message = "Current task"),
	})
	@GetMapping("api/v1/game/{gameCode}/tasks/current")
	public Object getCurrentTask(@PathVariable String gameCode)
	{
		if (gameService.hasGameFinished(gameCode))
			return Map.of("hasGameFinished", true);
		return gameService.getCurrentTaskInfo(gameCode);
	}
	
	@ApiOperation(value = "Send answers to the current task", code = 200)
	@ApiResponses(
	{
		@ApiResponse(code = 200, message = "Answers received and graded"),
		@ApiResponse(code = 400, message = "Invalid answer content")
	})
	@PostMapping("api/v1/game/{gameCode}/tasks/answer")
	public ResponseEntity<Object> answer(@PathVariable String gameCode, @RequestBody JsonNode answer)
	{
		try
		{
			gameService.acceptAnswer(gameCode, answer);
		}
		catch (Exception e)
		{
			return ResponseEntity.badRequest().body("Niepoprawny format odpowiedzi na zadanie");
		}
		return ResponseEntity.ok().build();
	}
}
