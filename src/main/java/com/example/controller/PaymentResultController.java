package com.example.controller;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.example.service.MomoService;
import com.example.service.VNpayService;

@Controller
public class PaymentResultController {
    @Autowired private VNpayService vnpayService;
    @Autowired private MomoService momoService;

    @GetMapping("/payment-result")
    public String paymentResult(@RequestParam Map<String, String> params, Model model) {
        boolean momo = params.containsKey("resultCode");
        boolean valid = momo ? momoService.verifyCallback(params) : vnpayService.verifyVnpayResponse(params);
        if (valid) { if (momo) momoService.markPaymentResult(params); else vnpayService.markPaymentResult(params); }
        String code = momo ? params.get("resultCode") : params.get("vnp_ResponseCode");
        boolean success = valid && (momo ? "0".equals(code) : "00".equals(code));

        // Sau khi backend đã xác thực và lưu trạng thái thanh toán, chuyển người dùng
        // về đúng trang chi tiết đơn hàng thay vì dừng ở trang trung gian.
        if (success) {
            String orderId = momo ? params.get("orderId") : params.get("vnp_TxnRef");
            if (orderId != null && orderId.matches("\\d+")) {
                return "redirect:/order/confirmation/" + orderId + "?payment=success";
            }
        }

        model.addAttribute("isValid", valid);
        model.addAttribute("isSuccess", success);
        return "order/payment-result";
    }
}
