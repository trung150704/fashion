package com.example.service;

import java.util.List;

import com.example.entity.OrderDetail;

public interface OrderDetailService {
	OrderDetail findById(Integer id);
	List<OrderDetail> findAll();
	OrderDetail save(OrderDetail orderDetail);
}
