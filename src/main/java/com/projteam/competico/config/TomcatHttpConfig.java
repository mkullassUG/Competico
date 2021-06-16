package com.projteam.competico.config;

import org.apache.catalina.connector.Connector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TomcatHttpConfig
{
	private int httpPort;

	@Autowired
	public TomcatHttpConfig(@Value("${http.port:80}") int httpPort)
	{
		this.httpPort = httpPort;
	}

	@Bean
	public ServletWebServerFactory servletContainer()
	{
		TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
		tomcat.addAdditionalTomcatConnectors(createHttpConnector());
		return tomcat;
	}

	private Connector createHttpConnector()
	{
		Connector connector = new Connector();
		connector.setPort(httpPort);
		return connector;
	}
}
