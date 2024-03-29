package com.projteam.competico.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.projteam.competico.SpringApp;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class ApiDocConfig
{
	@Bean
	public Docket configureDocket()
	{
		return new Docket(DocumentationType.SWAGGER_2)
				.select()
				.apis(RequestHandlerSelectors.basePackage(SpringApp.class.getPackageName()))
				.paths(PathSelectors.any())
				.build()
				.useDefaultResponseMessages(false)
				.apiInfo(new ApiInfoBuilder()
						.title("API Documentation")
						.description("A documentation of all currently present APIs.")
						.version("v1.0")
						.build());
	}
}