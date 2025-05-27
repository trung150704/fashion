package com.example.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.entity.SizeProduct;

@Repository
public interface SizeProductRepository extends JpaRepository<SizeProduct, Integer>{
	Optional<SizeProduct>  findById (Integer id);
	
}
