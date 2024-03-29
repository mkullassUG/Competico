package com.projteam.competico.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
import com.projteam.competico.domain.Account;
import com.projteam.competico.dto.lobby.LobbyOptionsDTO;
import com.projteam.competico.service.AccountService;
import com.projteam.competico.service.game.GameService;
import com.projteam.competico.service.game.LobbyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@Api(value = "LobbyAPI", tags = "API managing lobbies before the game starts")
public class LobbyAPI
{
	private LobbyService lobbyService;
	private GameService gameService;
	private AccountService accountService;
	
	@Autowired
	public LobbyAPI(LobbyService ls, GameService gs, AccountService as)
	{
		lobbyService = ls;
		gameService = gs;
		accountService = as;
	}
	
	@ResponseStatus(HttpStatus.CREATED)
	@ApiOperation(value = "Create a new lobby", code = 201)
	@ApiResponses(
	{
		@ApiResponse(code = 201, message = "Lobby created successfully"),
		@ApiResponse(code = 400, message = "Lobby could not be created")
	})
	@PostMapping("api/v1/lobby")
	public ResponseEntity<String> createLobby()
	{
		try
		{
			return new ResponseEntity<String>(lobbyService.createLobby(), HttpStatus.CREATED);
		}
		catch (Exception e)
		{
			return new ResponseEntity<String>("Nie udało się stworzyć lobby", HttpStatus.BAD_REQUEST);
		}
	}
	
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(value = "Join the lobby", code = 200)
	@ApiResponses(
	{
		@ApiResponse(code = 200, message = "Joined the lobby successfully"),
		@ApiResponse(code = 400, message = "Could not join lobby")
	})
	@PostMapping("api/v1/lobby/join/{gameCode}")
	public ResponseEntity<?> joinLobby(@PathVariable String gameCode)
	{
		boolean success = lobbyService.addPlayer(gameCode);
		if (success)
			return new ResponseEntity<>(HttpStatus.OK);
		if (lobbyService.lobbyExists(gameCode))
			return new ResponseEntity<>("Could not join lobby", HttpStatus.BAD_REQUEST);
		return new ResponseEntity<>("A lobby with this code does not exist", HttpStatus.BAD_REQUEST);
	}
	
	@ApiOperation(value = "Check lobby status", code = 200)
	@ApiResponses(
	{
		@ApiResponse(code = 200, message = "Current status of the lobby"),
	})
	@GetMapping("api/v1/lobby/{gameCode}")
	public Map<String, Object> lobbyStatus(@PathVariable String gameCode)
	{
		boolean exists = lobbyService.lobbyExists(gameCode);
		if (!exists)
			return Map.of("exists", exists);
		Account host = lobbyService.getHost(gameCode);
		Map<String, Object> ret = new HashMap<>();
		ret.putAll(Map.of(
				"exists", exists,
				"allowsRandomPlayers", lobbyService.allowsRandomPlayers(gameCode),
				"isFull", lobbyService.isLobbyFull(gameCode),
				"maxPlayers", lobbyService.getMaximumPlayerCount(gameCode),
				"host", Map.of(
						"username", host.getUsername(),
						"nickname", host.getNickname(),
						"roles", host.getRoles()),
				"players", lobbyService.getPlayers(gameCode)
					.stream()
					.map(acc -> Map.of(
							"username", acc.getUsername(),
							"nickname", acc.getNickname()
		))));
		if (lobbyService.isGroupLobby(gameCode))
		{
			ret.putAll(Map.of(
					"isGroupLobby", true,
					"groupCode", lobbyService.getGroupCode(gameCode).orElse(null)));
			List<String> tasksets = lobbyService.getTasksetNames(gameCode);
			if (tasksets != null)
				ret.put("tasksets", tasksets);
		}
		return ret;
	}
	
	@ApiOperation(value = "Retrieve information about the current player", code = 200)
	@ApiResponses(
	{
		@ApiResponse(code = 200, message = "Information about the player"),
	})
	@GetMapping("api/v1/playerinfo")
	public Object playerInfo()
	{
		try
		{
			Account player = getAuthenticatedAccount();
			return lobbyService.getLobbyForAccount(player)
					.map(gameCode -> Map.<String, Object>of(
							"username", player.getUsername(),
							"nickname", player.getNickname(),
							"isHost", lobbyService.isHost(gameCode, player),
							"gameStarted", false,
							"gameCode", gameCode))
					.or(() ->  gameService.getGameForAccount(player)
						.map(gameCode -> Map.<String, Object>of(
								"username", player.getUsername(),
								"nickname", player.getNickname(),
								"gameStarted", true,
								"gameCode", gameCode,
								"gameID", gameService.getGameID(gameCode))))
					.orElseGet(() -> Map.of(
							"username", player.getUsername(),
							"nickname", player.getNickname()));
		}
		catch (Exception e)
		{
			return new ResponseEntity<String>("Not authenticated", HttpStatus.BAD_REQUEST);
		}
	}
	
