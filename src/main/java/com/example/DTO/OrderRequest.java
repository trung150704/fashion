package com.example.DTO;

import java.util.List;

import lombok.Data;

@Data
public class OrderRequest {
    private String fullName;
    private String phone;
    private String address;
    private String paymentMethod;
    private double shippingFee;
    private List<CartItem> cart;

    private boolean paid;
    @Data
    public static class CartItem {
        private Integer productId;
        private Integer sizeId;
        private int quantity;
        private double price;
    }
}

