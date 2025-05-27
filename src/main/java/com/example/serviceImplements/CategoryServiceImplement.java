package com.example.serviceImplements;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.entity.Category;
import com.example.repository.CategoryRepository;
import com.example.service.CategoryService;

@Service
public class CategoryServiceImplement implements CategoryService{
	@Autowired
	CategoryRepository categoryRepository;
	
	@Override
	public Category findById(Integer id) {
		return categoryRepository.findById(id).orElse(null);
	}

	@Override
	public List<Category> findAll() {
		return categoryRepository.findAll();
	}

	@Override
	public Category save(Category category) {
		return categoryRepository.save(category);		
	}
	
	@Override
	public void deleteById(Integer id) {
		categoryRepository.deleteById(id);
	}
	
	
}
