package com.projteam.app.api;

import java.io.BufferedReader;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.projteam.app.dto.EmailChangeDTO;
import com.projteam.app.dto.LoginDTO;
import com.projteam.app.dto.PasswordChangeDTO;
import com.projteam.app.dto.RegistrationDTO;
import com.projteam.app.service.AccountService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@Api(value = "AccountAPI", tags = "The main API managing account access")
public class AccountAPI
{
	private AccountService accServ;
	
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
		@ApiResponse(code = 400, message = "Account could not be registered"
				+ " due to invalid or duplicate data")
	})
	public ResponseEntity<Object> registerAccount(HttpServletRequest req,
			@RequestBody @ApiParam(name = "Account data") RegistrationDTO regDto)
	{
		log.debug("Register request received: " + regDto);
		try
		{
			accServ.register(req, regDto, true);
			log.debug("Account registered successfully.");
			return new ResponseEntity<Object>(HttpStatus.CREATED);
		}
		catch (IllegalArgumentException e)
		{
			return ResponseEntity.badRequest().body(e.getMessage());
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
	
	@GetMapping("api/v1/authenticated")
	@ApiOperation(value = "Check if the user is currently authenticated", code = 200)
	@ApiResponses(
	{
		@ApiResponse(code = 200, message = "Whether the user is authenticated")
	})
	public boolean isAuthenticated()
	{
		return accServ.isAuthenticated();
	}
	
	@GetMapping("api/v1/account/info")
	@ApiOperation(value = "Get information about the current account", code = 200)
	@ApiResponses(
	{
		@ApiResponse(code = 200, message = "Information about the current account")
	})
	public Object getAccountInfo()
	{
		return accServ.getAuthenticatedAccount()
				.map(acc -> Map.of(
						"authenticated", true,
						"email", acc.getEmail(),
						"emailVerified", acc.isEmailVerified(),
						"username", acc.getUsername(),
						"nickname", acc.getNickname(),
						"roles", acc.getRoles()))
				.orElseGet(() -> Map.of("authenticated", false));
	}
	
	@GetMapping("api/v1/account/{username}/info")
	@ApiOperation(value = "Get information about an account", code = 200)
	@ApiResponses(
	{
		@ApiResponse(code = 200, message = "Information about the account with the provided username")
	})
	public Object getAccountInfo(@PathVariable String username)
	{
		return accServ.findByUsername(username)
				.map(acc -> Map.of(
						"username", acc.getUsername(),
						"nickname", acc.getNickname(),
						"roles", acc.getRoles()))
				.orElseGet(() -> Map.of("exists", false));
	}
	
	@PutMapping("api/v1/account/email")
	@ApiOperation(value = "Update email of the current account", code = 200)
	@ApiResponses(
	{
		@ApiResponse(code = 200, message = "Whether the email was updated")
	})
	public ResponseEntity<String> changeEmail(@RequestBody EmailChangeDTO eDto)
	{
		try
		{
			accServ.changeEmail(eDto.getEmail(), eDto.getPassword());
			return ResponseEntity.ok().build();
		}
		catch (Exception e)
		{
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	@PutMapping("api/v1/account/nickname")
	@ApiOperation(value = "Update nickname of the current account", code = 200)
	@ApiResponses(
	{
		@ApiResponse(code = 200, message = "Whether the email was updated")
	})
	public ResponseEntity<String> changeNickname(@RequestBody String newNickname)
	{
		try
		{
			accServ.changeNickname(newNickname);
			return ResponseEntity.ok().build();
		}
		catch (Exception e)
		{
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	@PutMapping("api/v1/account/password")
	@ApiOperation(value = "Update password of the current account", code = 200)
	@ApiResponses(
	{
		@ApiResponse(code = 200, message = "Whether the email was updated")
	})
	public ResponseEntity<String> changePassword(@RequestBody PasswordChangeDTO pcDto)
	{
		try
		{
			accServ.changePassword(pcDto.getOldPassword(), pcDto.getNewPassword());
			return ResponseEntity.ok().build();
		}
		catch (Exception e)
		{
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@PostMapping("api/v1/forgotpassword")
	@ApiOperation(value =
		"Request a password reset for the account with the specified username or email",
		code = 200)
	@ApiResponses(
	{
		@ApiResponse(code = 200, message = "Request accepted")
	})
	public ResponseEntity<String> requestPasswordReset(HttpServletRequest req)
	{
		try
		{
			accServ.requestPasswordReset(readString(req.getReader()));
			return ResponseEntity.ok().build();
		}
		catch (Exception e)
		{
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	@PostMapping("api/v1/resetpassword/{token}")
	@ApiOperation(value =
		"Reset password using a password reset token",
		code = 200)
	@ApiResponses(
	{
		@ApiResponse(code = 200, message = "Password set")
	})
	public ResponseEntity<String> resetPassword(
			@PathVariable String token, HttpServletRequest req)
	{
		try
		{
			accServ.resetPassword(token, readString(req.getReader()));
			return ResponseEntity.ok().build();
		}
		catch (Exception e)
		{
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@PostMapping("api/v1/emailverification")
	@ApiOperation(value =
		"Request email verification for the account with the specified username or email",
		code = 200)
	@ApiResponses(
	{
		@ApiResponse(code = 200, message = "Request accepted")
	})
	public ResponseEntity<String> requestEmailVerification()
	{
		try
		{
			accServ.requestEmailVerification();
			return ResponseEntity.ok().build();
		}
		catch (Exception e)
		{
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	private String readString(BufferedReader r)
	{
		return r.lines().collect(Collectors.joining());
	}
}
