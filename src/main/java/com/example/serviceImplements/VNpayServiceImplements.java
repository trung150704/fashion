package com.example.serviceImplements;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import javax.crypto.Mac;
import org.springframework.stereotype.Service;

import com.example.entity.Order;
import com.example.service.VNpayService;

@Service
public class VNpayServiceImplements implements VNpayService {

	@Override
	public String generatePaymentUrl(Order order) {
		String vnp_Version = "2.1.0";
		String vnp_Command = "pay";
		String vnp_TmnCode = "G1JFP6JX"; // 👉 Mã của bạn cấp bởi VNPay
		String vnp_HashSecret = "FBFU8D7TOPMJDKIQ6TESCKE5YRQFYUD4"; // 👉 Khóa bí mật
		String vnp_Url = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
		String vnp_ReturnUrl = "http://localhost:8080/order-list/" + order.getId();

		String vnp_TxnRef = String.valueOf(order.getId());
		String vnp_OrderInfo = "Thanh toan don hang #" + order.getId();
		String vnp_Amount = String.valueOf((long) (order.getTotal_price() * 100)); // Nhân 100
		String vnp_Locale = "vn";
		String vnp_CurrCode = "VND";
		String vnp_IpAddr = "127.0.0.1";
		String vnp_CreateDate = java.time.LocalDateTime.now()
				.format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

		Map<String, String> vnp_Params = new TreeMap<>();
		vnp_Params.put("vnp_Version", vnp_Version);
		vnp_Params.put("vnp_Command", vnp_Command);
		vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
		vnp_Params.put("vnp_Amount", vnp_Amount);
		vnp_Params.put("vnp_CurrCode", vnp_CurrCode);
		vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
		vnp_Params.put("vnp_OrderInfo", vnp_OrderInfo);
		vnp_Params.put("vnp_Locale", vnp_Locale);
		vnp_Params.put("vnp_ReturnUrl", vnp_ReturnUrl);
		vnp_Params.put("vnp_IpAddr", vnp_IpAddr);
		vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

		// Bước 1: Tạo chuỗi hash
		StringBuilder hashData = new StringBuilder();
		StringBuilder query = new StringBuilder();
		for (Map.Entry<String, String> entry : vnp_Params.entrySet()) {
			hashData.append(entry.getKey()).append('=').append(entry.getValue()).append('&');
			query.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8)).append('=')
					.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8)).append('&');
		}
		hashData.setLength(hashData.length() - 1); // remove last &
		query.setLength(query.length() - 1); // remove last &

		// Bước 2: Tạo checksum
		String calculatedHash = hmacSHA512(vnp_HashSecret, hashData.toString());
		query.append("&vnp_SecureHash=").append(calculatedHash);

		return vnp_Url + "?" + query.toString();
	}
	
	public boolean verifyVnpayResponse(Map<String, String> params) {
		String receivedHash = params.get("vnp_SecureHash");
		params.remove("vnp_SecureHash");
		params.remove("vnp_SecureHashType");

		// Sắp xếp theo key tăng dần
		SortedMap<String, String> sortedParams = new TreeMap<>(params);
		StringBuilder hashData = new StringBuilder();
		for (Map.Entry<String, String> entry : sortedParams.entrySet()) {
			if (hashData.length() > 0) hashData.append('&');
			hashData.append(entry.getKey()).append('=').append(entry.getValue());
		}

		// Tính lại mã
		String calculatedHash = hmacSHA512(receivedHash, hashData.toString());
		return calculatedHash.equals(receivedHash);
	}

	private String hmacSHA512(String key, String data) {
		try {
			Mac hmac512 = Mac.getInstance("HmacSHA512");
			SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
			hmac512.init(secretKey);
			byte[] hashBytes = hmac512.doFinal(data.getBytes(StandardCharsets.UTF_8));
			StringBuilder result = new StringBuilder();
			for (byte b : hashBytes) {
				result.append(String.format("%02x", b));
			}
			return result.toString();
		} catch (Exception e) {
			throw new RuntimeException("Lỗi tạo mã HMAC", e);
		}
	}

}
