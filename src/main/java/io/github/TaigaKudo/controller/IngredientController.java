package io.github.TaigaKudo.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.TaigaKudo.dto.IngredientResponse;
import io.github.TaigaKudo.service.IngredientService;

@RequestMapping("/ingredient")
@RestController
public class IngredientController {
	
	private final IngredientService ingredientService;
	
	public IngredientController(
			IngredientService ingredientService
			) {
		this.ingredientService = ingredientService;
	}

	@GetMapping
	public ResponseEntity<List<IngredientResponse>> getIngredient(){
		List<IngredientResponse> ingredients = ingredientService.getIngredients();
		return ResponseEntity
				.ok()
				.body(ingredients);
	}
}
