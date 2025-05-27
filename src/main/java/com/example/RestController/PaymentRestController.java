package com.example.RestController;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.entity.Order;
import com.example.service.VNpayService;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/payment")
public class PaymentRestController {

    @Autowired
    private VNpayService vnpayService;

    @PostMapping("/create")
    public void createPayment(@RequestBody Order order, HttpServletResponse response) throws IOException {
        String redirectUrl = vnpayService.generatePaymentUrl(order);
        response.sendRedirect(redirectUrl);
    }
}