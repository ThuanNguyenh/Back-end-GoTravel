package com.gotravel.gotravel.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.gotravel.gotravel.auth.AuthenticationRequest;
import com.gotravel.gotravel.auth.AuthenticationResponse;
import com.gotravel.gotravel.auth.ErrorResponse;
import com.gotravel.gotravel.auth.Logout;
import com.gotravel.gotravel.auth.RegisterRequest;
import com.gotravel.gotravel.entity.User;
import com.gotravel.gotravel.repository.UserRepository;
import com.gotravel.gotravel.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@CrossOrigin
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Validated
public class AuthenticationApi {

	private final AuthService authService;

	@Autowired
	private UserRepository userRepository;

	private final PasswordEncoder passwordEncoder;

	// ĐĂNG KÝ
	@PostMapping("/register")
	public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {

		try {

			// Kiểm tra email đã tồn tại hay chưa
			Optional<User> existingEmail = userRepository.findByEmail(registerRequest.getEmail());

			if (existingEmail.isPresent()) {
				return ResponseEntity.status(HttpStatus.CONFLICT).body("Email đã tồn tại.");
			}

			// trường hợp đúng

			AuthenticationResponse response = authService.register(registerRequest);
			Map<String, Object> responsebody = new HashMap<>();
			responsebody.put("message", "Đăng ký thành công");
			responsebody.put("data", response);
			return ResponseEntity.ok(responsebody);

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi máy chủ.");
		}

	}

	

	// ĐĂNG NHẬP
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody @Valid AuthenticationRequest authenticationRequest) {

		Optional<User> userOptional = userRepository.findByEmail(authenticationRequest.getEmail());

		if (!userOptional.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tài khoản không tồn tại.");
		}

		User user = userOptional.get();
		String encodePassword = user.getPassword();

		// kiểm tra tính đúng đắng của mật khẩu nhập vào
		if (passwordEncoder.matches(authenticationRequest.getPassword(), encodePassword)) {
			// mật khẩu khớp

			AuthenticationResponse authResponse = authService.authenticate(authenticationRequest);
			Map<String, Object> responseBody = new HashMap<>();
			
			responseBody.put("message", "Đăng nhập thành công");
			responseBody.put("data", authResponse);

			return ResponseEntity.ok(responseBody);
		} else {
			// mật khẩu không khớp
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Mật khẩu không đúng.");
		}

	}

	// ĐĂNG XUẤT
	@PostMapping("/logout")
	public ResponseEntity<String> logout(@RequestHeader("Authorization") String authorizationHeader) {
		// kiểm tra và lấy JWT Token từ header

		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			String jwtToken = authorizationHeader.substring(7);
			// Gọi phương thức đăng xuất từ AuthService
			authService.logout(jwtToken);
			return ResponseEntity.ok("Đăng xuất thành công.");
		} else {
			return ResponseEntity.badRequest().body("Lỗi đăng xuất.");
		}

	}
	
		// VALIDATION INPUT
		@ResponseStatus(HttpStatus.BAD_REQUEST)
		@ExceptionHandler(BindException.class)
		public String handleBindException(BindException e) {

			String errorMessage = null;

			if (e.getBindingResult().hasErrors()) {
				errorMessage = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();

			}

			return errorMessage;
		}

}
