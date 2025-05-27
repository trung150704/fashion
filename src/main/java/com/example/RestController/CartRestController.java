package com.example.RestController;

import java.security.Principal;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.DTO.CartRequest;
import com.example.entity.Cart;
import com.example.entity.Size;
import com.example.entity.User;
import com.example.repository.CartRepository;
import com.example.repository.SizeRepository;
import com.example.repository.UserRepository;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class CartRestController {

    private final SizeRepository sizeRepository;

    @Autowired private CartRepository cartRepository;
    @Autowired private UserRepository userRepository;

    CartRestController(SizeRepository sizeRepository) {
        this.sizeRepository = sizeRepository;
    }
    @PostMapping("/add")
    public ResponseEntity<?> addToCart(@RequestBody CartRequest req, Principal principal) {
        if (principal == null) {
            return ResponseEntity.badRequest().body("Bạn chưa đăng nhập");
        }

        String username = principal.getName();
        User user = userRepository.findByUsername(username);

        Size size = sizeRepository.findById(req.getSizeId()).orElse(null);
        if (size == null) {
            return ResponseEntity.status(400).body("Size không tồn tại");
        }
        String sizeName = (size != null) ? size.getName() : "Không rõ";
        
        Cart existing = cartRepository.findByUserIdAndProductIdAndSizeId(
            user.getId(), req.getProductId(), req.getSizeId());
        
        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + req.getQuantity());
            existing.setSizeName(sizeName);
            cartRepository.save(existing);
        } else {
            Cart newItem = new Cart();
            newItem.setUserId(user.getId());
            newItem.setProductId(req.getProductId());
            newItem.setSizeId(req.getSizeId());
            newItem.setQuantity(req.getQuantity());
            newItem.setProductName(req.getProductName());
            newItem.setImage(req.getImage());
            newItem.setPrice(req.getPrice());
            newItem.setSizeName(sizeName);
            cartRepository.save(newItem);
        }

        return ResponseEntity.ok(Collections.singletonMap("message", "Đã thêm vào giỏ hàng"));

    }

    @PostMapping("/remove")
    public ResponseEntity<?> removeFromCart(@RequestBody CartRequest req, Principal principal) {
        if (principal == null) {
            return ResponseEntity.badRequest().body("Bạn chưa đăng nhập");
        }

        String username = principal.getName();
        User user = userRepository.findByUsername(username);

        Cart existing = cartRepository.findByUserIdAndProductIdAndSizeId(
            user.getId(), req.getProductId(), req.getSizeId());

        if (existing != null) {
            cartRepository.delete(existing);
        }

        return ResponseEntity.ok(Collections.singletonMap("message", "Đã xoá sản phẩm khỏi giỏ hàng"));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401)
                .body(Collections.singletonMap("message", "Chưa đăng nhập"));
        }
        User user = userRepository.findByUsername(principal.getName());
        return ResponseEntity.ok(user);
    }

    @GetMapping("/me/cart")
    public ResponseEntity<?> getMyCart(Principal principal) {
        if (principal == null) {
        	return ResponseEntity.status(401)
            .body(Collections.singletonMap("message", "Chưa đăng nhập"));
        }

        User user = userRepository.findByUsername(principal.getName());
        List<Cart> cartItems = cartRepository.findByUserId(user.getId());
        return ResponseEntity.ok(cartItems);
    }
}
