package io.github.TaigaKudo.controller;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class DbCheckConfig {
	@Bean
	CommandLineRunner testConnectiion(JdbcTemplate jdbcTemplate) {
		return args -> {
			Integer result  = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
			System.out.println("PostgreSQL 疎通成功:"+result);
		};
	}
}