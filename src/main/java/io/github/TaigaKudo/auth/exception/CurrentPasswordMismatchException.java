package io.github.TaigaKudo.auth.exception;

public class CurrentPasswordMismatchException extends RuntimeException {

	public CurrentPasswordMismatchException(String message) {
		super(message);
	}
}
