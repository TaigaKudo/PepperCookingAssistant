package io.github.TaigaKudo.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.TaigaKudo.auth.exception.IngredientNotFoundException;
import io.github.TaigaKudo.auth.exception.StockNotFoundException;
import io.github.TaigaKudo.auth.exception.UserNotFoundException;
import io.github.TaigaKudo.dto.StockRegisterRequest;
import io.github.TaigaKudo.dto.StockResponse;
import io.github.TaigaKudo.dto.StockUpdateRequest;
import io.github.TaigaKudo.entity.Ingredient;
import io.github.TaigaKudo.entity.IngredientCategory;
import io.github.TaigaKudo.entity.Stock;
import io.github.TaigaKudo.entity.User;
import io.github.TaigaKudo.repository.IngredientRepository;
import io.github.TaigaKudo.repository.StockRepository;
import io.github.TaigaKudo.repository.UserRepository;

@Service
public class StockService {

	private final StockRepository stockRepository;
	private final IngredientRepository ingredientRepository;
	private final UserRepository userRepository;
	
	public StockService(
			StockRepository stockRepository,
			IngredientRepository ingredientRepository,
			UserRepository userRepository
			) {
		this.stockRepository = stockRepository;
		this.ingredientRepository = ingredientRepository;
		this.userRepository = userRepository;
	}
	
	/* 食材在庫情報登録処理 */
	@Transactional
	public void registerStock(
			Long userId,
			StockRegisterRequest request
			) {
		
		User user = userRepository.findByIdAndDeletedAtIsNull(userId)
				.orElseThrow(() -> new UserNotFoundException("ユーザーが見つかりません"));
		
		Long ingredientId = request.ingredientId();
		Ingredient ingredient = ingredientRepository.findByIdAndDeletedAtIsNull(ingredientId)
				.orElseThrow(() -> new IngredientNotFoundException("指定した食材が見つかりません"));
		
		BigDecimal quantity = request.quantity();
		
		LocalDate expirationDate = request.expirationDate();
		
		Stock stock = new Stock(
				ingredient,
				user,
				quantity,
				expirationDate
				);
		
		stockRepository.save(stock);
	}
	
	/* 食材在庫情報全件取得処理 */
	@Transactional(readOnly = true)
	public List<StockResponse> getStocks(
			Long userId
			){
		
		List<Stock> stocks = stockRepository.findAllActiveByUserId(userId);
		
		List<StockResponse> response = stocks
				.stream()
				.map(this::toResponse)
				.toList();
		
		return response;
	}
	
	/* 食材在庫情報取得処理 */
	@Transactional(readOnly = true)
	public StockResponse getStock(
			Long userId,
			Long stockId
			) {
		
		Stock stock = stockRepository.findActiveByStockIdAndUserId(stockId, userId)
				.orElseThrow(() -> new StockNotFoundException("食材在庫が見つかりません"));
		
		return toResponse(stock);
	}
	
	/* 食材在庫情報更新処理 */
	@Transactional
	public StockResponse updateStock(
			Long userId,
			Long stockId,
			StockUpdateRequest request
			) {
		Stock stock = stockRepository.findActiveByStockIdAndUserId(stockId, userId)
				.orElseThrow(() -> new StockNotFoundException("食材在庫が見つかりません"));
		
		// 変更箇所を更新
		Long currentIngredientId = stock.getIngredient().getId();
		Long requestedIngredientId = request.ingredientId();
		
		BigDecimal currentQuantity = stock.getQuantity();
		BigDecimal requestedQuantity = request.quantity();
		
		LocalDate currentExpirationDate = stock.getExpirationDate();
		LocalDate requestedExpirationDate = request.expirationDate();
		
		if(!Objects.equals(currentIngredientId, requestedIngredientId)) {
			Ingredient requestedIngredient = ingredientRepository.findByIdAndDeletedAtIsNull(requestedIngredientId)
					.orElseThrow(() -> new IngredientNotFoundException("指定した食材が見つかりません"));
			stock.changeIngredient(requestedIngredient);
		}
		
		if(currentQuantity.compareTo(requestedQuantity) != 0) {
			stock.changeQuantity(request.quantity());
		}
		
		if(!Objects.equals(currentExpirationDate, requestedExpirationDate)) {
			stock.changeExpirationDate(request.expirationDate());
		}
		
		return toResponse(stock);
	}
	
	/* 食材情報論理削除処理 */
	@Transactional
	public void deleteStock(
			Long userId,
			Long stockId
			) {
		Stock stock = stockRepository.findActiveByStockIdAndUserId(stockId, userId)
				.orElseThrow(() -> new StockNotFoundException("食材在庫が見つかりません"));
		
		stock.delete();
	}
	
	private StockResponse toResponse(Stock stock){
		
		Ingredient ingredient = stock.getIngredient();
		IngredientCategory ingredientCategory = ingredient.getCategory();
		
		return new StockResponse(
				stock.getId(),
				ingredient.getId(),
				ingredient.getName(),
				ingredient.getReading(),
				ingredient.getDefaultUnit(),
				ingredientCategory == null ? null : ingredientCategory.getId(),
				ingredientCategory == null ? null : ingredientCategory.getCategoryName(),
				ingredientCategory == null ? null : ingredientCategory.getReading(),
				stock.getUser().getId(),
				stock.getQuantity(),
				stock.getExpirationDate()
				);
	}
}
