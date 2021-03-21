package com.projteam.app.api.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import io.swagger.annotations.ApiOperation;

@Controller
public class LobbyController
{
	@GetMapping("/lobby")
	@ApiOperation(value = "Display the lobby join page.")
    public String lobbyJoin()
    {
        return "lobby-join";
    }
	@GetMapping("/game/{code}")
	@ApiOperation(value = "Display the lobby with the given game code.")
	public String lobbyPage(@PathVariable("code") String code)
	{
		return "lobby";
	}
}
