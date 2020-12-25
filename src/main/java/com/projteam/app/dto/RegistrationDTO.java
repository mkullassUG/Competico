package com.projteam.app.dto;

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
		this.email = email;
	}
	public void setUsername(String username)
	{
		this.username = username;
	}
	public void setPassword(CharSequence password)
	{
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
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RegistrationDTO other = (RegistrationDTO) obj;
		if (email == null)
		{
			if (other.email != null)
				return false;
		}
		else if (!email.equals(other.email))
			return false;
		if (isPlayer != other.isPlayer)
			return false;
		if (password == null)
		{
			if (other.password != null)
				return false;
		}
		else if (!password.equals(other.password))
			return false;
		if (username == null)
		{
			if (other.username != null)
				return false;
		}
		else if (!username.equals(other.username))
			return false;
		return true;
	}
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + (isPlayer ? 1231 : 1237);
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		result = prime * result + ((username == null) ? 0 : username.hashCode());
		return result;
	}

	@Override
	public String toString()
	{
		return "[RegistrationDTO: email=" + email + ", username=" + username + ", password=(hidden)]";
	}
}
