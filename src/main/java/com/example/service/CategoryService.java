package com.example.service;

import java.util.List;

import com.example.entity.Category;

public interface CategoryService {
	Category findById(Integer id);
	List<Category> findAll();
	void deleteById(Integer id);
	Category save(Category category);
}
