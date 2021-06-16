package com.projteam.competico.config;

import java.net.URI;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.trace.http.HttpTrace;
import org.springframework.boot.actuate.trace.http.HttpTraceRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class HttpTraceConfig
{
	private static final int SIZE_LIMIT = 200;
	@Value("${app.trace.minLogTime:5000}")
	private int MIN_LOG_TIME;
	
	@Bean
	public HttpTraceRepository getHttpTraceRepository()
	{
		return new HttpTraceRepository()
		{
			private Deque<HttpTrace> traces = new LinkedList<>();
			private Set<String> traceIDs = new HashSet<>();
			
			@Override
			public synchronized List<HttpTrace> findAll()
			{
				return new ArrayList<>(traces);
			}

			@Override
			public synchronized void add(HttpTrace trace)
			{
				if (trace.getTimeTaken() > MIN_LOG_TIME)
					log.warn("Request took {}ms:\t{} {} - returned {}",
							trace.getTimeTaken(),
							trace.getRequest().getUri(),
							trace.getRequest().getMethod(),
							trace.getResponse().getStatus());
				
				String traceId = compress(trace);
				if (isHotPath(trace) && traceIDs.contains(traceId))
					return;
				
				if (traces.size() > SIZE_LIMIT)
					traceIDs.remove(compress(traces.removeFirst()));
				traces.add(trace);
				traceIDs.add(traceId);
			}

			private boolean isHotPath(HttpTrace trace)
			{
				String path = trace.getRequest()
						.getUri()
						.getPath()
						.toLowerCase();
				return path.startsWith("/api/v1/game")
						|| path.startsWith("api/v1/game")
						|| path.startsWith("/api/v1/lobby")
						|| path.startsWith("api/v1/lobby")
						|| path.startsWith("/api/v1/group")
						|| path.startsWith("api/v1/group");
			}
			private String compress(HttpTrace trace)
			{
				URI uri = trace.getRequest().getUri();
				return uri.getHost() + ":" + uri.getPort() + uri.getRawPath();
			}
		};
	}
}
