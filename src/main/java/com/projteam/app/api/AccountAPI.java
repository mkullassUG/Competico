package com.projteam.app.api;

import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import com.projteam.app.dto.LoginDTO;
import com.projteam.app.dto.RegistrationDTO;
import com.projteam.app.service.AccountService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@Api(value = "AccountAPI", tags = "The main API managing account access")
public class AccountAPI
{
	private AccountService accServ;
	
	private Logger log = LoggerFactory.getLogger(AccountAPI.class);
	
	@Autowired
	public AccountAPI(AccountService accServ)
	{
		this.accServ = accServ;
	}
	
	@PostMapping("api/v1/register")
	@ResponseStatus(HttpStatus.CREATED)
	@ApiOperation(value = "Register an account with the provided details", code = 201)
	@ApiResponses(
	{
		@ApiResponse(code = 201, message = "Account registered successfully"),
		@ApiResponse(code = 400, message = "Account could not be registered "
				+ "due to invalid or duplicate data")
	})
	public ResponseEntity<Object> registerAccount(HttpServletRequest req,
			@RequestBody @ApiParam(name = "Account data") RegistrationDTO regDto)
	{
		log.debug("Register request received: " + regDto);
		try
		{
			accServ.register(req, regDto);
			log.debug("Account registered successfully.");
			return new ResponseEntity<Object>(HttpStatus.CREATED);
		}
		catch (IllegalArgumentException e)
		{
			return ResponseEntity.badRequest().body("Konto z podanym adresem email już istnieje.");
		}
	}
	
	@PostMapping("api/v1/login")
	@ApiOperation(value = "Log in the user with the provided credentials", code = 200)
	@ApiResponses(
	{
		@ApiResponse(code = 200, message = "Logged in successfully"),
		@ApiResponse(code = 400, message = "Incorrect credentials")
	})
	public ResponseEntity<Object> loginAccount(HttpServletRequest req,
			@RequestBody @ApiParam(name = "User credentials") LoginDTO loginDto)
	{
		log.debug("Login request received: " + loginDto);
		if (accServ.login(req, loginDto))
			return new ResponseEntity<Object>(HttpStatus.OK);
		else
			return ResponseEntity.badRequest().body("Podane dane są niepoprawne.");
	}

	@GetMapping("register")
	@ApiOperation(value = "Display the registration page", code = 200)
	public Object registerPage()
	{
		if (accServ.isAuthenticated())
			return new ModelAndView("redirect:dashboard");
		return new ModelAndView("register");
	}
	@GetMapping("login")
	@ApiOperation(value = "Display the login page", code = 200)
	public ModelAndView loginPage()
	{
		if (accServ.isAuthenticated())
			return new ModelAndView("redirect:dashboard");
		return new ModelAndView("login");
	}
}
