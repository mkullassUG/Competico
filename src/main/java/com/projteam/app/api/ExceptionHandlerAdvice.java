package com.projteam.app.api;

import org.springframework.http.ResponseEntity;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class ExceptionHandlerAdvice
{
	@ExceptionHandler(value =
	{
			RequestRejectedException.class,
			IllegalArgumentException.class,
			IllegalStateException.class
	})
	protected ResponseEntity<String> handleConflict(RuntimeException ex, WebRequest request)
	{
		return ResponseEntity.badRequest().body("Invalid request: " + ex.getMessage());
	}
}
