package io.github.TaigaKudo.dto;

public record IngredientResponse(
		Long id,
		String name,
		String reading,
		String defaultUnit,
		Long categoryId,
		String categoryName,
		String categoryReading
		) {

}
