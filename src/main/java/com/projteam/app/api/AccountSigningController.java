package com.projteam.app.api;

import java.util.ArrayList;
import java.util.HashMap;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.projteam.app.service.AccountService;

@RestController
public class AccountSigningController
{
	private AccountService accServ;
	
	//Could also use @Value("${:classpath:/static/XXX.html}")
	private final Resource registerPageResource = new ClassPathResource("static/register.html");
	private final Resource loginPageResource = new ClassPathResource("static/login.html");
	
	@Autowired
	public AccountSigningController(AccountService accServ)
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
	public ResponseEntity<Object> registerAccount(@RequestBody HashMap<String, String> json)
	{
		System.out.println("Register request received: " + json);
		String email = json.get("email");
		String username = json.get("username");
		String password = json.get("password");
		try
		{
			accServ.register(email, username, password);
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
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
	
	@PostMapping("api/v1/login")
	public ResponseEntity<Object> loginAccount(@RequestBody HashMap<String, String> json)
	{
		System.out.println("Login request received: " + json);
		String email = json.get("email");
		String password = json.get("password");
		if (accServ.login(email, password))
			return new ResponseEntity<Object>(HttpStatus.OK);
		else
			return ResponseEntity.badRequest().body("Podane dane są niepoprawne.");
	}
	
	@PostMapping("api/v1/logout")
	public ResponseEntity<Object> logoutAccount()
	{
		accServ.logout();
		return new ResponseEntity<Object>(HttpStatus.OK);
	}
	
	@RequestMapping("register")
	public ResponseEntity<Resource> registerPage()
	{
		return ResponseEntity.ok(registerPageResource);
	}
	@RequestMapping("login")
	public ResponseEntity<Resource> loginPage()
	{
		return ResponseEntity.ok(loginPageResource);
	}
}
