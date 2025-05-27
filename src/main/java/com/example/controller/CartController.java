package com.example.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.entity.Payment;
import com.example.service.PaymentService;

@Controller
public class CartController {
	@Autowired PaymentService paymentService;
	
	@RequestMapping("/cart")
	public String cart(Model model) {
		model.addAttribute("pageTitle", "Giỏ hàng");
		return "cart/view";

	}

	@RequestMapping("/favorite")
	public String favorite(Model model) {
		model.addAttribute("pageTitle", "Sản phẩm yêu thích");
		return "cart/favorite";

	}
	@RequestMapping("/checkout")
	public String checkout(Model model) {
		List<Payment> payment = paymentService.findAll();
		model.addAttribute("payment",payment);
		model.addAttribute("pageTitle", "Thanh Toán");
		return "cart/checkout";

	}
}
