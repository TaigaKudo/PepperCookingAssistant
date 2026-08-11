package io.github.TaigaKudo.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import io.github.TaigaKudo.auth.exception.CurrentPasswordMismatchException;
import io.github.TaigaKudo.auth.exception.EmailAlreadyUsedException;
import io.github.TaigaKudo.auth.exception.UserNotFoundException;
import io.github.TaigaKudo.dto.EmailChangeRequest;
import io.github.TaigaKudo.dto.PasswordChangeRequest;
import io.github.TaigaKudo.dto.UserMeResponse;
import io.github.TaigaKudo.dto.UserUpdateRequest;
import io.github.TaigaKudo.entity.User;
import io.github.TaigaKudo.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private RefreshTokenService refreshTokenService;

	private UserService userService;

	@BeforeEach
	void setUp() {
		userService = new UserService(userRepository, passwordEncoder, refreshTokenService);
	}

	@Test
	@DisplayName("自分の情報を取得できる")
	void getMe_ReturnsUserMeResponse() throws Exception {
		User user = new User("太郎", "taro@example.com", "hash");
		setId(user, 11L);

		when(userRepository.findByIdAndDeletedAtIsNull(11L)).thenReturn(Optional.of(user));

		UserMeResponse res = userService.getMe(11L);

		assertEquals(11L, res.id());
		assertEquals("太郎", res.name());
		assertEquals("taro@example.com", res.email());
	}

	@Test
	@DisplayName("ユーザーが存在しない場合は例外を投げる（getMe）")
	void getMe_ThrowsWhenNotFound() {
		when(userRepository.findByIdAndDeletedAtIsNull(99L)).thenReturn(Optional.empty());

		assertThrows(UserNotFoundException.class, () -> userService.getMe(99L));
	}

	@Test
	@DisplayName("ユーザー名を更新できる")
	void updateMe_ChangesName() throws Exception {
		User user = new User("old", "u@example.com", "hash");
		setId(user, 12L);

		when(userRepository.findByIdAndDeletedAtIsNull(12L)).thenReturn(Optional.of(user));

		UserUpdateRequest req = new UserUpdateRequest("newname");

		var res = userService.updateMe(12L, req);

		assertEquals(12L, res.id());
		assertEquals("newname", res.name());
	}

	@Test
	@DisplayName("パスワードを変更できる（現在のパスワードが正しい場合）")
	void changePassword_SucceedsWhenCurrentMatches() throws Exception {
		User user = new User("name", "a@example.com", "old-hash");
		setId(user, 13L);

		when(userRepository.findByIdAndDeletedAtIsNull(13L)).thenReturn(Optional.of(user));
		when(passwordEncoder.matches("current", "old-hash")).thenReturn(true);
		when(passwordEncoder.encode("newpass")).thenReturn("new-hash");

		PasswordChangeRequest req = new PasswordChangeRequest("current", "newpass");

		userService.changePassword(13L, req);

		assertEquals("new-hash", user.getPasswordHash());
	}

	@Test
	@DisplayName("現在のパスワードが違う場合は例外を投げる（changePassword）")
	void changePassword_ThrowsWhenCurrentMismatch() {
		User user = new User("name", "a@example.com", "old-hash");
		setId(user, 14L);

		when(userRepository.findByIdAndDeletedAtIsNull(14L)).thenReturn(Optional.of(user));
		when(passwordEncoder.matches("wrong", "old-hash")).thenReturn(false);

		PasswordChangeRequest req = new PasswordChangeRequest("wrong", "newpass");

		assertThrows(CurrentPasswordMismatchException.class, () -> userService.changePassword(14L, req));
	}

	@Test
	@DisplayName("メールアドレスを変更できる（条件が満たされる場合）")
	void changeEmail_Succeeds() throws Exception {
		User user = new User("name", "old@example.com", "old-hash");
		setId(user, 15L);

		when(userRepository.findByIdAndDeletedAtIsNull(15L)).thenReturn(Optional.of(user));
		when(passwordEncoder.matches("pwd", "old-hash")).thenReturn(true);
		when(userRepository.existsByEmailAndIdNotAndDeletedAtIsNull("new@example.com", 15L)).thenReturn(false);

		EmailChangeRequest req = new EmailChangeRequest("new@example.com", "pwd");

		userService.changeEmail(15L, req);

		assertEquals("new@example.com", user.getEmail());
	}

	@Test
	@DisplayName("現在のパスワードが間違っていると例外を投げる（changeEmail）")
	void changeEmail_ThrowsOnPasswordMismatch() {
		User user = new User("name", "old@example.com", "old-hash");
		setId(user, 16L);

		when(userRepository.findByIdAndDeletedAtIsNull(16L)).thenReturn(Optional.of(user));
		when(passwordEncoder.matches("bad", "old-hash")).thenReturn(false);

		EmailChangeRequest req = new EmailChangeRequest("x@example.com", "bad");

		assertThrows(CurrentPasswordMismatchException.class, () -> userService.changeEmail(16L, req));
	}

	@Test
	@DisplayName("変更先メールアドレスが既に使われている場合は例外を投げる（changeEmail）")
	void changeEmail_ThrowsWhenEmailUsed() {
		User user = new User("name", "old@example.com", "old-hash");
		setId(user, 17L);

		when(userRepository.findByIdAndDeletedAtIsNull(17L)).thenReturn(Optional.of(user));
		when(passwordEncoder.matches("pwd", "old-hash")).thenReturn(true);
		when(userRepository.existsByEmailAndIdNotAndDeletedAtIsNull("used@example.com", 17L)).thenReturn(true);

		EmailChangeRequest req = new EmailChangeRequest("used@example.com", "pwd");

		assertThrows(EmailAlreadyUsedException.class, () -> userService.changeEmail(17L, req));
	}

	@Test
	@DisplayName("アカウントを削除するとユーザーは論理削除されリフレッシュトークンが無効化される")
	void deleteMe_DeletesUserAndRevokesTokens() throws Exception {
		User user = new User("name", "u@example.com", "hash");
		setId(user, 18L);

		when(userRepository.findByIdAndDeletedAtIsNull(18L)).thenReturn(Optional.of(user));

		userService.deleteMe(18L);

		assertNotNull(getDeletedAt(user));
		verify(refreshTokenService).revokeAllByUserId(18L);
	}

	@Test
	@DisplayName("対象ユーザーが存在しない場合は例外を投げる（deleteMe）")
	void deleteMe_ThrowsWhenNotFound() {
		when(userRepository.findByIdAndDeletedAtIsNull(999L)).thenReturn(Optional.empty());

		assertThrows(UserNotFoundException.class, () -> userService.deleteMe(999L));
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

	// ユーティリティ: User.deletedAt を取得（リフレクション）
	private static Object getDeletedAt(User user) {
		try {
			Field f = User.class.getDeclaredField("deletedAt");
			f.setAccessible(true);
			return f.get(user);
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException(e);
		}
	}

}
