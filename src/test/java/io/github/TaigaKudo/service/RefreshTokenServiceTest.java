package io.github.TaigaKudo.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.github.TaigaKudo.auth.exception.AuthenticationException;
import io.github.TaigaKudo.auth.repository.RefreshTokenRepository;
import io.github.TaigaKudo.entity.RefreshToken;
import io.github.TaigaKudo.entity.User;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

	@Mock
	private RefreshTokenRepository refreshTokenRepository;

	private RefreshTokenService refreshTokenService;

	@Captor
	private ArgumentCaptor<RefreshToken> refreshTokenCaptor;

	@BeforeEach
	void setUp() {
		refreshTokenService = new RefreshTokenService(refreshTokenRepository);
	}

	@Test
	@DisplayName("リフレッシュトークンを発行してDBに保存する")
	void issue_SavesTokenAndReturnsRaw() throws Exception {
		User user = new User("name", "email@example.com", "hash");
		setId(user, 1L);

		LocalDateTime expiresAt = LocalDateTime.now().plusDays(7);

		String raw = refreshTokenService.issue(user, expiresAt);

		assertNotNull(raw);
		assertFalse(raw.isBlank());

		verify(refreshTokenRepository).save(refreshTokenCaptor.capture());
		RefreshToken saved = refreshTokenCaptor.getValue();

		// compute expected hash
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		String expectedHash = HexFormat.of().formatHex(digest.digest(raw.getBytes(java.nio.charset.StandardCharsets.UTF_8)));

		assertEquals(expectedHash, saved.getTokenHash());
		assertEquals(expiresAt, saved.getExpiresAt());
		assertEquals(user, saved.getUser());
	}

	@Test
	@DisplayName("有効なリフレッシュトークンを検証して取得できる")
	void validate_ReturnsRefreshToken_WhenValid() throws Exception {
		User user = new User("name", "email@example.com", "hash");
		setId(user, 2L);

		String raw = "raw-token-value";
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		String tokenHash = HexFormat.of().formatHex(digest.digest(raw.getBytes(java.nio.charset.StandardCharsets.UTF_8)));

		RefreshToken token = new RefreshToken(user, tokenHash, LocalDateTime.now().plusDays(1));

		when(refreshTokenRepository.findByTokenHashAndRevokedAtIsNull(tokenHash)).thenReturn(Optional.of(token));

		RefreshToken result = refreshTokenService.validate(raw);

		assertSame(token, result);
	}

	@Test
	@DisplayName("無効なリフレッシュトークンは例外を投げる")
	void validate_ThrowsWhenNotFound() throws Exception {
		String raw = "missing-token";
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		String tokenHash = HexFormat.of().formatHex(digest.digest(raw.getBytes(java.nio.charset.StandardCharsets.UTF_8)));

		when(refreshTokenRepository.findByTokenHashAndRevokedAtIsNull(tokenHash)).thenReturn(Optional.empty());

		AuthenticationException ex = assertThrows(AuthenticationException.class, () -> refreshTokenService.validate(raw));
		assertTrue(ex.getMessage().contains("無効なRefresh Token"));
	}

	@Test
	@DisplayName("期限切れのリフレッシュトークンは例外を投げる")
	void validate_ThrowsWhenExpired() throws Exception {
		User user = new User("name", "email@example.com", "hash");
		setId(user, 3L);

		String raw = "expired-token";
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		String tokenHash = HexFormat.of().formatHex(digest.digest(raw.getBytes(java.nio.charset.StandardCharsets.UTF_8)));

		RefreshToken token = new RefreshToken(user, tokenHash, LocalDateTime.now().minusDays(1));

		when(refreshTokenRepository.findByTokenHashAndRevokedAtIsNull(tokenHash)).thenReturn(Optional.of(token));

		AuthenticationException ex = assertThrows(AuthenticationException.class, () -> refreshTokenService.validate(raw));
		assertTrue(ex.getMessage().contains("有効期限"));
	}

	@Test
	@DisplayName("トークンをローテートすると古いトークンは無効化され新しいトークンが発行される")
	void rotate_RevokesCurrentAndIssuesNew() {
		User user = new User("name", "email@example.com", "hash");
		setId(user, 4L);

		RefreshToken current = mock(RefreshToken.class);
		when(current.getUser()).thenReturn(user);

		LocalDateTime newExpiresAt = LocalDateTime.now().plusDays(10);

		String newRaw = refreshTokenService.rotate(current, newExpiresAt);

		assertNotNull(newRaw);
		assertFalse(newRaw.isBlank());

		verify(current).revoke();
		verify(refreshTokenRepository).save(any(RefreshToken.class));
	}

	@Test
	@DisplayName("リフレッシュトークンを失効させると該当トークンは無効化される")
	void revoke_RevokeToken() throws Exception {
		User user = new User("name", "email@example.com", "hash");
		setId(user, 5L);

		String raw = "revoke-token";
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		String tokenHash = HexFormat.of().formatHex(digest.digest(raw.getBytes(java.nio.charset.StandardCharsets.UTF_8)));

		RefreshToken token = spy(new RefreshToken(user, tokenHash, LocalDateTime.now().plusDays(1)));

		when(refreshTokenRepository.findByTokenHashAndRevokedAtIsNull(tokenHash)).thenReturn(Optional.of(token));

		refreshTokenService.revoke(raw);

		verify(token).revoke();
	}

	@Test
	@DisplayName("ユーザーIDに紐づく全てのトークンを無効化する")
	void revokeAllByUserId_RevokeAll() {
		User user = new User("name", "email@example.com", "hash");
		setId(user, 6L);

		RefreshToken t1 = spy(new RefreshToken(user, "h1", LocalDateTime.now().plusDays(1)));
		RefreshToken t2 = spy(new RefreshToken(user, "h2", LocalDateTime.now().plusDays(1)));

		when(refreshTokenRepository.findAllByUserIdAndRevokedAtIsNull(6L)).thenReturn(List.of(t1, t2));

		refreshTokenService.revokeAllByUserId(6L);

		verify(t1).revoke();
		verify(t2).revoke();
	}

	// ユーティリティ: User.id フィールドをリフレクションで設定
	private static void setId(User user, Long id) {
		try {
			Field f = User.class.getDeclaredField("id");
			f.setAccessible(true);
			f.set(user, id);
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException(e);
		}
	}

}
