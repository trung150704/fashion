package com.example.serviceImplements;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.entity.Size;
import com.example.repository.SizeRepository;
import com.example.service.SizeService;

@Service
public class SizeServiceImplements implements SizeService{

	@Autowired
	SizeRepository sizeRepository;
	
	@Override
	public Size findById(Integer id) {
		return sizeRepository.findById(id).orElse(null);
	}

	@Override
	public List<Size> findAll() {
		return sizeRepository.findAll();
	}

	@Override
	public List<Size> getAllSizes() {
        return sizeRepository.findAllByOrderByIdAsc();
    }
}
