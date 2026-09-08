package com.example.RestController;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.service.VNpayService;

/** Endpoint VNPay gọi server-to-server để cập nhật trạng thái giao dịch. */
@RestController
@RequestMapping("/payment/vnpay")
public class VNPayIpnRestController {
    private final VNpayService vnpayService;

    public VNPayIpnRestController(VNpayService vnpayService) {
        this.vnpayService = vnpayService;
    }

    @GetMapping("/ipn")
    public Map<String, String> ipn(@RequestParam Map<String, String> params) {
        if (!vnpayService.verifyVnpayResponse(params)) {
            return Map.of("RspCode", "97", "Message", "Fail checksum");
        }
        vnpayService.markPaymentResult(params);
        return Map.of("RspCode", "00", "Message", "Confirm Success");
    }
}
