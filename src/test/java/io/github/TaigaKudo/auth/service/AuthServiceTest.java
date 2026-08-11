package io.github.TaigaKudo.auth.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import io.github.TaigaKudo.auth.dto.LoginResult;
import io.github.TaigaKudo.auth.dto.RefreshResult;
import io.github.TaigaKudo.auth.exception.AuthenticationException;
import io.github.TaigaKudo.auth.exception.EmailAlreadyUsedException;
import io.github.TaigaKudo.dto.LoginRequest;
import io.github.TaigaKudo.dto.RegisterRequest;
import io.github.TaigaKudo.entity.RefreshToken;
import io.github.TaigaKudo.entity.User;
import io.github.TaigaKudo.repository.UserRepository;
import io.github.TaigaKudo.security.JwtService;
import io.github.TaigaKudo.security.RefreshTokenProperties;
import io.github.TaigaKudo.service.RefreshTokenService;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

	@Mock
	private UserRepository userRepository;
	
	@Mock
	private PasswordEncoder passwordEncoder;
	
	@Mock
	private JwtService jwtService;
	
	@Mock
	private RefreshTokenService refreshTokenService;
	
	@Mock
	private RefreshToken refreshToken;
	
	@Mock
	private RefreshTokenProperties refreshTokenProperties;
	
	private AuthService authService;
	
	@BeforeEach
	void setUp() {
		authService = new AuthService(
				userRepository,
				passwordEncoder,
				jwtService,
				refreshTokenService,
				refreshTokenProperties
				);
	}
	
	@Test
	@DisplayName("正しいメールアドレスとパスワードでログインできる")
	void loginSuccess() {
		LoginRequest request = new LoginRequest("test@example.com", "password");
		
		User user = new User(
				"テストユーザー",
				"test@example.com",
				"hashed-password"
				);
		ReflectionTestUtils.setField(user, "id", 1L);
		when(userRepository.findByEmailAndDeletedAtIsNull("test@example.com"))
		.thenReturn(Optional.of(user));
		
		when(passwordEncoder.matches("password", "hashed-password"))
		.thenReturn(true);
		
		when(jwtService.generateAccessToken(1L))
		.thenReturn("access-token");
		
		when(refreshTokenProperties.expiration())
		.thenReturn(java.time.temporal.ChronoUnit.DAYS.getDuration().multipliedBy(7));
		
		when(refreshTokenService.issue(
				eq(user),
				any(LocalDateTime.class)
				))
		.thenReturn("refresh-token");
		
		LoginResult result = authService.login(request);
		
		assertEquals("access-token", result.accessToken());
		assertEquals("refresh-token", result.refreshToken());
	}
	
	@Test
	@DisplayName("ユーザーが見つからない場合ログインに失敗する")
	void loginFailUserNotFound() {
		LoginRequest request = new LoginRequest("nonexistent@example.com", "password");
		
		when(userRepository.findByEmailAndDeletedAtIsNull("nonexistent@example.com"))
		.thenReturn(Optional.empty());
		
		AuthenticationException exception = assertThrows(
				AuthenticationException.class,
				() -> authService.login(request)
				);
		
		assertEquals("メールアドレスまたはパスワードが正しくありません", exception.getMessage());
	}
	
	@Test
	@DisplayName("パスワードが不正な場合ログインに失敗する")
	void loginFailInvalidPassword() {
		LoginRequest request = new LoginRequest("test@example.com", "wrong-password");
		
		User user = new User(
				"テストユーザー",
				"test@example.com",
				"hashed-password"
				);
		ReflectionTestUtils.setField(user, "id", 1L);
		
		when(userRepository.findByEmailAndDeletedAtIsNull("test@example.com"))
		.thenReturn(Optional.of(user));
		
		when(passwordEncoder.matches("wrong-password", "hashed-password"))
		.thenReturn(false);
		
		AuthenticationException exception = assertThrows(
				AuthenticationException.class,
				() -> authService.login(request)
				);
		
		assertEquals("メールアドレスまたはパスワードが正しくありません", exception.getMessage());
	}
	
	@Test
	@DisplayName("有効なリフレッシュトークンから新しいアクセストークンを取得できる")
	void refreshSuccess() {
		String rawRefreshToken = "raw-refresh-token";
		
		User user = new User(
				"テストユーザー",
				"test@example.com",
				"hashed-password"
				);
		ReflectionTestUtils.setField(user, "id", 1L);
		
		when(refreshToken.getUser())
		.thenReturn(user);
		
		when(refreshTokenService.validate(rawRefreshToken))
		.thenReturn(refreshToken);
		
		when(jwtService.generateAccessToken(1L))
		.thenReturn("new-access-token");
		
		when(refreshTokenProperties.expiration())
		.thenReturn(java.time.temporal.ChronoUnit.DAYS.getDuration().multipliedBy(7));
		
		when(refreshTokenService.rotate(
				eq(refreshToken),
				any(LocalDateTime.class)
				))
		.thenReturn("new-refresh-token");
		
		RefreshResult result = authService.refresh(rawRefreshToken);
		
		assertEquals("new-access-token", result.accessToken());
		assertEquals("new-refresh-token", result.refreshToken());
	}
	
	@Test
	@DisplayName("無効なリフレッシュトークンで例外が発生する")
	void refreshFailInvalidToken() {
		String rawRefreshToken = "invalid-token";
		
		when(refreshTokenService.validate(rawRefreshToken))
		.thenThrow(new IllegalArgumentException("無効なトークンです"));
		
		assertThrows(
				IllegalArgumentException.class,
				() -> authService.refresh(rawRefreshToken)
				);
	}
	
	@Test
	@DisplayName("ログアウト時にリフレッシュトークンが無効化される")
	void logoutSuccess() {
		String rawRefreshToken = "raw-refresh-token";
		
		authService.logout(rawRefreshToken);
		
		verify(refreshTokenService).revoke(rawRefreshToken);
	}
	
	@Test
	@DisplayName("新規ユーザーの登録に成功する")
	void registerSuccess() {
		RegisterRequest request = new RegisterRequest(
				"新規ユーザー",
				"newuser@example.com",
				"password123"
				);
		
		when(userRepository.existsByEmailAndDeletedAtIsNull("newuser@example.com"))
		.thenReturn(false);
		
		when(passwordEncoder.encode("password123"))
		.thenReturn("encoded-password");
		
		authService.register(request);
		
		verify(userRepository).save(any(User.class));
	}
	
	@Test
	@DisplayName("メールアドレスが既に使用されている場合、登録に失敗する")
	void registerFailEmailAlreadyUsed() {
		RegisterRequest request = new RegisterRequest(
				"テストユーザー",
				"existing@example.com",
				"password123"
				);
		
		when(userRepository.existsByEmailAndDeletedAtIsNull("existing@example.com"))
		.thenReturn(true);
		
		EmailAlreadyUsedException exception = assertThrows(
				EmailAlreadyUsedException.class,
				() -> authService.register(request)
				);
		
		assertEquals("このメールアドレスは既に使用されています", exception.getMessage());
		verify(userRepository, never()).save(any(User.class));
	}
	
}
