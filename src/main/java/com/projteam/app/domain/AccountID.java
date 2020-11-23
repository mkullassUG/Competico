package com.projteam.app.domain;

import java.io.Serializable;
import java.util.UUID;
import javax.persistence.Embeddable;

@Embeddable
public class AccountID implements Serializable
{
	private UUID id;
	private String email;
	private String username;
	
	public AccountID()
	{}
	public AccountID(UUID id, String email, String username)
	{
		this.id = id;
		this.email = email;
		this.username = username;
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
	public void setId(UUID id)
	{
		this.id = id;
	}
	public void setEmail(String email)
	{
		this.email = email;
	}
	public void setUsername(String username)
	{
		this.username = username;
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((username == null) ? 0 : username.hashCode());
		return result;
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
		AccountID other = (AccountID) obj;
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
		if (username == null)
		{
			if (other.username != null)
				return false;
		}
		else if (!username.equals(other.username))
			return false;
		return true;
	}
}