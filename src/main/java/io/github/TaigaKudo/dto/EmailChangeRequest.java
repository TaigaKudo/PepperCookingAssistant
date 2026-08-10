package io.github.TaigaKudo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EmailChangeRequest(
		@NotBlank
		@Email
		String newEmail,
		
		@NotBlank
		String currentPassword
		){

}
