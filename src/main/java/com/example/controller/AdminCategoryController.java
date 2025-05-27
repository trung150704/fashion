package com.example.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.entity.Category;
import com.example.service.CategoryService;

@Controller
@RequestMapping("/admin/categories")
public class AdminCategoryController {

	@Autowired CategoryService categoryService;
	
	@GetMapping("")
	public String categories(Model model) {
		List<Category> categories = categoryService.findAll();
		model.addAttribute("pageTitle", "Quản Lý Danh Mục");
		model.addAttribute("categories", categories);
		return "admin/category/categories";
	}

	// Hiển thị form edit
	@GetMapping("/edit/{id}")
	public String editCategory(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
	    Category catid = categoryService.findById(id);
	    
	    if (catid == null) {
	        redirectAttributes.addFlashAttribute("message", "Không tìm thấy danh mục.");
	        return "redirect:/admin/categories";
	    }
	    
	    model.addAttribute("catid", catid);
	    model.addAttribute("pageTitle", "Chỉnh sửa danh mục");
	    return "admin/category/edit"; // Tạo file HTML này
	}

	// Xử lý cập nhật
	@PostMapping("/edit/{id}")
	public String updateCategory(@PathVariable Integer id,
	                             @ModelAttribute("catid") Category updatedCategory,
	                             RedirectAttributes redirectAttributes) {
		
		Category catid = categoryService.findById(id);
	    
		if (catid == null) {
	        redirectAttributes.addFlashAttribute("message", "Danh mục không tồn tại.");
	        return "redirect:/admin/categories";
	    }
		System.out.println("Tên mới: " + updatedCategory.getName());
		catid.setName(updatedCategory.getName());
	    categoryService.save(catid);

	    redirectAttributes.addFlashAttribute("message", "Cập nhật danh mục thành công!");
	    return "redirect:/admin/categories";
	}


	@GetMapping("/delete/{id}")
	public String deleteCategory(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
		categoryService.deleteById(id);
		redirectAttributes.addFlashAttribute("message", "Xóa danh mục thành công!");
		return "redirect:/admin/categories";
	}
	
	@GetMapping("/add")
	public String showCategory(Model model) {
		model.addAttribute("catid", new Category());
	    model.addAttribute("pageTitle", "Thêm danh mục mới");
	    return "admin/category/add"; // file HTML
	}

	@PostMapping("/add")
	public String addCategory(@ModelAttribute("catid") Category category,
            RedirectAttributes redirectAttributes) {
		
		categoryService.save(category);
	    redirectAttributes.addFlashAttribute("message", "Thêm danh mục thành công!");
	    return "redirect:/admin/categories";
	}
}
