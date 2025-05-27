package com.example.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GhnService {
    private static final String TOKEN = "7d0ed6b7-96b2-11ef-b17f-4603c5b85b86";
    private static final int SHOP_ID = 5424124; // dạng int
    private static final int FROM_DISTRICT_ID = 1442; // nơi gửi hàng

    @Autowired
    private RestTemplate restTemplate;

    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Token", TOKEN);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    public List<Map<String, Object>> getProvinces() {
        String url = "https://online-gateway.ghn.vn/shiip/public-api/master-data/province";
        ResponseEntity<Map> res = restTemplate.exchange(
                url, HttpMethod.GET, new HttpEntity<>(defaultHeaders()), Map.class);
        return (List<Map<String, Object>>) res.getBody().get("data");
    }

    public List<Map<String, Object>> getDistricts(int provinceId) {
        String url = "https://online-gateway.ghn.vn/shiip/public-api/master-data/district";
        Map<String, Integer> body = Map.of("province_id", provinceId);
        ResponseEntity<Map> res = restTemplate.exchange(
                url, HttpMethod.POST, new HttpEntity<>(body, defaultHeaders()), Map.class);
        return (List<Map<String, Object>>) res.getBody().get("data");
    }

    public List<Map<String, Object>> getWards(int districtId) {
        String url = "https://online-gateway.ghn.vn/shiip/public-api/master-data/ward";
        Map<String, Integer> body = Map.of("district_id", districtId);
        ResponseEntity<Map> res = restTemplate.exchange(
                url, HttpMethod.POST, new HttpEntity<>(body, defaultHeaders()), Map.class);
        return (List<Map<String, Object>>) res.getBody().get("data");
    }

    public Integer calculateShippingFee(int toDistrict, String wardCode, int weight) {
        String url = "https://online-gateway.ghn.vn/shiip/public-api/v2/shipping-order/fee";
        Map<String, Object> body = new HashMap<>();
        body.put("from_district_id", FROM_DISTRICT_ID);
        body.put("to_district_id", toDistrict);
        body.put("to_ward_code", wardCode);
        body.put("weight", weight);
        body.put("service_type_id", 2); // dịch vụ tiêu chuẩn

        HttpHeaders headers = defaultHeaders();
        headers.set("ShopId", String.valueOf(SHOP_ID));

        ResponseEntity<Map> res = restTemplate.exchange(
                url, HttpMethod.POST, new HttpEntity<>(body, headers), Map.class);
        Map<String, Object> data = (Map<String, Object>) res.getBody().get("data");
        return (Integer) data.get("total");
    }

    public Map<String, Object> getOrderStatus(String orderCode) {
        String url = "https://online-gateway.ghn.vn/shiip/public-api/v2/shipping-order/detail";
        Map<String, String> body = Map.of("order_code", orderCode);
        ResponseEntity<Map> res = restTemplate.exchange(
                url, HttpMethod.POST, new HttpEntity<>(body, defaultHeaders()), Map.class);
        return (Map<String, Object>) res.getBody().get("data");
    }
    
    
}

