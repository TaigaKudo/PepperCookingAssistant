package io.github.TaigaKudo.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ingredient_categories")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class IngredientCategory {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "category_name", nullable = false, unique = true)
	private String categoryName;
	
	@Column(name = "reading")
	private String reading;
	
	@Column(name = "created_at", nullable = false)
	@CreationTimestamp
	private LocalDateTime createdAt;

	@Column(name = "updated_at", nullable = false)
	@UpdateTimestamp
	private LocalDateTime updatedAt;
	
	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;
}
