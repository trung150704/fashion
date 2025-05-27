package com.example.RestController;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.service.GhnService;

@RestController
@RequestMapping("/ghn")
public class GhnRestController {
    @Autowired private GhnService ghnService;

    @GetMapping("/provinces")
    public List<Map<String, Object>> getProvinces() {
        return ghnService.getProvinces();
    }

    @PostMapping("/districts")
    public List<Map<String, Object>> getDistricts(@RequestBody Map<String, Integer> body) {
        return ghnService.getDistricts(body.get("province_id"));
    }

    @PostMapping("/wards")
    public List<Map<String, Object>> getWards(@RequestBody Map<String, Integer> body) {
        return ghnService.getWards(body.get("district_id"));
    }

    @PostMapping("/fee")
    public Integer getFee(@RequestBody Map<String, Object> body) {
        int toDistrict = (int) body.get("to_district_id");
        String wardCode = (String) body.get("to_ward_code");
        int weight = (int) body.get("weight");
        return ghnService.calculateShippingFee(toDistrict, wardCode, weight);
    }

    @PostMapping("/order-status")
    public Map<String, Object> getStatus(@RequestBody Map<String, String> body) {
        return ghnService.getOrderStatus(body.get("order_code"));
    }
}

