package com.example.controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.DTO.OrderRequest;
import com.example.entity.Order;
import com.example.entity.OrderDetail;
import com.example.entity.Product;
import com.example.entity.Size;
import com.example.entity.User;
import com.example.service.CartService;
import com.example.service.OrderDetailService;
import com.example.service.OrderService;
import com.example.service.ProductService;
import com.example.service.SizeService;
import com.example.service.UserService;
import com.example.service.VNpayService;
import com.example.service.MomoService;


@Controller
public class OrderController {

    @Autowired
    UserService userService;

    @Autowired
    ProductService productService;

    @Autowired
    OrderDetailService orderDetailService;

    @Autowired
    OrderService orderService;

    @Autowired
    SizeService sizeService;
    
    @Autowired
    CartService cartService;
    @Autowired
    VNpayService vnpayService;
    @Autowired
    MomoService momoService;
    
    @PostMapping("/order")
    public ResponseEntity<?> createOrder(@RequestBody OrderRequest request, Principal principal) {
    	if (request.getCart() == null || request.getCart().isEmpty()) {
    	    return ResponseEntity.badRequest().body(Map.of("message", "Giỏ hàng trống! Không thể đặt hàng."));
    	}

        try {

            
            Order order = new Order();
            User user = null;
            if (principal != null) {
                user = userService.findByUsername(principal.getName());
                order.setUser(user); // chỉ set nếu đăng nhập
            }
            order.setOrder_date(LocalDateTime.now());
            order.setStatus("pending");
            order.setAddress(request.getAddress());
            order.setPayment_method(request.getPaymentMethod());
            order.setRecipient_name(request.getFullName());
            order.setRecipient_phone(request.getPhone());
            double total = 0.0;

            for (OrderRequest.CartItem item : request.getCart()) {
                Product product = productService.findById(item.getProductId());
                Size size = sizeService.findById(item.getSizeId());
                if (product == null || size == null) {
                    System.out.println("Không tìm thấy product hoặc size: productId=" + item.getProductId() + ", sizeId=" + item.getSizeId());
                    continue;
                }

                OrderDetail detail = new OrderDetail();
                detail.setProduct(product);
                detail.setQuantity(item.getQuantity());
                if (item.getQuantity() <= 0) {
                    return ResponseEntity.badRequest().body(Map.of("message", "Số lượng sản phẩm không hợp lệ"));
                }
                detail.setPrice(product.getPrice());
                detail.setSize(size);
                
                order.addOrderDetail(detail); // ✅ dùng phương thức 2 chiều

                total += product.getPrice() * item.getQuantity();
            }

            total += Math.max(0, request.getShippingFee());
            order.setTotal_price(total);
            if ("Thanh toán bằng Ví điện tử/ QR Code".equals(request.getPaymentMethod()) && request.isPaid()) {
                order.setStatus("paid"); // hoặc "confirmed"
            } else {
                order.setStatus("pending");
            }
            orderService.save(order);
            if (user != null) {
                cartService.deleteByUserId(user.getId());
                System.out.println("Đã xoá giỏ hàng của user ID: " + user.getId());
            }
            
            String paymentUrl = null;
            String payment = request.getPaymentMethod() == null ? "" : request.getPaymentMethod().toLowerCase();
            if (payment.contains("vnpay")) paymentUrl = vnpayService.generatePaymentUrl(order);
            else if (payment.contains("momo")) paymentUrl = momoService.createPaymentUrl(order);
            Map<String, Object> result = new java.util.HashMap<>();
            result.put("message", "Đặt hàng thành công"); result.put("orderId", order.getId());
            if (paymentUrl != null) result.put("paymentUrl", paymentUrl);
            return ResponseEntity.ok().body(result);
        } catch (Exception e) {
            e.printStackTrace(); // ⚠️ In ra log lỗi thực tế
            return ResponseEntity.status(500).body("Đặt hàng thất bại: " + e.getMessage());
        }
    }
    
    @GetMapping("/order-list")
    public String listOrders(Model model, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        List<Order> orders = orderService.findByUser(user);
        model.addAttribute("pageTitle","Đơn hàng");
        model.addAttribute("orders", orders);
        return "order/list";
    }


    // Chi tiết đơn hàng theo id
    @GetMapping("/order-list/{id}")
    public String orderDetail(@PathVariable("id") Integer id, Model model) {
    	Optional<Order> orderOpt = orderService.findById(id);
        if (orderOpt.isEmpty()) {
            return "redirect:/order-list"; // hoặc trang lỗi
        }
        Order order = orderOpt.get();
        model.addAttribute("order", order);
        model.addAttribute("pageTitle", "Chi Tiết Đơn hàng " + order.getId());
        return "order/detail";
    }
    
    @GetMapping("/order/confirmation/{id}")
    public String orderConfirmation(@PathVariable("id") Integer orderId, Model model) {
        Optional<Order> orderOpt = orderService.findById(orderId);
        if (orderOpt.isEmpty()) {
            return "redirect:/"; // hoặc về trang chính nếu không có đơn
        }
        Order order = orderOpt.get();
        model.addAttribute("pageTitle", "Xác nhận đơn hàng");
        model.addAttribute("order", order);
        return "order/confirmation";
    }
    
    @GetMapping("/order/lookup")
    public String lookupOrder(@RequestParam(value = "orderId", required = false) Integer orderId, Model model) {
        if (orderId != null) {
            Optional<Order> orderOpt = orderService.findById(orderId);
            if (orderOpt.isPresent()) {
                model.addAttribute("order", orderOpt.get());
                model.addAttribute("notFound", false);
            } else {
                model.addAttribute("notFound", true);
            }
            model.addAttribute("searchedId", orderId);
        }
        model.addAttribute("pageTitle","Tra cứu đơn hàng");
        return "order/lookup"; // Trang tra cứu đơn
    }

    @GetMapping("/qrcode")
    public String qrCheckout(Model model, Principal principal) {
        model.addAttribute("pageTitle","Thanh toán mã QR");
        return "order/qrcode";
    }
}
