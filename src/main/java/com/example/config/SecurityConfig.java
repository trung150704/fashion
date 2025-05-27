package com.example.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.SecurityFilterChain;

import com.example.entity.User;
import com.example.service.UserService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    private UserService userService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
        	.csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth .anyRequest().permitAll()
//            	.requestMatchers("/", "/auth/**", "/layout/**","/home/**", "/product/**", "/css/**", "/js/**", "/images/**","/js/**").permitAll()
            		//.requestMatchers("/admin/**").hasRole("ADMIN")
//                .anyRequest().authenticated()
            )
            .formLogin(login -> login
            		.loginPage("/login") // DÙNG form bạn thiết kế
            	    .loginProcessingUrl("/login") // Spring sẽ xử lý form này
            	    .defaultSuccessUrl("/", true) // Trang sẽ chuyển đến sau khi đăng nhập thành công
            	    .failureUrl("/login?error=true") // Đăng nhập sai sẽ trả về trang login kèm lỗi
            	    .permitAll()
            )
            .logout(logout -> logout.permitAll());//trả về trang login
        	
		     // Trở về trang chủ sau khi logout
//		        .logout(logout -> logout
//        		.logoutUrl("/logout")
//                .logoutSuccessUrl("/") 
//                .permitAll());
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            User user = userService.findByUsername(username);
            if (user == null) throw new UsernameNotFoundException("User not found");
            return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
            );
        };
    }
}

