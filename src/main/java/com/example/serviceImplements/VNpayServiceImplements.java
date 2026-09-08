package com.example.serviceImplements;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.example.entity.Order;
import com.example.service.OrderService;
import com.example.service.VNpayService;

@Service
public class VNpayServiceImplements implements VNpayService {
    private final OrderService orderService;
    private final String tmnCode, hashSecret, paymentUrl, baseUrl;

    public VNpayServiceImplements(OrderService orderService,
            @Value("${app.payment.vnpay.tmn-code:}") String tmnCode,
            @Value("${app.payment.vnpay.hash-secret:}") String hashSecret,
            @Value("${app.payment.vnpay.url}") String paymentUrl,
            @Value("${app.payment.base-url}") String baseUrl) {
        this.orderService = orderService; this.tmnCode = tmnCode; this.hashSecret = hashSecret;
        this.paymentUrl = paymentUrl; this.baseUrl = baseUrl;
    }

    @Override public String generatePaymentUrl(Order order) {
        if (tmnCode.isBlank() || hashSecret.isBlank()) throw new IllegalStateException("VNPay chưa được cấu hình");
        Map<String, String> p = new TreeMap<>();
        p.put("vnp_Version", "2.1.0"); p.put("vnp_Command", "pay"); p.put("vnp_TmnCode", tmnCode);
        p.put("vnp_Amount", String.valueOf(order.getTotal_price().longValue() * 100)); p.put("vnp_CurrCode", "VND");
        p.put("vnp_TxnRef", String.valueOf(order.getId())); p.put("vnp_OrderInfo", "Thanh toan don hang " + order.getId());
        p.put("vnp_OrderType", "other"); p.put("vnp_Locale", "vn"); p.put("vnp_ReturnUrl", baseUrl + "/payment-result");
        p.put("vnp_IpAddr", "127.0.0.1");
        LocalDateTime now = LocalDateTime.now();
        p.put("vnp_CreateDate", now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
        p.put("vnp_ExpireDate", now.plusMinutes(15).format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
        // VNPay Java sample ký trên chính chuỗi field/value đã URL-encode.
        String encodedQuery = queryString(p, true);
        return paymentUrl + "?" + encodedQuery + "&vnp_SecureHash=" + hmac(hashSecret, encodedQuery);
    }

    @Override public boolean verifyVnpayResponse(Map<String, String> params) {
        String received = params.get("vnp_SecureHash"); if (received == null || hashSecret.isBlank()) return false;
        SortedMap<String, String> p = new TreeMap<>(params); p.remove("vnp_SecureHash"); p.remove("vnp_SecureHashType");
        return hmac(hashSecret, queryString(p, true)).equalsIgnoreCase(received);
    }

    @Override public void markPaymentResult(Map<String, String> params) {
        if (!verifyVnpayResponse(params)) return;
        try { orderService.findById(Integer.valueOf(params.get("vnp_TxnRef"))).ifPresent(o -> {
            o.setStatus("00".equals(params.get("vnp_ResponseCode")) && "00".equals(params.get("vnp_TransactionStatus")) ? "paid" : "payment_failed");
            orderService.save(o);
        }); } catch (NumberFormatException ignored) { }
    }

    private String queryString(Map<String, String> p, boolean encode) {
        StringBuilder b = new StringBuilder();
        p.forEach((k, v) -> { if (v != null && !v.isBlank()) { if (b.length() > 0) b.append('&'); b.append(encode(k, encode)).append('=').append(encode(v, encode)); } });
        return b.toString();
    }
    private String encode(String s, boolean enabled) { return enabled ? URLEncoder.encode(s, StandardCharsets.UTF_8) : s; }
    private String hmac(String key, String data) {
        try { Mac mac = Mac.getInstance("HmacSHA512"); mac.init(new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512"));
            StringBuilder b = new StringBuilder(); for (byte x : mac.doFinal(data.getBytes(StandardCharsets.UTF_8))) b.append(String.format("%02x", x)); return b.toString();
        } catch (Exception e) { throw new IllegalStateException("Không tạo được chữ ký VNPay", e); }
    }
}
