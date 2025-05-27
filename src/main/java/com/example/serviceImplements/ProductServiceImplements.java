package com.example.serviceImplements;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.entity.Product;
import com.example.repository.ProductRepository;
import com.example.service.ProductService;

@Service
public class ProductServiceImplements implements ProductService{
	@Autowired
	ProductRepository productRepository;
	
	@Override
	public Product findById(Integer id) {
		return productRepository.findById(id).orElse(null);
	}

	@Override
	public List<Product> findAll() {
		return productRepository.findAll();
	}

	@Override
	public List<Product> findByNameContainingIgnoreCase(String keyword) {
		return productRepository.findByNameContainingIgnoreCase(keyword);
	}

	@Override
	public List<Product> findByCategoryId(Integer categoryId) {
		return productRepository.findByCategoryId(categoryId);
	}

	@Override
	public Page<Product> findRelatedProductsExcludeCurrent(Integer categoryId, Integer currentProductId,
			Pageable pageable) {
		return productRepository.findByCategoryIdAndIdNot(categoryId, currentProductId, pageable);
	}
	
	@Override
	public Page<Product> getProductsByPage(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return productRepository.findAll(pageable);
    }
//	@Override
//	public Page<Product> findByCategoryIdPaged(Integer categoryId, Pageable pageable) {
//		return productRepository.findByCategoryId(categoryId, pageable);
//	}

	@Override
	public Product save(Product product) {
		return productRepository.save(product);
	}

	@Override
	public void deleteById(Integer id) {
	    productRepository.deleteById(id);
	}

	@Override
	public List<Product> searchByName(String keyword) {
		return productRepository.searchByName(keyword);
	}

	@Override
	public Page<Product> filterProducts(String keyword, BigDecimal min, BigDecimal max, int page) {
	    Pageable pageable = PageRequest.of(page, 12);
	    return productRepository.filterProducts(keyword, min, max, pageable);
	}



}
