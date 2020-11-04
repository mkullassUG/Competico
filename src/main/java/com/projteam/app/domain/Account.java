package com.projteam.app.domain;

import java.util.UUID;

public class Account
{
	private UUID id;
	private String email;
	private String username;
	private String passwordHash;
	
	public Account(String email, String username, String passwordHash)
	{
		this(null, email, username, passwordHash);
	}
	public Account(UUID id, String email, String username, String passwordHash)
	{
		this.id = id;
		this.email = email;
		this.username = username;
		this.passwordHash = passwordHash;
	}
	
	public UUID getId()
	{
		return id;
	}
	public String getEmail()
	{
		return email;
	}
	public String getUsername()
	{
		return username;
	}
	public String getPasswordHash()
	{
		return passwordHash;
	}
	
	public Account setId(UUID id)
	{
		return new Account(id, email, username, passwordHash);
	}
	public Account setEmail(String email)
	{
		return new Account(id, email, username, passwordHash);
	}
	public Account setUsername(String username)
	{
		return new Account(id, email, username, passwordHash);
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
		Account other = (Account) obj;
		if (email == null)
		{
			if (other.email != null)
				return false;
		}
		else if (!email.equals(other.email))
			return false;
		if (id == null)
		{
			if (other.id != null)
				return false;
		}
		else if (!id.equals(other.id))
			return false;
		if (passwordHash == null)
		{
			if (other.passwordHash != null)
				return false;
		}
		else if (!passwordHash.equals(other.passwordHash))
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
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((passwordHash == null) ? 0 : passwordHash.hashCode());
		result = prime * result + ((username == null) ? 0 : username.hashCode());
		return result;
	}
}
