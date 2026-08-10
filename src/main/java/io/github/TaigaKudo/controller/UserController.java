package io.github.TaigaKudo.controller;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.TaigaKudo.dto.EmailChangeRequest;
import io.github.TaigaKudo.dto.PasswordChangeRequest;
import io.github.TaigaKudo.dto.UserMeResponse;
import io.github.TaigaKudo.dto.UserUpdateRequest;
import io.github.TaigaKudo.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController {

	private final UserService userService;
	
	public UserController(UserService userService) {
		this.userService = userService;
	}
	
	@GetMapping("/me")
	public ResponseEntity<UserMeResponse> me(Authentication authentication){
		Long userId = (Long)authentication.getPrincipal();
		
		UserMeResponse response = userService.getMe(userId);
		
		return ResponseEntity.ok(response);
	}
	
	@PutMapping("/me")
	public ResponseEntity<UserMeResponse> updateMe(
			Authentication authentication,
			@Valid @RequestBody UserUpdateRequest request
			){
		Long userId = (Long)authentication.getPrincipal();
		
		UserMeResponse response = userService.updateMe(userId, request);
		
		return ResponseEntity.ok(response);
	}
	
	@PutMapping("/me/password")
	public ResponseEntity<Void> changepassword(
			Authentication authentication,
			@Valid @RequestBody PasswordChangeRequest request
			){
		Long userId = (Long)authentication.getPrincipal();
		
		userService.changePassword(userId, request);
		
		return ResponseEntity.noContent().build();
	}
	
	@PutMapping("/me/email")
	public ResponseEntity<Void> changeEmail(
			Authentication authentication,
			@Valid @RequestBody EmailChangeRequest request
			){
		Long userId = (Long)authentication.getPrincipal();
		
		userService.changeEmail(userId, request);
		
		return ResponseEntity.noContent().build();
	}
}
