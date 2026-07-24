package io.github.TaigaKudo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {
	    DataSourceAutoConfiguration.class
	})
public class PepperCookingAssistantApplication {

	public static void main(String[] args) {
		SpringApplication.run(PepperCookingAssistantApplication.class, args);
	}

}
