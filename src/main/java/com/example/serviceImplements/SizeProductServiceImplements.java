package com.example.serviceImplements;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.entity.SizeProduct;
import com.example.repository.SizeProductRepository;
import com.example.service.SizeProductService;

@Service
public class SizeProductServiceImplements implements SizeProductService {

	@Autowired
	SizeProductRepository productRepository;

	@Override
	public SizeProduct findById(Integer id) {
		return productRepository.findById(id).orElse(null);
	}

	@Override
	public List<SizeProduct> findAll() {
		return productRepository.findAll();
	}
}
