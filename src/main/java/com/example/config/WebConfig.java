package com.example.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		// Ưu tiên lấy ảnh ở D:/upload-images nếu tồn tại
		registry.addResourceHandler("/images/**").addResourceLocations("file:/D:/upload-images/", // ảnh upload
				"classpath:/static/images/"); // fallback: ảnh tĩnh
	}
	
}
