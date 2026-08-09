package io.github.TaigaKudo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HealthController {

	@ResponseBody
	@GetMapping("/health")
	public String GetHealth()
	{
		return "OK";
	}
}
