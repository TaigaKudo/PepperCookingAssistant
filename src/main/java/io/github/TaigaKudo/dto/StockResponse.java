package io.github.TaigaKudo.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record StockResponse(
		Long stockId,
		Long ingredientId,
		String ingredientName,
		String ingredientReading,
		String defaultUnit,
		Long categoryId,
		String categoryName,
		String categoryReading,
		Long userId,
		BigDecimal quantity,
		LocalDate expirationDate
		) {	
}
