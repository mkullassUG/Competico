package com.projteam.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginDTO
{
	private String email;
	private CharSequence password;
	
	@Override
	public String toString()
	{
		return "[LoginDTO: email=" + email + ", password=(hidden)]";
	}
}
