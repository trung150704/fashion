package com.example.service;

import java.util.Optional;

import com.example.entity.PasswordResetToken;

public interface PasswordResetService {
	Optional<PasswordResetToken> findByUser_Email(String email);
    Optional<PasswordResetToken> findByToken(String token);
    void deleteToken(PasswordResetToken token);
	boolean isValidToken(String token);
	String sendResetToken(String email);

}
