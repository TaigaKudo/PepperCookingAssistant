package io.github.TaigaKudo.auth.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.TaigaKudo.entity.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long>{
	Optional<RefreshToken> findByTokenHashAndRevokedAtIsNull(String tokenHash);
	public List<RefreshToken> findAllByUserIdAndRevokedAtIsNull(Long userId);
}
