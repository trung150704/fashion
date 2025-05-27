package com.example.service;

import java.util.List;
import java.util.Optional;

import com.example.entity.Payment;

public interface PaymentService {
	Optional<Payment> findById(Integer id);
	List<Payment> findAll();
}
