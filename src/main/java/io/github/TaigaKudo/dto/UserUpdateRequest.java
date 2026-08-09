package io.github.TaigaKudo.dto;

import jakarta.validation.constraints.NotBlank;

public record UserUpdateRequest(
		@NotBlank
		String name
		) {

}
