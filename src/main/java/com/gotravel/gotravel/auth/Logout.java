package com.gotravel.gotravel.auth;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import com.gotravel.gotravel.repository.TokenRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class Logout implements LogoutHandler {
	
	private final TokenRepository tokenRepository;

	@Override
	public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
		
		// lấy giá tri của tiêu đề từ HTTP.
		// bao gồm thông tin xác thực của người dùng và jwt
		final String authHeader= request.getHeader("Authorization");
		final String jwt;
		
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			return;
		}
		// cắt bỏ phần Bearer ra khỏi chuỗi authHeader chỉ lấy phần JWT
		jwt = authHeader.substring(7);
		
		// tìm kiếm token trong csdl
		var storedToken = tokenRepository.findByToken(jwt).orElse(null);
		
		if (storedToken != null) {
			System.out.println("token nè: " + storedToken);
			storedToken.setExpired(true);
			storedToken.setRevoked(true);
			tokenRepository.save(storedToken);
			SecurityContextHolder.clearContext();
		}
		
	}

}
