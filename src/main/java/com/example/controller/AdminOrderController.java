package com.example.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.example.entity.Order;
import com.example.service.OrderService;
import com.example.service.UserService;

@Controller
@RequestMapping("/admin/orders")
public class AdminOrderController {

	@Autowired
	private OrderService orderService;

	@Autowired
	private UserService userService;

	@GetMapping("")
	public String orders(Model model) {
		List<Order> orders = orderService.findAll();
		model.addAttribute("orders", orders);
		model.addAttribute("pageTitle", "Quản Lý Đơn Hàng");
		return "admin/order/orders";
	}

	@GetMapping("/edit/{id}")
	public String editOrder(@PathVariable Integer id, Model model) {
		Optional<Order> optionalOrder = orderService.findById(id);
		if (optionalOrder.isEmpty()) {
			return "redirect:/admin/orders"; // hoặc trả về trang báo lỗi
		}

		model.addAttribute("orders", optionalOrder.get());
		model.addAttribute("users", userService.findAll());
		model.addAttribute("pageTitle","Chỉnh sửa đơn hàng: " + id);
		return "admin/order/edit";
	}

	@PostMapping("/edit/{id}")
	public String updateOrder(@PathVariable Integer id,
	                          @ModelAttribute("orders") Order updatedOrder,
	                          RedirectAttributes redirectAttributes) {

		Optional<Order> optionalOrder = orderService.findById(id);
		if (optionalOrder.isEmpty()) {
			redirectAttributes.addFlashAttribute("message", "Đơn hàng không tồn tại.");
			return "redirect:/admin/orders";
		}

		Order existingOrder = optionalOrder.get();

		// ⚠ Nếu có thay đổi user, thì phải lấy lại User từ DB
		if (updatedOrder.getUser() != null) {
			existingOrder.setUser(updatedOrder.getUser()); // Hoặc tìm userService.findById(...)
		}

		// ✅ Cập nhật từng trường cần thay đổi
		existingOrder.setStatus(updatedOrder.getStatus());
		existingOrder.setOrder_date(updatedOrder.getOrder_date());
		existingOrder.setTotal_price(updatedOrder.getTotal_price());
		existingOrder.setAddress(updatedOrder.getAddress());
		existingOrder.setRecipient_name(updatedOrder.getRecipient_name());
		existingOrder.setRecipient_phone(updatedOrder.getRecipient_phone());
		existingOrder.setPayment_method(updatedOrder.getPayment_method());

		// ⚠️ Không cập nhật orderDetails nếu không có ý định sửa
		// Nếu cần sửa orderDetails thì phải làm riêng, không làm trong form này

		orderService.save(existingOrder);

		redirectAttributes.addFlashAttribute("message", "Cập nhật đơn hàng thành công!");
		return "redirect:/admin/orders";
	}


	@GetMapping("/delete/{id}")
	public String deleteOrder(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
		orderService.deleteById(id);
		redirectAttributes.addFlashAttribute("message", "Xóa đơn hàng thành công!");
		return "redirect:/admin/orders";
	}
}
