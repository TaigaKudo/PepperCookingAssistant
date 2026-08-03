package io.github.TaigaKudo.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.TaigaKudo.auth.repository.RefreshTokenRepository;
import io.github.TaigaKudo.entity.RefreshToken;
import io.github.TaigaKudo.entity.User;

@Service
public class RefreshTokenService {

	private final int TOKEN_BYTE_LENGTH = 32;
	
	private final RefreshTokenRepository refreshTokenRepository;
	private final SecureRandom secureRandom;
	
	public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
		this.refreshTokenRepository = refreshTokenRepository;
		this.secureRandom = new SecureRandom();
	}
	
	@Transactional
	public String issue(User user, LocalDateTime expiresAt) {
		String rawToken = generateToken();
		String tokenHash = hashToken(rawToken);
		
		RefreshToken refreshToken = new RefreshToken(user, tokenHash, expiresAt);
		
		refreshTokenRepository.save(refreshToken);
		
		return rawToken;
	}
	
	private String generateToken() {
		byte[] tokenBytes = new byte[TOKEN_BYTE_LENGTH];
		secureRandom.nextBytes(tokenBytes);
		
		return Base64.getUrlEncoder()
				.withoutPadding()
				.encodeToString(tokenBytes);
	}
	
	private String hashToken(String rawToken) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hashBytes = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
			
			return HexFormat.of().formatHex(hashBytes);
		}catch(NoSuchAlgorithmException exception) {
			throw new IllegalStateException("SHA-256 is not available", exception);
		}
	}
}
