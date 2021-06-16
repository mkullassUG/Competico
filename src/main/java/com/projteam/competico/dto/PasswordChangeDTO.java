package com.projteam.competico.dto;

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
