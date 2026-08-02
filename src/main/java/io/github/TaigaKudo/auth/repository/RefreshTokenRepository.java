package io.github.TaigaKudo.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.TaigaKudo.entity.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long>{
	Optional<RefreshToken> findByTokenHashAndRevokedAtIsNull(String tokenHash);
}
