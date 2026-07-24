package io.github.TaigaKudo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HealthController {

	@GetMapping("/health")
	public String GetHealth()
	{
		return "OK";
	}
}
