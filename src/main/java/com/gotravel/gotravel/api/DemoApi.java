package com.gotravel.gotravel.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gotravel.gotravel.service.AuthService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/demo")
@RequiredArgsConstructor
public class DemoApi {
	
	private final AuthService authService;
	
	@GetMapping("/test")
	public ResponseEntity<String> login() {
		return ResponseEntity.ok("Xac thuc va phan quyen thanh cong!");
	}

}
