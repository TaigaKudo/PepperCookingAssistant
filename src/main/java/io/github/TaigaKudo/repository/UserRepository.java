package io.github.TaigaKudo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.TaigaKudo.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByEmailAndDeletedAtIsNull(String email);	
}
