package com.example.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.data.domain.Pageable;

import com.example.entity.Category;
import com.example.entity.Product;
import com.example.entity.Size;
import com.example.service.CategoryService;
import com.example.service.ProductService;
import com.example.service.SizeService;

@Controller

public class ProductController {

    @Autowired
    private ProductService productService;
    
    @Autowired
    private SizeService sizeService;
    
    @Autowired
    private CategoryService categoryService;
    
    // Trang tất cả sản phẩm
    @RequestMapping("/products")
    public String listProducts(Model model,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(required = false) String keyword,
                               @RequestParam(required = false) String priceRange) {
        BigDecimal min = null, max = null;
        if (priceRange != null && priceRange.contains("-")) {
            String[] parts = priceRange.split("-");
            try {
                min = new BigDecimal(parts[0].trim());
                max = new BigDecimal(parts[1].trim());
            } catch (NumberFormatException e) {
                // xử lý lỗi nếu người dùng nhập sai định dạng giá
            }
        }

        Page<Product> products = productService.filterProducts(keyword, min, max, page);

        model.addAttribute("p", products.getContent());
        model.addAttribute("products", products.getContent()); // dùng chung nếu không cần phân biệt
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", products.getTotalPages());
        model.addAttribute("pageTitle", "Danh sách sản phẩm");

        return "product/list";
    }

    // Trang chi tiết sản phẩm
    @RequestMapping("/products/{id}")
    public String detailProduct(@PathVariable("id") Integer id, Model model
    		,@RequestParam(value ="page",defaultValue = "0") int page) {
        Product product = productService.findById(id);
        List<Size>  size = sizeService.getAllSizes();
        Pageable pageable = PageRequest.of(page, 4);
        Page<Product> relatedProducts = productService.findRelatedProductsExcludeCurrent(
                product.getCategory().getId(),id, pageable);

        model.addAttribute("relatedProducts", relatedProducts);
        model.addAttribute("currentPage", page);
        model.addAttribute("size", size);
        model.addAttribute("product", product);
        model.addAttribute("pageTitle",product.getName());
        return "product/detail";
    }
    
    //tim kiem
    @RequestMapping("/products/search")
    public String searchProducts(@RequestParam("keyword") String keyword, Model model) {
        List<Product> products = productService.searchByName(keyword);
        model.addAttribute("products", products);
        model.addAttribute("pageTitle","Tìm kiếm: "+ keyword);
        model.addAttribute("currentPage", 0);
        model.addAttribute("totalPages", 1);
        return "product/list"; // Trang danh sách sản phẩm
    }
    
    //danh muc
    @RequestMapping("/category/{id}")
    public String getProductsByCategory(@PathVariable("id") Integer categoryId, Model model) {
        List<Product> findCategoryId = productService.findByCategoryId(categoryId);
        Category category = categoryService.findById(categoryId);
        model.addAttribute("products", findCategoryId);
        model.addAttribute("pageTitle","Danh mục "+category.getName());
        model.addAttribute("currentPage", 0);   // Không null
        model.addAttribute("totalPages", 1);
        return "product/list";
    }

}
