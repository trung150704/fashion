package com.example.serviceImplements;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.entity.PasswordResetToken;
import com.example.entity.User;
import com.example.repository.PasswordResetTokenRepository;
import com.example.repository.UserRepository;
import com.example.service.EmailService;
import com.example.service.PasswordResetService;

@Service
public class PasswordResetServiceImplements implements PasswordResetService{

	@Autowired
    private PasswordResetTokenRepository tokenRepo;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    public String sendResetToken(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) return "Không tìm thấy người dùng";

        User user = userOpt.get();

        // Kiểm tra token cũ (nếu có)
        Optional<PasswordResetToken> oldTokenOpt = tokenRepo.findByUser_Email(email);
        if (oldTokenOpt.isPresent()) {
            PasswordResetToken oldToken = oldTokenOpt.get();
            if (oldToken.getCreatedAt().isAfter(LocalDateTime.now().minusMinutes(1))) {
                return "Bạn chỉ có thể yêu cầu mã sau 1 phút.";
            }
            tokenRepo.delete(oldToken);
        }

        // Tạo mã mới
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setUser(user);
        resetToken.setToken(token);
        resetToken.setCreatedAt(LocalDateTime.now());
        resetToken.setExpiresAt(LocalDateTime.now().plusMinutes(15));

        tokenRepo.save(resetToken);

        // Gửi email
        String link = "http://localhost:8080/reset-password?token=" + token;
        emailService.sendEmail(email, "Đặt lại mật khẩu", "Bấm vào link để đặt lại: " + link);

        return "Đã gửi email đặt lại mật khẩu.";
    }

    public boolean isValidToken(String token) {
        Optional<PasswordResetToken> tokenOpt = tokenRepo.findByToken(token);
        if (tokenOpt.isEmpty()) return false;

        PasswordResetToken resetToken = tokenOpt.get();
        if (resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            tokenRepo.delete(resetToken); // Tự động xóa
            return false;
        }

        return true;
    }

    @Override
    public Optional<PasswordResetToken> findByToken(String token) {
        return tokenRepo.findByToken(token);
    }

    @Override
    public Optional<PasswordResetToken> findByUser_Email(String email) {
        return tokenRepo.findByUser_Email(email);
    }

	@Override
	public void deleteToken(PasswordResetToken token) {
	    tokenRepo.delete(token);
	}

}
