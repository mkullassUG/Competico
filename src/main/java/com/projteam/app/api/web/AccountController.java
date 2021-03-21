package com.projteam.app.api.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import com.projteam.app.service.AccountService;
import io.swagger.annotations.ApiOperation;

@Controller
public class AccountController
{
	private AccountService accServ;
	
	@Autowired
	public AccountController(AccountService accServ)
	{
		this.accServ = accServ;
	}
	
	@GetMapping("register")
	@ApiOperation(value = "Display the registration page", code = 200)
	public String registerPage()
	{
		if (accServ.isAuthenticated())
			return "redirect:/dashboard";
		return "/register";
	}
	@GetMapping("login")
	@ApiOperation(value = "Display the login page", code = 200)
	public String loginPage()
	{
		if (accServ.isAuthenticated())
			return "redirect:/dashboard";
		return "/login";
	}
}
