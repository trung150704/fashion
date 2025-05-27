package com.example.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@NotBlank(message = "Tên sản phẩm không được để trống")
	private String name;
	
	@NotBlank(message = "Mô tả không được để trống")
	private String description;
	
	@NotNull(message = "Giá không được để trống")
	@DecimalMin(value = "0.0", inclusive = false, message = "Giá phải lớn hơn 0")
	private Double price;
	
	@NotNull(message = "Số lượng không được để trống")
	@Min(value = 0, message = "Số lượng phải >= 0")
	private Integer stock_quantity;
	
    private LocalDateTime created_at;
    
    private String image;
	@ManyToOne
	@JoinColumn(name = "category_id")
	private Category category;
}
