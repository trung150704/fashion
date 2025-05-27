package com.example.serviceImplements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.example.service.EmailService;

@Service
public class EmailServiceImplements  implements EmailService{
	@Autowired
    private JavaMailSender mailSender;
	@Override
	public void sendEmail(String to, String subject, String text) {
		SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject(subject);
        msg.setText(text);
        mailSender.send(msg);
		
	}
	@Override
	public void sendPromotionEmail( String toEmail) {
		SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Chào mừng đến với T.T fashion bạn sẽ nhận nhiều ưu đãi đặc biệt từ shop của chúng tôi!");
        message.setText("Cảm ơn bạn đã đăng ký nhận ưu đãi. Giảm giá 90% cho đơn đầu tiên khi đặt hàng, dùng mã: WELCOME10");
        mailSender.send(message);
	}
}
