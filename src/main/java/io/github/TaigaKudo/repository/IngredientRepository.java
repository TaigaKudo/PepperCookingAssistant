package io.github.TaigaKudo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.TaigaKudo.entity.Ingredient;

public interface IngredientRepository extends JpaRepository<Ingredient, Long> {

}
