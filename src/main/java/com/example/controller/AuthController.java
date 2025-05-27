package com.example.controller;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.entity.PasswordResetToken;
import com.example.entity.User;
import com.example.service.EmailService;
import com.example.service.PasswordResetService;
import com.example.service.UserService;

import jakarta.validation.Valid;

@Controller
public class AuthController {
	
	@Autowired
	private UserService userService;

	@Autowired
    private PasswordEncoder passwordEncoder;
	
	@Autowired
	private PasswordResetService passwordResetService;
	
	@Autowired
	private EmailService emailService;
	
	private boolean isStrongPassword(String password) {
	    return password.length() >= 6 &&
	           password.matches(".*[A-Z].*") &&     // có chữ hoa
	           password.matches(".*[a-z].*") &&     // có chữ thường
	           password.matches(".*\\d.*") &&       // có số
	           password.matches(".*[@#$%^&+=!].*"); // có ký tự đặc biệt
	}

	
	@RequestMapping("/login")
	public String login(Model model) {
		model.addAttribute("pageTitle", "Đăng nhập");
		return "auth/login";

	}

	@GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User()); // truyền object để binding
        model.addAttribute("pageTitle", "Đăng kí");
        return "auth/register";
    }

    @PostMapping("/register")
    public String processRegister (@Valid @ModelAttribute("user") User user,BindingResult result, Model model) {
    	if (userService.existsByEmail(user.getEmail())) {
            result.rejectValue("email", "error.user", "Email đã được sử dụng");
        }
    	if (result.hasErrors()) {
            return "auth/register"; // quay lại form nếu lỗi
        }
    	user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("USER");
        user.setCreated_at(LocalDateTime.now());
        userService.save(user); // Tự động mã hóa trong UserServiceImplements
        model.addAttribute("pageTitle", "Đăng kí");
        return "redirect:/login"; // Đăng ký xong chuyển về login
    }

	@GetMapping("/forgotPw")
	public String forgotPw(Model model) {
		model.addAttribute("pageTitle", "Quên mật khẩu");
		return "auth/forgotPw";

	}
	
	@RequestMapping("/chanePw")
	public String chanePw(Model model) {
		model.addAttribute("pageTitle", "Đổi mật khẩu");
		return "auth/chanePw";

	}
	@PostMapping("/forgotPw")
	public String processForgotPassword(@RequestParam("email") String email, Model model) {
	    String message = passwordResetService.sendResetToken(email);
	    model.addAttribute("message", message);
	    model.addAttribute("pageTitle", "Quên mật khẩu");
	    return "auth/forgotPw";
	}

	@GetMapping("/reset-password")
	public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
	    if (!passwordResetService.isValidToken(token)) {
	        model.addAttribute("error", "Mã không hợp lệ hoặc đã hết hạn.");
	        return "auth/chanePw";
	    }
	    model.addAttribute("token", token);
	    return "auth/chanePw";
	}

	@PostMapping("/reset-password")
	public String processResetPassword(
	        @RequestParam("token") String token,
	        @RequestParam("newPassword") String newPassword,
	        @RequestParam("confirmPassword") String confirmPassword,
	        Model model) {

	    if (!newPassword.equals(confirmPassword)) {
	        model.addAttribute("error", "Mật khẩu không khớp.");
	        model.addAttribute("token", token);
	        return "auth/chanePw";
	    }

	    if (!isStrongPassword(newPassword)) {
	        model.addAttribute("error", "Mật khẩu yếu. Cần ít nhất 6 ký tự, gồm chữ hoa, số và ký tự đặc biệt.");
	        model.addAttribute("token", token);
	        return "auth/chanePw";
	    }

	    Optional<PasswordResetToken> tokenOpt = passwordResetService.findByToken(token);
	    if (tokenOpt.isEmpty()) {
	        model.addAttribute("error", "Mã không hợp lệ hoặc đã hết hạn.");
	        return "auth/chanePw";
	    }

	    User user = tokenOpt.get().getUser();
	    user.setPassword(passwordEncoder.encode(newPassword));
	    userService.save(user);
	    passwordResetService.deleteToken(tokenOpt.get());

	    // Gửi email thông báo
	    emailService.sendEmail(user.getEmail(), "Đổi mật khẩu thành công",
	            "Bạn đã đổi mật khẩu vào lúc: " + LocalDateTime.now());

	    return "redirect:/login?resetSuccess";
	}
	@GetMapping("/change-password")
	public String showChangePasswordForm(Model model) {
	    model.addAttribute("pageTitle", "Đổi mật khẩu");
	    return "auth/changePassword";
	}

	@PostMapping("/change-password")
	public String processChangePassword(
	        @RequestParam("currentPassword") String currentPassword,
	        @RequestParam("newPassword") String newPassword,
	        @RequestParam("confirmPassword") String confirmPassword,
	        Model model,
	        @AuthenticationPrincipal org.springframework.security.core.userdetails.User loggedUser) {

	    User user = userService.findByUsername(loggedUser.getUsername());

	    if (user == null) {
	        model.addAttribute("error", "Không tìm thấy người dùng.");
	        return "auth/changePassword";
	    }

	    if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
	        model.addAttribute("error", "Mật khẩu hiện tại không đúng.");
	        return "auth/changePassword";
	    }

	    if (!newPassword.equals(confirmPassword)) {
	        model.addAttribute("error", "Mật khẩu mới không khớp.");
	        return "auth/changePassword";
	    }

	    if (!isStrongPassword(newPassword)) {
	        model.addAttribute("error", "Mật khẩu yếu. Cần ít nhất 6 ký tự, gồm chữ hoa, số và ký tự đặc biệt.");
	        return "auth/changePassword";
	    }

	    user.setPassword(passwordEncoder.encode(newPassword));
	    userService.save(user);

	    emailService.sendEmail(user.getEmail(), "Bạn đã đổi mật khẩu",
	            "Bạn đã đổi mật khẩu lúc " + LocalDateTime.now());

	    model.addAttribute("success", "Đổi mật khẩu thành công!");
	    return "auth/changePassword";
	}


}
