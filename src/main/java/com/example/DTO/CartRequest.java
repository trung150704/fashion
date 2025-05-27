package com.example.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartRequest {
	private int productId;
	private int sizeId;
	private int quantity;

	private String productName;

	private int price;
	private String image;
	private String sizeName;
}
