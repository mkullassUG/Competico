package com.projteam.competico.api.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.projteam.competico.domain.Account;
import com.projteam.competico.service.AccountService;
import io.swagger.annotations.ApiOperation;

@Controller
public class LobbyController
{
	private AccountService accServ;
	
	@Autowired
	public LobbyController(AccountService accServ)
	{
		this.accServ = accServ;
	}
	
	@GetMapping("/lobby")
	@ApiOperation(value = "Display the lobby join page.")
    public String lobbyJoin(Model model)
    {
		Account acc = accServ.getAuthenticatedAccount().orElse(null);
		
		if ( acc == null)
			return "redirect:login";
		
		model.addAttribute("roles", acc.getRoles());
		model.addAttribute("authenticated", true);
		model.addAttribute("email", acc.getEmail());
		model.addAttribute("username", acc.getUsername());
		model.addAttribute("nickname", acc.getNickname());
		
        return "lobby-join";
    }
	@GetMapping("/game/{code}")
	@ApiOperation(value = "Display the lobby with the given game code.")
	public String lobbyPage(@PathVariable("code") String code)
	{
		return "lobby";
	}
}
