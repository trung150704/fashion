package com.example.serviceImplements;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.entity.Order;
import com.example.entity.User;
import com.example.repository.OrderRepository;
import com.example.service.OrderService;

@Service
public class OrderServiceImplements implements OrderService{
	@Autowired
	OrderRepository orderRepository;

	@Override
	public Optional<Order> findById(Integer id) {
		return orderRepository.findById(id);
	}

	@Override
	public List<Order> findAll() {
		return orderRepository.findAll();
	}

	@Override
	public Order save(Order order) {
		return orderRepository.save(order);
	}

	@Override
	public List<Order> findByUser(User user) {
		return orderRepository.findByUser(user);
	}

	@Override
	public void deleteById(Integer id) {
		orderRepository.deleteById(id);
	}

	@Override
    public List<Object[]> getMonthlyRevenue() {
        return orderRepository.getMonthlyRevenue();
    }

    @Override
    public List<Object[]> getDailyRevenue() {
        return orderRepository.getDailyRevenue();
    }

	@Override
	public Optional<Order> findByOrderCodeAndRecipientPhone(String orderCode, String recipientPhone) {
		return orderRepository.findByOrderCodeAndRecipientPhone(orderCode, recipientPhone);
	}
}
