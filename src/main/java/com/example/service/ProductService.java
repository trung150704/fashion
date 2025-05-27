package com.example.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.entity.Product;

public interface ProductService {
	Product findById (Integer id);
	List<Product> findAll();
	List<Product> findByNameContainingIgnoreCase(String keyword);
	List<Product> findByCategoryId(Integer categoryId);
	//Page<Product> findByCategoryIdPaged(Integer id, Pageable pageable);
	Page<Product> findRelatedProductsExcludeCurrent(Integer categoryId, Integer currentProductId, Pageable pageable);
	Page<Product> getProductsByPage(int pageNumber, int pageSize);
	Product save(Product product);
	void deleteById(Integer id);
	List<Product> searchByName(String keyword);
	Page<Product> filterProducts(String keyword, BigDecimal min, BigDecimal max, int page);
}
