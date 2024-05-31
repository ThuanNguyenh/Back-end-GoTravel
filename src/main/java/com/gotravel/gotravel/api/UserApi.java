package com.gotravel.gotravel.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.gotravel.gotravel.dto.UserDTO;
import com.gotravel.gotravel.entity.User;
import com.gotravel.gotravel.repository.UserRepository;
import com.gotravel.gotravel.service.impl.IUserService;

import jakarta.validation.Valid;

@RestController
@CrossOrigin
@RequestMapping(value = "/api/v1/user", produces = "application/json")
@Validated
public class UserApi {

	@Autowired
	private IUserService userService;

	@Autowired
	private UserRepository userRepository;

	@GetMapping()
	public ResponseEntity<?> getAllUser() {

		List<UserDTO> users = userService.findAll();

		if (!users.isEmpty()) {
			return new ResponseEntity<>(users, HttpStatus.OK);
		} else {
			return new ResponseEntity<>("Không có dữ liệu.", HttpStatus.NOT_FOUND);
		}

	}

	@GetMapping("/{id}")
	public ResponseEntity<?> getUserById(@PathVariable("id") UUID id) {
		try {
			UserDTO user = userService.findById(id);
			if (user != null) {
				return new ResponseEntity<>(user, HttpStatus.OK);
			} else {
				return new ResponseEntity<>("Người dùng không tồn tại.", HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi máy chủ.");
		}
	}

	@PutMapping("/update/{id}")
	public ResponseEntity<?> updateUserByIdd(@PathVariable("id") UUID id, @Validated @RequestBody UserDTO userUpdateDTO) {
		try {
			Optional<User> userOptional = userRepository.findById(id);
			if (userOptional.isPresent()) {
				UserDTO result = userService.update(id, userUpdateDTO);
				Map<String, Object> responseBody = new HashMap<>();
				responseBody.put("message", "Cập nhật thành công");
				responseBody.put("data", result);
				return new ResponseEntity<>(responseBody, HttpStatus.OK);
			} else {
				return new ResponseEntity<>("Tài khoản không tồn tại.", HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi máy chủ.");
		}
	}

	@DeleteMapping("/delete/{id}")
	private ResponseEntity<?> deleteUserById(@PathVariable("id") UUID id) {

		Optional<User> userOptional = userRepository.findById(id);

		return userOptional.map(user -> {

			userService.remove(id);
			return new ResponseEntity<>("Xóa thành công.", HttpStatus.OK);
		}).orElseGet(() -> new ResponseEntity<>("Tài khoản không tồn tại.", HttpStatus.NOT_FOUND));

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
