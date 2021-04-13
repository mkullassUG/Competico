package com.projteam.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordChangeDTO
{
	private CharSequence oldPassword;
	private CharSequence newPassword;
}
