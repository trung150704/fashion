package com.example.serviceImplements;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.entity.Cart;
import com.example.repository.CartRepository;
import com.example.service.CartService;

import jakarta.transaction.Transactional;

@Service
public class CartServiceImplements implements CartService{

	@Autowired
	CartRepository cartRepository;
	
	@Override
	public List<Cart> findByUserId(Integer userId) {
		return cartRepository.findByUserId(userId);
	}

	@Override
	@Transactional
    public void deleteByUserId(Integer userId) {
        cartRepository.deleteByUserId(userId);
    }
}
