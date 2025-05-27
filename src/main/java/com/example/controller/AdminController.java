package com.example.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.entity.Category;
import com.example.entity.Order;
import com.example.entity.Product;
import com.example.entity.User;
import com.example.service.CategoryService;
import com.example.service.OrderService;
import com.example.service.ProductService;
import com.example.service.UserService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin")
public class AdminController {
	@Autowired
	ProductService productService;
	@Autowired
	UserService userService;
	@Autowired
	OrderService orderService;
	@Autowired
	CategoryService categoryService;

	@GetMapping("/dashboard")
	public String dashboard(Model model) {
		model.addAttribute("pageTitle", "Quản Lý");
		return "admin/settings";
	}

	@GetMapping("/products")
	public String viewProductsPage(Model model, @RequestParam(defaultValue = "0") int page) {
		int pageSize = 5;
		Page<Product> productPage = productService.getProductsByPage(page, pageSize);
		model.addAttribute("products", productPage.getContent());
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", productPage.getTotalPages());
		model.addAttribute("pageTitle", "Quản Lý Sản Phẩm");
		return "admin/product/products";
	}

	@GetMapping("/users")
	public String users(Model model) {
		List<User> users = userService.findAll();
		model.addAttribute("users", users);
		model.addAttribute("pageTitle", "Quản Lý Người Dùng");
		return "admin/user/users";
	}

	@GetMapping("/reports")
	public String reports(Model model) {
		model.addAttribute("pageTitle", "Doanh Thu");
		return "admin/reports";
	}

	@RequestMapping("/search")
	public String searchProducts(@RequestParam("keyword") String keyword, Model model) {
		List<Product> products = productService.searchByName(keyword);
		model.addAttribute("products", products);
		model.addAttribute("pageTitle", "Tìm kiếm: ");
		return "admin/product/products";
	}

	@GetMapping("/add")
	public String showAddForm(Model model) {
		model.addAttribute("product", new Product());
		model.addAttribute("categories", categoryService.findAll());
		model.addAttribute("pageTitle", "Thêm sản phẩm");
		return "admin/product/add";
	}

	@PostMapping("/add")
	public String addProduct(@Valid @ModelAttribute("product") Product product, BindingResult result, Model model,
			@RequestParam("imageFile") MultipartFile imageFile, RedirectAttributes redirectAttributes) {
		if (result.hasErrors()) {
			model.addAttribute("categories", categoryService.findAll());
			model.addAttribute("pageTitle", "Thêm sản phẩm");
			return "admin/product/add"; // giữ nguyên form để hiển thị lỗi
		}

		if (!imageFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(imageFile.getOriginalFilename());
			Path uploadPath = Paths.get("D:/upload-images");

			try {
				if (!Files.exists(uploadPath)) {
					Files.createDirectories(uploadPath);
				}
				imageFile.transferTo(uploadPath.resolve(fileName));
				product.setImage(fileName); // Chỉ lưu tên file
			} catch (IOException e) {
				model.addAttribute("errorImage", "Không thể lưu hình ảnh.");

				model.addAttribute("categories", categoryService.findAll());
				model.addAttribute("pageTitle", "Thêm sản phẩm");
				return "admin/product/add";
			}
		} else {
			model.addAttribute("errorImage", "Vui lòng chọn hình ảnh.");
			model.addAttribute("categories", categoryService.findAll());
			model.addAttribute("pageTitle", "Thêm sản phẩm");
			return "admin/product/add";
		}
		product.setCreated_at(LocalDateTime.now());
		productService.save(product);
		redirectAttributes.addFlashAttribute("message", "Thêm sản phẩm thành công!");
		return "redirect:/admin/products";
	}

	@GetMapping("/product/edit/{id}")
	public String showEditForm(@PathVariable Integer id, Model model) {
		Product product = productService.findById(id);
		model.addAttribute("product", product);
		model.addAttribute("categories", categoryService.findAll());
		model.addAttribute("pageTitle", product.getName());
		return "admin/product/edit";
	}

	@PostMapping("/product/edit/{id}")
	public String updateProduct(@PathVariable Integer id, @ModelAttribute Product product,
			@RequestParam("imageFile") MultipartFile imageFile, RedirectAttributes redirectAttributes) {
		Product existingProduct = productService.findById(id);
		if (existingProduct == null) {
			return "admin/products";
		}
		if (!imageFile.isEmpty()) {
			String fileName = imageFile.getOriginalFilename();
			Path uploadPath = Paths.get("src/main/resources/static/images");

			try {
				if (!Files.exists(uploadPath)) {
					Files.createDirectories(uploadPath);
				}

				Path filePath = uploadPath.resolve(fileName);
				imageFile.transferTo(filePath);

				product.setImage(fileName);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			product.setImage(existingProduct.getImage());
		}
		product.setCreated_at(existingProduct.getCreated_at());
		product.setId(id);
		redirectAttributes.addFlashAttribute("message", "Cập nhật sản phẩm thành công! " + product.getName());
		productService.save(product);
		return "redirect:/admin/products";
	}

	@GetMapping("/delete/{id}")
	public String deleteProduct(@PathVariable Integer id,RedirectAttributes redirectAttributes) {
		productService.deleteById(id);
		redirectAttributes.addFlashAttribute("message", "Xóa sản phẩm thành công! ");
		return "redirect:/admin/products";
	}
	
	@GetMapping("/statistics")
	public String showStatistics(Model model) {
	    List<Object[]> monLyrevenueData = orderService.getMonthlyRevenue();
	    List<Object[]> daiLyrevenueData = orderService.getDailyRevenue();
	    model.addAttribute("mrevenueData", monLyrevenueData);
	    model.addAttribute("drevenueData", daiLyrevenueData);
	    model.addAttribute("pageTitle","Doanh Thu");
	    return "admin/statistics";
	}

}
