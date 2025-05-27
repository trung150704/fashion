package com.example.service;

public interface EmailService {
	void sendEmail(String to, String subject, String text);
	void sendPromotionEmail(String toEmail);
}
