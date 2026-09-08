package com.example.RestController;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.entity.Order;
import com.example.service.VNpayService;
import com.example.service.MomoService;
import java.util.Map;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/payment")
public class PaymentRestController {

    @Autowired
    private VNpayService vnpayService;
    @Autowired
    private MomoService momoService;

    @PostMapping("/create")
    public void createPayment(@RequestBody Order order, HttpServletResponse response) throws IOException {
        String redirectUrl = vnpayService.generatePaymentUrl(order);
        response.sendRedirect(redirectUrl);
    }

    @PostMapping("/momo/create")
    public void createMomoPayment(@RequestBody Order order, HttpServletResponse response) throws IOException {
        response.sendRedirect(momoService.createPaymentUrl(order));
    }

    @PostMapping("/momo/ipn")
    public Map<String, String> momoIpn(@RequestBody Map<String, String> params) {
        momoService.markPaymentResult(params);
        return Map.of("resultCode", "0", "message", "success");
    }
}
