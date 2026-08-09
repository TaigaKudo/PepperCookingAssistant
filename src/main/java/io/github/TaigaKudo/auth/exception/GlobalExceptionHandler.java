package io.github.TaigaKudo.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<String> handleAuthenticationException(AuthenticationException exception){
		return ResponseEntity
				.status(HttpStatus.UNAUTHORIZED)
				.body(exception.getMessage());
	}
}
