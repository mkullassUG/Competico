package com.projteam.competico.config;

import java.nio.charset.StandardCharsets;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templatemode.TemplateMode;

@Configuration
public class TemplateConfig
{
	private SpringTemplateEngine templateEngine;

	@Autowired
	public TemplateConfig(SpringTemplateEngine templateEngine)
	{
		this.templateEngine = templateEngine;
	}
	
	@PostConstruct
	public void configureTemplateResolver()
	{
		var resolver = new SpringResourceTemplateResolver();
		resolver.setPrefix("classpath:/templates/");
		resolver.setSuffix(".html");
		resolver.setTemplateMode(TemplateMode.HTML);
		resolver.setCharacterEncoding(StandardCharsets.UTF_8.name());
		templateEngine.addTemplateResolver(resolver);
	}
}
