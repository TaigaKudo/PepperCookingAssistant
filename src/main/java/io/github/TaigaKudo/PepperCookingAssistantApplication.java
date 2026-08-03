package io.github.TaigaKudo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class PepperCookingAssistantApplication {

	public static void main(String[] args) {
		System.out.println("working dir: " + System.getProperty("user.dir"));
		SpringApplication.run(PepperCookingAssistantApplication.class, args);
	}

}
