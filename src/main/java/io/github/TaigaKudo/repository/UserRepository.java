package io.github.TaigaKudo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.TaigaKudo.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

}
