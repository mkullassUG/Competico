//package com.projteam.app.config;
//
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.boot.jdbc.DataSourceBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.orm.jpa.JpaTransactionManager;
//import org.springframework.transaction.annotation.EnableTransactionManagement;
//import com.zaxxer.hikari.HikariDataSource;
//
//@Configuration
//@EnableTransactionManagement
//public class DatabaseConfig
//{
////	@Bean
////	@ConfigurationProperties("spring.datasource")
////	public HikariDataSource dataSource()
////	{
////		return DataSourceBuilder
////			.create()
////			.type(HikariDataSource.class)
////			.build();
////	}
//}