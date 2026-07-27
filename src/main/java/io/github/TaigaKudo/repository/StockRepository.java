package io.github.TaigaKudo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.TaigaKudo.entity.Stock;

public interface StockRepository extends JpaRepository<Stock, Long> {

}
