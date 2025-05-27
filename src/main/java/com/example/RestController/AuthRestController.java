package com.example.RestController;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthRestController {
	@GetMapping("/check")
	public ResponseEntity<?> checkLogin(Principal principal) {
	    if (principal == null) {
	        return ResponseEntity.status(401).body("Chưa đăng nhập");
	    }
	    return ResponseEntity.ok("Đã đăng nhập");
	}

}
