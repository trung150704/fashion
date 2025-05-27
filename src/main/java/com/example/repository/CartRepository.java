package com.example.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.entity.Cart;

@Repository
public interface CartRepository extends JpaRepository<Cart, Integer> {

	Cart findByUserIdAndProductIdAndSizeId(int userId, int productId, int sizeId);

	List<Cart> findByUserId(Integer userId);

	void deleteByUserId(Integer userId);
}
