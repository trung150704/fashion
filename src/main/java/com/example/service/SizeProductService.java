package com.example.service;

import java.util.List;
import com.example.entity.SizeProduct;

public interface SizeProductService {

	SizeProduct findById(Integer id);

	List<SizeProduct> findAll();
}