	@ApiOperation(value = "Check if lobby status changed since last request", code = 200)
	@ApiResponses(
	{
		@ApiResponse(code = 200, message = "Whether lobby status changed since last request"),
	})
	@GetMapping("api/v1/lobby/{gameCode}/changes")
	public Map<String, Object> lobbyStatusChanged(@PathVariable String gameCode)
	{
		Optional<Boolean> lobbyContentChanged = lobbyService.hasAnythingChanged(gameCode,
				getAuthenticatedAccount());
		boolean gameStarted = gameService.gameExists(gameCode);
		
		if (lobbyContentChanged.isEmpty() && !gameStarted)
			return Map.of("lobbyExists", false);
		if (gameStarted)
			return Map.of(
					"lobbyContentChanged", lobbyContentChanged.orElse(false),
					"gameStarted", true,
					"gameID", gameService.getGameID(gameCode));
		else
			return Map.of(
					"lobbyContentChanged", lobbyContentChanged.orElse(false),
					"gameStarted", false);
	}
	
	@ApiOperation(value = "Remove a player from the lobby", code = 200)
	@ApiResponses(
	{
		@ApiResponse(code = 200, message = "Whether the player was succesfully removed"),
	})
	@DeleteMapping("api/v1/lobby/{gameCode}/players")
	public boolean removePlayer(@PathVariable String gameCode, @RequestBody String username)
	{
		Account player = accountService.findByUsername(username).orElse(null);
		return lobbyService.removePlayer(gameCode, getAuthenticatedAccount(), player);
	}
	
	@ApiOperation(value = "Update lobby settings", code = 200)
	@ApiResponses(
	{
		@ApiResponse(code = 200, message = "Whether lobby settings were updated"),
	})
	@PutMapping("api/v1/lobby/{gameCode}")
	public boolean updateLobbySettings(
			@PathVariable String gameCode,
			@RequestBody LobbyOptionsDTO options)
	{
		return lobbyService.updateOptions(gameCode, options);
	}
	
	@ApiOperation(value = "Update selected tasksets in a group lobby", code = 200)
	@ApiResponses(
	{
		@ApiResponse(code = 200, message = "Whether lobby settings were updated"),
	})
	@PutMapping("api/v1/lobby/{gameCode}/tasksets")
	public ResponseEntity<?> updateGroupLobbySettings(
			@PathVariable String gameCode,
			@RequestBody List<String> tasksets)
	{
		try
		{
			lobbyService.setTasksets(gameCode, tasksets);
			return ResponseEntity.ok().build();
		}
		catch (Exception e)
		{
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@ApiOperation(value = "Leave lobby", code = 200)
	@ApiResponses(
	{
		@ApiResponse(code = 200, message = "Whether left lobby successfully"),
	})
	@PostMapping("api/v1/lobby/{gameCode}/leave")
	public boolean leaveLobby(@PathVariable String gameCode)
	{
		if (lobbyService.isHost(gameCode))
			return lobbyService.deleteLobby(gameCode);
		return lobbyService.removePlayer(gameCode);
	}
	
	@ApiOperation(value = "Find a random lobby", code = 200)
	@ApiResponses(
	{
		@ApiResponse(code = 200, message = "Lobby found"),
		@ApiResponse(code = 404, message = "No lobby available"),
	})
	@GetMapping("api/v1/lobby/random")
	public ResponseEntity<String> findRandomLobby()
	{
		Account acc = getAuthenticatedAccount();
		
		return Optional.ofNullable(lobbyService.getRandomLobby(acc))
			.map(gameCode -> new ResponseEntity<String>(gameCode, HttpStatus.OK))
			.orElseGet(() -> new ResponseEntity<String>("No lobby available", HttpStatus.NOT_FOUND));
	}
	
	private Account getAuthenticatedAccount()
	{
		return accountService.getAuthenticatedAccount()
				.orElseThrow(() -> new IllegalArgumentException("Not authenticated."));
	}
}