package io.github.TaigaKudo.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "stocks")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Stock {
	
	/* アプリ側で使用するコンストラクタ */
	public Stock(
			Ingredient ingredient,
			User user,
			BigDecimal quantity,
			LocalDate expirationDate
			) {
		this.ingredient = ingredient;
		this.user = user;
		this.quantity = quantity;
		this.expirationDate = expirationDate;
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "ingredient_id", nullable = false)
	private Ingredient ingredient;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;
	
	@Column(name = "quantity", nullable = false)
	private BigDecimal quantity;
	
	@Column(name = "expiration_date", nullable = false)
	private LocalDate expirationDate;
	
	@Column(name = "created_at", nullable = false)
	@CreationTimestamp
	private LocalDateTime createdAt;
	
	@Column(name = "updated_at", nullable = false)
	@UpdateTimestamp
	private LocalDateTime updatedAt;
	
	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;
	
	public void changeIngredient(Ingredient ingredient) {
		this.ingredient = ingredient;
	}
	
	
	public void changeQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}
	
	public void changeExpirationDate(LocalDate expirationDate) {
		this.expirationDate = expirationDate;
	}
	
	public void delete() {
		this.deletedAt = LocalDateTime.now();
	}
}