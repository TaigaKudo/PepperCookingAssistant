package io.github.TaigaKudo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.TaigaKudo.entity.IngredientCategory;

public interface IngredientCategoryRepository extends JpaRepository<IngredientCategory, Long> {

}
