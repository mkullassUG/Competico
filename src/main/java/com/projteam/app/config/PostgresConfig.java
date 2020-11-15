//package com.projteam.app.config;
//
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.boot.jdbc.DataSourceBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import com.zaxxer.hikari.HikariDataSource;
//
//@Configuration
//public class PostgresConfig
//{
//	@Bean
//	@ConfigurationProperties("spring.datasource")
//	public HikariDataSource getDataSource()
//	{
//		return DataSourceBuilder.create()
//			.type(HikariDataSource.class)
//			.build();
//	}
//}
