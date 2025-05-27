package com.example.service;

import java.util.List;

import com.example.entity.Size;

public interface SizeService {
	Size findById(Integer id);
	List<Size> findAll();
	List<Size> getAllSizes();
}
