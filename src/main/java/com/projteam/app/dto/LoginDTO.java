package com.projteam.app.dto;

import java.util.Objects;
import org.springframework.lang.NonNull;
import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginDTO
{
	private @NonNull String email;
	private @NonNull CharSequence password;
	
	public LoginDTO(
			@JsonProperty("email") String email,
			@JsonProperty("password") String password)
	{
		Objects.requireNonNull(email);
		Objects.requireNonNull(password);
		
		this.email = email;
		this.password = password;
	}

	public String getEmail()
	{
		return email;
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
	public void setPassword(CharSequence password)
	{
		Objects.requireNonNull(password);
		this.password = password;
	}

	@Override
	public String toString()
	{
		return "[LoginDTO: email=" + email + ", password=(hidden)]";
	}
}
