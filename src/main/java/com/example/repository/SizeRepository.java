package com.example.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.entity.Size;

@Repository
public interface SizeRepository extends JpaRepository<Size, Integer>{
	Optional<Size>  findById (Integer id);
	List<Size> findAllByOrderByIdAsc();
}
