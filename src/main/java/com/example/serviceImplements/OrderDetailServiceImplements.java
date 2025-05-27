package com.example.serviceImplements;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.entity.OrderDetail;
import com.example.repository.OrderDetailRepository;
import com.example.service.OrderDetailService;

@Service
public class OrderDetailServiceImplements implements OrderDetailService{
	@Autowired
	OrderDetailRepository orderDetailRepository;
	@Override
	public OrderDetail findById(Integer id) {
		return orderDetailRepository.findById(id).orElse(null);
	}

	@Override
	public List<OrderDetail> findAll() {
		return orderDetailRepository.findAll();
	}

	@Override
	public OrderDetail save(OrderDetail orderDetail) {
		return orderDetailRepository.save(orderDetail);
	}
	
}
