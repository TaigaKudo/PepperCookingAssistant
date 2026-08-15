package io.github.TaigaKudo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import io.github.TaigaKudo.entity.Stock;

public interface StockRepository extends JpaRepository<Stock, Long> {

	@Query("""
			SELECT s
			FROM Stock s
			JOIN FETCH s.ingredient i
			LEFT JOIN FETCH i.category c
			WHERE s.user.id = :userId
			AND s.deletedAt IS NULL
			AND i.deletedAt IS NULL
			AND (c IS NULL OR c.deletedAt IS NULL)
			""")
	List<Stock> findAllActiveByUserId(Long userId);
	
	@Query("""
			SELECT s
			FROM Stock s
			JOIN FETCH s.ingredient i
			LEFT JOIN FETCH i.category c
			WHERE s.id = :stockId
			AND s.user.id = :userId
			AND s.deletedAt IS NULL
			AND i.deletedAt IS NULL
			AND (c IS NULL OR c.deletedAt IS NULL)
			""")
	Optional<Stock> findActiveByStockIdAndUserId(Long stockId, Long userId);
}
