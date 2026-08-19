package io.github.TaigaKudo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import io.github.TaigaKudo.dto.IngredientResponse;
import io.github.TaigaKudo.entity.Ingredient;
import io.github.TaigaKudo.entity.IngredientCategory;
import io.github.TaigaKudo.repository.IngredientRepository;

@Service
public class IngredientService {

	private final IngredientRepository ingredientRepository;
	
	public IngredientService(
			IngredientRepository ingredientRepository
			) {
		this.ingredientRepository = ingredientRepository;
	}
	
	public List<IngredientResponse> getIngredients() {
		List<Ingredient> ingredients = ingredientRepository.findAllByDeletedAtIsNull();
		
		List<IngredientResponse> response = ingredients
				.stream()
				.map(this::toResponse)
				.toList();
		
		return response;
	}
	
	private IngredientResponse toResponse(Ingredient ingredient) {
		IngredientCategory category = ingredient.getCategory();
		
		return new IngredientResponse(
				ingredient.getId(),
				ingredient.getName(),
				ingredient.getReading(),
				ingredient.getDefaultUnit(),
				category != null ? category.getId() : null,
				category != null ? category.getCategoryName() : null,
				category != null ? category.getReading() : null
				);
	}
}
