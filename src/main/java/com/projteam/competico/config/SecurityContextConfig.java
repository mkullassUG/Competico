package com.projteam.competico.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@Configuration
public class SecurityContextConfig
{
	public SecurityContext getContext()
	{
		return SecurityContextHolder.getContext();
	}
	public void clearContext()
	{
		 SecurityContextHolder.clearContext();
	}
}