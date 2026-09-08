package com.example.service;
import java.util.Map;
import com.example.entity.Order;
public interface MomoService {
    String createPaymentUrl(Order order);
    boolean verifyCallback(Map<String, String> params);
    void markPaymentResult(Map<String, String> params);
}
