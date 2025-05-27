package com.example.controlleradvice;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.example.entity.Category;
import com.example.service.CategoryService;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private CategoryService categoryService;

    @ModelAttribute
    public void addCategoriesToModel(Model model) {
        List<Category> categoryList = categoryService.findAll();
        model.addAttribute("category", categoryList); // Tên biến dùng trong HTML
    }
}

