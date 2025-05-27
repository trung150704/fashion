package com.example.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.service.VNpayService;

@Controller
public class PaymentResultController {

	@Autowired
	VNpayService vnpayService;

	@GetMapping("/payment-result")
	public String paymentResult(@RequestParam Map<String, String> params, Model model) {
		boolean isValid = vnpayService.verifyVnpayResponse(params);
		String responseCode = params.get("vnp_ResponseCode"); // "00" = thành công
		model.addAttribute("isValid", isValid);
		model.addAttribute("isSuccess", isValid && "00".equals(responseCode));
		return "payment-result"; // Trả về trang HTML để frontend xử lý tiếp
	}
}
