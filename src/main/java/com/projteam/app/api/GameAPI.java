package com.projteam.app.api;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.projteam.app.domain.game.tasks.answers.WordFillAnswer;
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
	public Map<String, Object> getCurrentTask(@PathVariable String gameCode)
	{
		//TODO implement properly
		List<String> text = List.of("Lorem ipsum dolor sit amet, consectetur ",
				" elit. Quisque vestibulum, enim id fringilla sodales, libero   ipsum ",
				" erat, id ullamcorper elit ante auctor est. Nulla facilisi. Maecenas ultricies, magna non pretium mattis, ligula risus pulvinar elit, eu mattis ",
				" dolor nec turpis. Quisque elementum ",
				" accumsan. Lorem ipsum dolor ",
				" amet, consectetur adipiscing elit. In nec ",
				" nisi, et semper nisl. Cras placerat ",
				" orci eget congue. Duis vitae gravida odio. Etiam elit turpis, ",
				" ac nisi et, dapibus blandit nibh. Duis eleifend metus in iaculis tincidunt.");
		List<String> possibleAnswers = List.of("slowo1", "slowo2",
				"slowo3", "slowo4", "slowo5",
				"slowo6", "slowo7", "slowo8");
		
		if (gameService.hasGameFinished(gameCode))
			return Map.of("hasGameFinished", true);
		return Map.of("taskNumber", 0,
				"taskName", "WordFill",
				"text", text,
				"possibleAnswers", possibleAnswers,
				"startsWithText", true,
				"emptySpaceCount", text.size() - 1);
	}
	
	@ApiOperation(value = "Send answers to the current task", code = 200)
	@ApiResponses(
	{
		@ApiResponse(code = 200, message = "Answers received and graded"),
	})
	@PostMapping("api/v1/game/{gameCode}/tasks/answer")
	public void answer(@PathVariable String gameCode, @RequestBody List<String> answer)
	{
		gameService.acceptAnswer(gameCode, new WordFillAnswer(answer));
	}
}
