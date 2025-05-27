package com.example.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@NotBlank(message = "Tên đăng nhập không được để trống")
	@Size(min = 4, max = 20, message = "Tên đăng nhập từ 4 đến 20 ký tự")
	private String username;
	@NotBlank(message = "Mật khẩu không được để trống")
	@Size(min = 4, message = "Mặt khẩu hơn 4 ký tự")
	private String password;
	@Email(message = "Email không hợp lệ")
	@NotBlank(message = "Email không được để trống")
	@Column(unique = true, nullable = false)
	private String email;
	private String role;
	private LocalDateTime created_at;
}
