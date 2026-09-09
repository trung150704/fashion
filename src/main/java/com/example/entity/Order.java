package com.example.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "`Order`") // vì "Order" là từ khóa SQL
public class Order {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@Column(name = "order_code", unique = true, length = 24)
	private String orderCode;

	private LocalDateTime order_date;
	private String status;
	private Double total_price;
	private String address;
	private String payment_method;
	
	@NotBlank(message ="Vui lòng nhận tên người nhận")
	private String recipient_name;
	@NotBlank(message ="Vui lòng nhận số điện thoại người nhận")
	private String recipient_phone;
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<OrderDetail> orderDetails = new ArrayList<>();

	public void addOrderDetail(OrderDetail detail) {
		orderDetails.add(detail);
		detail.setOrder(this);
	}
	
	@Override
    public String toString() {
        return "Order{orderDetails=" + orderDetails + "}";
    }
}
