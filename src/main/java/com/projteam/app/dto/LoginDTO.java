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
			@JsonProperty("password") CharSequence password)
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
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LoginDTO other = (LoginDTO) obj;
		if (email == null)
		{
			if (other.email != null)
				return false;
		}
		else if (!email.equals(other.email))
			return false;
		if (password == null)
		{
			if (other.password != null)
				return false;
		}
		else if (!password.equals(other.password))
			return false;
		return true;
	}
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		return result;
	}

	@Override
	public String toString()
	{
		return "[LoginDTO: email=" + email + ", password=(hidden)]";
	}
}
