package com.projteam.app.api.web;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.projteam.app.domain.TokenStatus;
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
		return "register";
	}
	@GetMapping("login")
	@ApiOperation(value = "Display the login page", code = 200)
	public String loginPage()
	{
		if (accServ.isAuthenticated())
			return "redirect:/dashboard";
		return "login";
	}
	
	@GetMapping("/profile")
	@ApiOperation(value = "Display profile information of the current user", code = 200)
	public String profilePage()
	{
		return "profile";
	}

	@GetMapping("/profile/{username}")
	@ApiOperation(value = "Display profile information of the user with the provided username", code = 200)
	public Object profilePage(Model model, @PathVariable String username)
	{
        Map<String, Object> userData = accServ.findByUsername(username)
	        .map(acc -> Map.of(
	                "username", acc.getUsername(),
	                "nickname", acc.getNickname(),
	                "roles", acc.getRoles()))
	        .orElse(null);
        
		if (userData == null)
			return ResponseEntity.notFound().build();

		model.addAttribute("username", userData.get("username"));
		model.addAttribute("nickname", userData.get("nickname"));
		model.addAttribute("roles", userData.get("roles"));
		return "profile-other";
	}
	
	@GetMapping("/forgotpassword")
	@ApiOperation(value = "Display account recovery page", code = 200)
	public String forgotPassword()
	{
		return "forgotpassword";
	}
	@GetMapping("/resetpassword/{token}")
	@ApiOperation(value = "Display password reset page", code = 200)
	public String resetPassword(@PathVariable String token, Model model)
	{
		TokenStatus tokenStatus = accServ.getPasswordResetTokenStatus(token);
		if (tokenStatus == TokenStatus.VALID)
			return "resetpassword";
		else
		{
			model.addAttribute("status", tokenStatus.name());
			return "invalidresettoken";
		}
	}
	
	@GetMapping("/verifyemail/{token}")
	@ApiOperation(value = "Display email verification page", code = 200)
	public String verifyEmail(@PathVariable String token, Model model)
	{
		TokenStatus tokenStatus = accServ.getEmailVerificationTokenStatus(token);
		if (tokenStatus == TokenStatus.VALID)
		{
			try
			{
				accServ.verifyEmail(token);
				return "verifyemail";
			}
			catch (Exception e)
			{
				model.addAttribute("status", TokenStatus.INVALID);
				model.addAttribute("error", e.getMessage());
				return "invalidverificationtoken";
			}
		}
		else
		{
			model.addAttribute("status", tokenStatus.name());
			return "invalidverificationtoken";
		}
	}
}
