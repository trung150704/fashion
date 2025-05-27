package com.example.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
	Optional<Product>  findById (Integer id);
	List<Product> findByNameContainingIgnoreCase(String keyword);
	List<Product> findByCategoryId(Integer categoryId);
	//Page<Product> findByCategoryId(Integer categoryId, Pageable pageable);
	Page<Product> findByCategoryIdAndIdNot(Integer categoryId, Integer excludeId, Pageable pageable);
	Page<Product> findAll(Pageable pageable);
	Product save(Product product);
	void deleteById(Integer id);
	
	@Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
	List<Product> searchByName(@Param("keyword") String keyword);

	// ProductRepository.java
	@Query("SELECT p FROM Product p WHERE "
		     + "(:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) "
		     + "AND (:min IS NULL OR p.price >= :min) "
		     + "AND (:max IS NULL OR p.price <= :max)")
	Page<Product> filterProducts(@Param("keyword") String keyword,
	                             @Param("min") BigDecimal min,
	                             @Param("max") BigDecimal max,
	                             Pageable pageable);

}
