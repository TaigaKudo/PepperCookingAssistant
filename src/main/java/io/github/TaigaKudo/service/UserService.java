package io.github.TaigaKudo.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.TaigaKudo.auth.exception.CurrentPasswordMismatchException;
import io.github.TaigaKudo.auth.exception.EmailAlreadyUsedException;
import io.github.TaigaKudo.auth.exception.UserNotFoundException;
import io.github.TaigaKudo.dto.EmailChangeRequest;
import io.github.TaigaKudo.dto.PasswordChangeRequest;
import io.github.TaigaKudo.dto.UserMeResponse;
import io.github.TaigaKudo.dto.UserUpdateRequest;
import io.github.TaigaKudo.entity.User;
import io.github.TaigaKudo.repository.UserRepository;

@Service
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final RefreshTokenService refreshTokenService;
	
	public UserService(
			UserRepository userRepository,
			PasswordEncoder passwordEncoder,
			RefreshTokenService refreshTokenService
			) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.refreshTokenService = refreshTokenService;
	}
	
	/* ユーザー情報取得 */
	@Transactional(readOnly = true)
	public UserMeResponse getMe(Long userId) {
		User user = userRepository.findByIdAndDeletedAtIsNull(userId)
				.orElseThrow(() -> new UserNotFoundException("ユーザーが見つかりません"));
		
		return new UserMeResponse(
				user.getId(),
				user.getName(),
				user.getEmail()
				);
	}
	
	/* ユーザー名更新処理 */
	@Transactional
	public UserMeResponse updateMe(
			Long userId,
			UserUpdateRequest request
			) {
		User user = userRepository.findByIdAndDeletedAtIsNull(userId)
				.orElseThrow(() ->
					new UserNotFoundException("ユーザーが見つかりません")
				);
		
		user.changeName(request.name());
		
		return new UserMeResponse(
				user.getId(),
				user.getName(),
				user.getEmail()
				);
	}
	
	/* パスワード変更処理 */
	@Transactional
	public void changePassword(
			Long userId,
			PasswordChangeRequest request
			) {
		User user = userRepository.findByIdAndDeletedAtIsNull(userId)
				.orElseThrow(() ->
					new UserNotFoundException("ユーザーが見つかりません")
						);
		
		if(!passwordEncoder.matches(
				request.currentPassword(),
				user.getPasswordHash()
				)) {
			throw new CurrentPasswordMismatchException("現在のパスワードが正しくありません");
		}
		
		String newPasswordHash = passwordEncoder.encode(request.newPassword());
		
		user.changePassword(newPasswordHash);
	}
	
	/* Email変更処理 */
	@Transactional
	public void changeEmail(
			Long userId,
			EmailChangeRequest request
			) {
		User user = userRepository.findByIdAndDeletedAtIsNull(userId)
				.orElseThrow(()->
				new UserNotFoundException("ユーザーが見つかりません")
						);
		
		if(!passwordEncoder.matches(request.currentPassword(), user.getPasswordHash())){
			throw new CurrentPasswordMismatchException("現在のパスワードが正しくありません");
		}
		
		if(userRepository.existsByEmailAndIdNotAndDeletedAtIsNull(request.newEmail(), userId)) {
			throw new EmailAlreadyUsedException("このメールアドレスは既に使用されています");
		}
		
		user.changeEmail(request.newEmail());
	}
	
	/* ユーザーアカウント論理削除処理 */
	@Transactional
	public void deleteMe(Long userId) {
		User user = userRepository.findByIdAndDeletedAtIsNull(userId)
				.orElseThrow(() ->
					new UserNotFoundException("ユーザーが見つかりません")
				);
		
		user.delete();
		
		// リフレッシュトークンも無効化する
		refreshTokenService.revokeAllByUserId(userId);
	}
}
