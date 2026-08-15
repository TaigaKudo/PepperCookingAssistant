package io.github.TaigaKudo.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record StockRegisterRequest(
		@NotNull
		@Positive
		Long ingredientId,
		
		@NotNull
		@DecimalMin("0.00")
		@DecimalMax("999.99")
		BigDecimal quantity,
		
		@NotNull
		@FutureOrPresent
		LocalDate expirationDate
		) {
}
