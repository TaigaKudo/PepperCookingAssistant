package io.github.TaigaKudo.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.TaigaKudo.dto.StockRegisterRequest;
import io.github.TaigaKudo.dto.StockResponse;
import io.github.TaigaKudo.dto.StockUpdateRequest;
import io.github.TaigaKudo.service.StockService;

@RestController
@RequestMapping("/stocks")
public class StockController {
	
	private final StockService stockService;
	
	public StockController(
			StockService stockService
			) {
		this.stockService = stockService;
	}
	
	@PostMapping
	public ResponseEntity<Void> registerStock(
			Authentication authentication,
			@Valid @RequestBody StockRegisterRequest resuest
			){
		Long userId = (Long)authentication.getPrincipal();
		stockService.registerStock(userId, resuest);
		
		return ResponseEntity.noContent().build();
	}
	
	@GetMapping
	public ResponseEntity<List<StockResponse>> getStocks(
			Authentication authentication
			){
		Long userId =  (Long)authentication.getPrincipal();
		List<StockResponse> response = stockService.getStocks(userId);
		
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/{stockId}")
	public ResponseEntity<StockResponse> getStock(
			Authentication authentication,
			@PathVariable Long stockId
			){
		Long userId = (Long)authentication.getPrincipal();
		StockResponse response = stockService.getStock(userId, stockId);
		
		return ResponseEntity.ok(response);
	}
	
	@PutMapping("/{stockId}")
	public ResponseEntity<StockResponse> updateStock(
			Authentication authentication,
			@PathVariable Long stockId,
			@Valid @RequestBody StockUpdateRequest request
			){
		Long userId = (Long)authentication.getPrincipal();
		StockResponse response = stockService.updateStock(userId, stockId, request);
		
		return ResponseEntity.ok(response);
	}
	
	@DeleteMapping("/{stockId}")
	public ResponseEntity<Void> deleteStock(
			Authentication authentication,
			@PathVariable Long stockId
			){
		Long userId = (Long)authentication.getPrincipal();
		stockService.deleteStock(userId, stockId);
		
		return ResponseEntity.noContent().build();
	}
}
