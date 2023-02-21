package com.projteam.competico.config;

import java.lang.reflect.Method;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableAsync
public class AsyncConfig implements AsyncUncaughtExceptionHandler
{
	@Override
	public void handleUncaughtException(Throwable ex, Method method, Object... params)
	{
		log.error("Uncaught async exception in {} - {}: {}",
				method,
				ex.getClass().getTypeName(),
				ex.getMessage());
	}
}
