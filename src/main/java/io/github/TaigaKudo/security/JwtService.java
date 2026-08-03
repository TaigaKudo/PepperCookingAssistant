package io.github.TaigaKudo.security;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

	private final SecretKey signinKey;
	private final Duration accessTokenExpiration;
	
	public JwtService(JwtProperties properties) {
		Objects.requireNonNull(properties, "JwtProperties must not be null");
		
		if(properties.secretKey() == null || properties.secretKey().isBlank()) {
			throw new IllegalStateException("security.jwt.secret-key must not be blank");
		}
		
		if(properties.accessTokenExpiration() == null
				|| properties.accessTokenExpiration().isZero()
				|| properties.accessTokenExpiration().isNegative()) {
			throw new IllegalStateException("security.jwt.access-token-expiration must be positive");
		}
	
		try {
			byte[] keyBytes = Decoders.BASE64.decode(properties.secretKey());
			this.signinKey = Keys.hmacShaKeyFor(keyBytes);
		}catch(RuntimeException exception) {
			throw new IllegalStateException("security.jwt.secret-key must be a Base64-encoded key with sufficient length", exception);
		}
		
		this.accessTokenExpiration = properties.accessTokenExpiration();
	}
	
	public String generateAccessToken(Long userId) {
		Objects.requireNonNull(userId, "userId must not be null");
		Instant issuedAt = Instant.now();
		Instant expiresAt = issuedAt.plus(accessTokenExpiration);
		
		return Jwts.builder()
				.subject(userId.toString())
				.issuedAt(Date.from(issuedAt))
				.expiration(Date.from(expiresAt))
				.signWith(signinKey, Jwts.SIG.HS256)
				.compact();
	}
	
	public Long validateAndExtractUserId(String token) {
		if(token == null
				|| token.isBlank()) {
			throw new IllegalArgumentException("token must not blank");
		}
		
		Claims claims = parseClaims(token);
		String subject = claims.getSubject();
		
		if(subject == null
				|| subject.isBlank()) {
			throw new IllegalArgumentException("JWT subject must not be blank");
		}
		
		try {
			return Long.valueOf(subject);
		}catch(NumberFormatException exception) {
			throw new IllegalArgumentException("JWT subject must be valid user ID", exception);
		}
	}
	
	private Claims parseClaims(String token) {
		return Jwts.parser()
				.verifyWith(signinKey)
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}
}