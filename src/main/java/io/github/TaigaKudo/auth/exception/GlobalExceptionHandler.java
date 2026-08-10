package io.github.TaigaKudo.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.github.TaigaKudo.dto.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException exception){
		return ResponseEntity
				.status(HttpStatus.UNAUTHORIZED)
				.body(new ErrorResponse(exception.getMessage()));
	}
	
	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException exception){
		return ResponseEntity
				.status(HttpStatus.NOT_FOUND)
				.body(new ErrorResponse(exception.getMessage()));
	}
	
	@ExceptionHandler(EmailAlreadyUsedException.class)
	public ResponseEntity<ErrorResponse> handlerEmailAlreadyUsedException(EmailAlreadyUsedException exception){
		return ResponseEntity
				.status(HttpStatus.CONFLICT)
				.body(new ErrorResponse(exception.getMessage()));
	}
	
	@ExceptionHandler(CurrentPasswordMismatchException.class)
	public ResponseEntity<ErrorResponse> handleCurrentPasswordMismatchException(CurrentPasswordMismatchException exception){
		return ResponseEntity
				.status(HttpStatus.BAD_REQUEST)
				.body(new ErrorResponse(exception.getMessage()));
	}
}
