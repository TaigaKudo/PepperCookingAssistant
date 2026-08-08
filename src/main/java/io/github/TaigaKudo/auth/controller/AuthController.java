package io.github.TaigaKudo.auth.controller;

import jakarta.validation.Valid;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.TaigaKudo.auth.dto.LoginResult;
import io.github.TaigaKudo.auth.service.AuthService;
import io.github.TaigaKudo.dto.LoginRequest;
import io.github.TaigaKudo.dto.TokenResponse;
import io.github.TaigaKudo.security.RefreshTokenProperties;

@RestController
@RequestMapping("/auth")
public class AuthController {

	private final AuthService authService;
	private final RefreshTokenProperties refreshTokenProperties;
	
	public AuthController(
			AuthService authService,
			RefreshTokenProperties refreshTokenProperties
			) {
		this.authService = authService;
		this.refreshTokenProperties = refreshTokenProperties;
	}
	
	@PostMapping("/login")
	public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request){
		LoginResult result = authService.login(request);
		
		ResponseCookie refreshTokenCookie = ResponseCookie
				.from("refreshToken", result.refreshToken())
				.httpOnly(true)
				.secure(refreshTokenProperties.cookieSecure())
				.path("/auth")
				.maxAge(refreshTokenProperties.expiration())
				.sameSite("Lax")
				.build();
		
		return ResponseEntity
				.ok()
				.header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
				.body(new TokenResponse(result.accessToken()));
	}
}
