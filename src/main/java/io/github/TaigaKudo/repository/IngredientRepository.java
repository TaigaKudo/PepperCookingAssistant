package io.github.TaigaKudo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.TaigaKudo.entity.Ingredient;

public interface IngredientRepository extends JpaRepository<Ingredient, Long> {

	Optional<Ingredient> findByIdAndDeletedAtIsNull(Long id);
	List<Ingredient> findAllByDeletedAtIsNull();
}
