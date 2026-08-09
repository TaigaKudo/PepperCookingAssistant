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

import io.github.TaigaKudo.auth.exception.AuthenticationException;
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
	
	/* 
	 * アクセストークン生成
	 * リフレッシュトークン生成
	 * リフレッシュトークンDB格納 */
	@Transactional
	public String issue(User user, LocalDateTime expiresAt) {
		String rawToken = generateToken();
		String tokenHash = hashToken(rawToken);
		
		RefreshToken refreshToken = new RefreshToken(user, tokenHash, expiresAt);
		
		refreshTokenRepository.save(refreshToken);
		
		return rawToken;
	}
	
	/* アクセストークン生成 */
	private String generateToken() {
		byte[] tokenBytes = new byte[TOKEN_BYTE_LENGTH];
		secureRandom.nextBytes(tokenBytes);
		
		return Base64.getUrlEncoder()
				.withoutPadding()
				.encodeToString(tokenBytes);
	}
	
	/* パスワードハッシュ化（平文⇒ハッシュ） */
	private String hashToken(String rawToken) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hashBytes = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
			
			return HexFormat.of().formatHex(hashBytes);
		}catch(NoSuchAlgorithmException exception) {
			throw new IllegalStateException("SHA-256 is not available", exception);
		}
	}
	
	/* アクセストークンの有効期限確認 */
	@Transactional(readOnly = true)
	public RefreshToken validate(String rawToken) {
		String tokenHash = hashToken(rawToken);
		
		RefreshToken refreshToken = refreshTokenRepository
				.findByTokenHashAndRevokedAtIsNull(tokenHash)
				.orElseThrow(() ->
					new AuthenticationException("無効なRefresh Tokenです")
						);
		
		if(refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
			throw new AuthenticationException("Refresh Tokenの有効期限が切れています");
		}
		
		return refreshToken;
	}
	
	/* リフレッシュトークンローテート処理 */
	@Transactional
	public String rotate(RefreshToken current, LocalDateTime newExpiresAt) {
		current.revoke();
		
		return issue(current.getUser(), newExpiresAt);
	}
	
	/* リフレッシュトークン失効処理 */
	@Transactional
	public void revoke(String rawToken) {
		RefreshToken refreshToken = validate(rawToken);
		
		refreshToken.revoke();
	}
}
