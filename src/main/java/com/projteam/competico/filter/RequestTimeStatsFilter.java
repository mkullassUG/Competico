package com.projteam.competico.filter;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class RequestTimeStatsFilter extends GenericFilterBean
{
	@Value("${app.requestProcessing.minLogTime:2000}")
	private int MIN_LOG_TIME;
	
	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException
	{
		Instant start = Instant.now();
		try
		{
			chain.doFilter(req, res);
		}
		finally
		{
			long time = Duration.between(start, Instant.now()).toMillis();
			if (time > MIN_LOG_TIME)
			{
				if (req instanceof HttpServletRequest)
				{
					HttpServletRequest httpReq = (HttpServletRequest) req;
					log.warn("Request processing took {}ms:\t{} {}",
							time, httpReq.getRequestURI(), httpReq.getMethod());
				}
				else
					log.warn("Request processing took {}ms:\t{}",
							time, req);
			}
		}
	}
	
}
