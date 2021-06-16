package com.projteam.competico.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationDTO
{
	private String email;
	private String username;
	private CharSequence password;
	private boolean isPlayer;
	
	public boolean isPlayer()
	{
		return isPlayer;
	}
	public void setPlayer(boolean isPlayer)
	{
		this.isPlayer = isPlayer;
	}
	public void setIsPlayer(boolean isPlayer)
	{
		this.isPlayer = isPlayer;
	}
	
	@Override
	public String toString()
	{
		return "RegistrationDTO [email=" + email + ", username=" + username + ", password=[hidden]" + ", isPlayer=" + isPlayer + "]";
	}
}
