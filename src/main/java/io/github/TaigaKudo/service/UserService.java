package io.github.TaigaKudo.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.TaigaKudo.dto.UserMeResponse;
import io.github.TaigaKudo.entity.User;
import io.github.TaigaKudo.repository.UserRepository;

@Service
public class UserService {

	private final UserRepository userRepository;
	
	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
	/* ユーザー情報取得 */
	@Transactional(readOnly = true)
	public UserMeResponse getMe(Long userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new IllegalArgumentException("ユーザーが見つかりません"));
		
		return new UserMeResponse(
				user.getId(),
				user.getName(),
				user.getEmail()
				);
	}
}
