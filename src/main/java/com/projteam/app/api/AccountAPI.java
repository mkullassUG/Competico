package com.projteam.app.api;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import com.projteam.app.dto.LoginDTO;
import com.projteam.app.dto.RegistrationDTO;
import com.projteam.app.service.AccountService;

@RestController
public class AccountAPI
{
	private AccountService accServ;
	
//	//Assignment equivalent to @Value("${:classpath:/static/XXX.html}")
//	private final Resource registerPageResource = new ClassPathResource("static/register.html");
//	private final Resource loginPageResource = new ClassPathResource("static/login.html");
	
	@Autowired
	public AccountAPI(AccountService accServ)
	{
		this.accServ = accServ;
	}
	
	/*
	 curl 
	 -X POST 
	 -H "Content-Type: application/json" 
	 -d '{"email":"test@tset.pl", "username":"Test", "password":"pass"}'
	 localhost/api/v1/register
	 */
	
	@PostMapping("api/v1/register")
	public ResponseEntity<Object> registerAccount(HttpServletRequest req, @RequestBody RegistrationDTO regDto)
	{
		System.out.println("Register request received: " + regDto);
		try
		{
			accServ.register(req, regDto);
			System.out.println("Account registered successfully.");
			return new ResponseEntity<Object>(HttpStatus.CREATED);
		}
		catch (IllegalArgumentException e)
		{
			return ResponseEntity.badRequest().body("Konto z podanym adresem email już istnieje.");
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return new ResponseEntity<>("An unknown error occurred during account registration.",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping("api/v1/login")
	public ResponseEntity<Object> loginAccount(HttpServletRequest req, @RequestBody LoginDTO loginDto)
	{
		System.out.println("Login request received: " + loginDto);
		if (accServ.login(req, loginDto))
			return new ResponseEntity<Object>(HttpStatus.OK);
		else
			return ResponseEntity.badRequest().body("Podane dane są niepoprawne.");
	}

	@GetMapping("register")
	public ModelAndView registerPage()
	{
		return new ModelAndView("register");
	}
	@GetMapping("login")
	public ModelAndView loginPage()
	{
		return new ModelAndView("login");
	}
}
