package com.example.service;

import java.util.List;
import java.util.Optional;

import com.example.entity.User;

public interface UserService {
	User findById(Integer id);
	List<User> findAll();
	User findByUsername(String username);
	Optional<User> findByEmail(String email);
	void save(User user);
	boolean existsByEmail(String email);
}
