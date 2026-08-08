package io.github.TaigaKudo.auth.service;

import java.time.LocalDateTime;

import jakarta.transaction.Transactional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import io.github.TaigaKudo.auth.dto.LoginResult;
import io.github.TaigaKudo.auth.dto.RefreshResult;
import io.github.TaigaKudo.auth.exception.AuthenticationException;
import io.github.TaigaKudo.dto.LoginRequest;
import io.github.TaigaKudo.entity.RefreshToken;
import io.github.TaigaKudo.entity.User;
import io.github.TaigaKudo.repository.UserRepository;
import io.github.TaigaKudo.security.JwtService;
import io.github.TaigaKudo.security.RefreshTokenProperties;
import io.github.TaigaKudo.service.RefreshTokenService;

@Service
public class AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	private final RefreshTokenService refreshTokenService;
	private final RefreshTokenProperties refreshTokenProperties;
	
	public AuthService(
			UserRepository userRepository,
			PasswordEncoder passwordEncoder,
			JwtService jwtService,
			RefreshTokenService refreshTokenService,
			RefreshTokenProperties refreshTokenProperties
			) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtService = jwtService;
		this.refreshTokenService = refreshTokenService;
		this.refreshTokenProperties = refreshTokenProperties;
	}
	
	/* ログイン認証 */
	@Transactional
	public LoginResult login(LoginRequest request) {
		// メールアドレス認証
		User user = userRepository
				.findByEmailAndDeletedAtIsNull(request.email())
				.orElseThrow(() ->
				new AuthenticationException("メールアドレスまたはパスワードが正しくありません")
				);
		
		// パスワード認証
		if(!passwordEncoder.matches(
				request.password(),
				user.getPasswordHash()
				)
			) {
			throw new AuthenticationException("メールアドレスまたはパスワードが正しくありません");
		}
		
		// アクセストークン発行
		String accessToken = jwtService.generateAccessToken(user.getId());
		
		// リフレッシュトークン発行
		LocalDateTime refreshTokenExpiresAt = LocalDateTime.now().plus(refreshTokenProperties.expiration());
		String refreshToken = refreshTokenService.issue(user, refreshTokenExpiresAt);
		
		return new LoginResult(accessToken, refreshToken);
	}
	
	/* アクセストークン再発行 */
	@Transactional
	public RefreshResult refresh(String rawRefreshToken) {
		RefreshToken current = refreshTokenService.validate(rawRefreshToken);
		
		User user = current.getUser();
		
		String accessToken = jwtService.generateAccessToken(user.getId());
		
		LocalDateTime newExpiresAt = LocalDateTime.now().plus(refreshTokenProperties.expiration());
		
		String newRefreshToken = refreshTokenService.rotate(current, newExpiresAt);
		
		return new RefreshResult(accessToken, newRefreshToken);
	}
}
