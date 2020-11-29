package com.projteam.app.dto;

import java.util.Objects;
import org.springframework.lang.NonNull;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RegistrationDTO
{
	private @NonNull String email;
	private @NonNull String username;
	private @NonNull CharSequence password;
	private boolean isPlayer;
	
	public RegistrationDTO(
			@JsonProperty("email") String email,
			@JsonProperty("username") String username,
			@JsonProperty("password") CharSequence password,
			@JsonProperty("isPlayer") boolean isPlayer)
	{
		Objects.requireNonNull(email);
		Objects.requireNonNull(username);
		Objects.requireNonNull(password);
		
		this.email = email;
		this.username = username;
		this.password = password;
		this.isPlayer = isPlayer;
	}
	
	public String getEmail()
	{
		return email;
	}
	public String getUsername()
	{
		return username;
	}
	public CharSequence getPassword()
	{
		return password;
	}
	public void setEmail(String email)
	{
		Objects.requireNonNull(email);
		this.email = email;
	}
	public void setUsername(String username)
	{
		Objects.requireNonNull(username);
		this.username = username;
	}
	public void setPassword(CharSequence password)
	{
		Objects.requireNonNull(password);
		this.password = password;
	}
	public boolean isPlayer()
	{
		return isPlayer;
	}
	public void setPlayer(boolean isPlayer)
	{
		this.isPlayer = isPlayer;
	}

	@Override
	public String toString()
	{
		return "[RegistrationDTO: email=" + email + ", username=" + username + ", password=(hidden)]";
	}
}
