package com.example.service;

import java.util.Map;

import com.example.entity.Order;

public interface VNpayService {

	 String generatePaymentUrl(Order order);

	boolean verifyVnpayResponse(Map<String, String> params);

}
