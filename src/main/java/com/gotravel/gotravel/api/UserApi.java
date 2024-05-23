package com.gotravel.gotravel.api;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gotravel.gotravel.dto.UserDTO;
import com.gotravel.gotravel.entity.User;
import com.gotravel.gotravel.repository.UserRepository;
import com.gotravel.gotravel.service.impl.IUserService;

@RestController
@CrossOrigin
@RequestMapping(value = "/api/v1/user", produces = "application/json")
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
	private ResponseEntity<?> getUserById(@PathVariable("id") UUID id) {

		UserDTO user = userService.findById(id);

		if (user != null) {
			return new ResponseEntity<>(user, HttpStatus.OK);
		} else {
			return new ResponseEntity<>("Người dùng không tồn tại.", HttpStatus.NOT_FOUND);
		}

	}

	@PutMapping("/update/{id}")
	private ResponseEntity<?> updateUserByIdd(@PathVariable("id") UUID id, @RequestBody UserDTO userUpdateDTO) {

		Optional<User> userOp = userRepository.findById(id);

		return userOp.map(user -> {
			userService.update(id, userUpdateDTO);
			return new ResponseEntity<>("Cập nhật thành công.", HttpStatus.OK);
		}).orElseGet(() -> new ResponseEntity<>("Tài khoản không tồn tại.", HttpStatus.NOT_FOUND));

	}

	@DeleteMapping("/delete/{id}")
	private ResponseEntity<?> deleteUserById(@PathVariable("id") UUID id) {

		Optional<User> userOptional = userRepository.findById(id);

		return userOptional.map(user -> {

			userService.remove(id);
			return new ResponseEntity<>("Xóa thành công.", HttpStatus.OK);
		}).orElseGet(() -> new ResponseEntity<>("Tài khoản không tồn tại.", HttpStatus.NOT_FOUND));

	}

}
