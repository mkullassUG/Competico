package com.projteam.competico.api.web;

import javax.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class ExceptionController
{
	@ResponseStatus(value=HttpStatus.METHOD_NOT_ALLOWED, reason="Method not allowed")
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public void handleMethodNotAllowed(HttpServletRequest req, Exception e)
	{
			log.warn("Request at '{}' rejected, method '{}' is not allowed.",
					req.getRequestURL(), req.getMethod());
	}
}
