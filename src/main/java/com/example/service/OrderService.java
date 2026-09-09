package com.example.service;

import java.util.List;
import java.util.Optional;

import com.example.entity.Order;
import com.example.entity.User;

public interface OrderService {
	Optional<Order> findById(Integer id);
	List<Order> findAll();
	Order save(Order order);
	List<Order> findByUser(User user);
	void deleteById(Integer id);
	
	List<Object[]> getMonthlyRevenue();
    List<Object[]> getDailyRevenue();

	Optional<Order> findByOrderCodeAndRecipientPhone(String orderCode, String recipientPhone);
}
