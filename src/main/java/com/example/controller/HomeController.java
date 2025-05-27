package com.example.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.entity.Product;
import com.example.entity.PromotionEmail;
import com.example.repository.PromotionEmailRepository;
import com.example.service.EmailService;
import com.example.service.ProductService;

import jakarta.validation.Valid;

@Controller
public class HomeController {

	@Autowired
	private EmailService emailService;
	@Autowired
	private ProductService productService;
	@Autowired
	PromotionEmailRepository promotionEmailRepository;

	@GetMapping("/")
	public String homePage(Model model) {
		List<Product> products = productService.findAll();
		List<List<Product>> chunks = new ArrayList<>();
		int chunkSize = 4;
		for (int i = 0; i < products.size(); i += chunkSize) {
			chunks.add(products.subList(i, Math.min(products.size(), i + chunkSize)));
		}
		model.addAttribute("productChunks", chunks);
		model.addAttribute("pageTitle", "Fashion - Shop");
		return "home/homeContent";
	}

	@GetMapping("/blog")
	public String blog(Model model) {
		model.addAttribute("pageTitle", "Blog");
		return "home/blog";
	}

	@PostMapping("/subscribe")
	public String subscribe(@Valid @ModelAttribute("emailForm") PromotionEmail form, BindingResult bindingResult,
			RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			return "home/homeContent"; // nếu có lỗi, hiển thị lại form kèm lỗi
		}

		if (!promotionEmailRepository.existsByEmail(form.getEmail())) {
			PromotionEmail subscriber = new PromotionEmail();
			subscriber.setEmail(form.getEmail());
			promotionEmailRepository.save(subscriber);

			emailService.sendPromotionEmail(form.getEmail());
			redirectAttributes.addFlashAttribute("message", "Đăng ký thành công!");
			redirectAttributes.addFlashAttribute("success", true);
		} else {
			redirectAttributes.addFlashAttribute("message", "Email này đã đăng ký rồi.");
			redirectAttributes.addFlashAttribute("success", false);
		}

		return "redirect:/";
	}

}
