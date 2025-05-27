package com.example.service;

import java.util.List;

import com.example.entity.Cart;

public interface CartService {
	List<Cart> findByUserId(Integer userId);
	void deleteByUserId(Integer userId);
}
