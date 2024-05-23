package com.gotravel.gotravel.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.gotravel.gotravel.converter.UserConverter;
import com.gotravel.gotravel.dto.RoleDTO;
import com.gotravel.gotravel.dto.UserDTO;
import com.gotravel.gotravel.entity.Role;
import com.gotravel.gotravel.entity.User;
import com.gotravel.gotravel.repository.RoleRepository;
import com.gotravel.gotravel.repository.UserRepository;
import com.gotravel.gotravel.service.impl.IUserService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserService implements IUserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserConverter userConverter;

	@Autowired
	private RoleRepository roleRepository;

	private final PasswordEncoder passwordEncoder;

	@Override
	public List<UserDTO> findAll() {

		List<User> users = userRepository.findAll();

		List<UserDTO> userDTOs = new ArrayList<>();

		for (User user : users) {
			if (user != null) {
				userDTOs.add(userConverter.toDTO(user));
			}
		}

		return userDTOs;
	}

	@Override
	public UserDTO findById(UUID id) {

		Optional<User> userOp = userRepository.findById(id);

		if (userOp.isPresent()) {
			User user = userOp.get();

			UserDTO userDTO = userConverter.toDTO(user);

			return userDTO;
		}

		return null;
	}

	@Override
	public UserDTO save(UserDTO userDTO) {

		User user = new User();

		user = userConverter.toEntity(userDTO);

		user.setPassword(passwordEncoder.encode(userDTO.getPassword()));

		userRepository.save(user);

		return userConverter.toDTO(user);
	}

	@Override
	public Role saveRole(Role role) {
		return roleRepository.save(role);
	}

	@Override
	public void addToUser(String username, String name) {

		User user = userRepository.findByEmail(username).get();

		Role role = roleRepository.findByName(name);

		user.getRoles().add(role);

	}

	@Override
	public UserDTO update(UUID id, UserDTO userUpdateDTO) {

		Optional<User> userOp = userRepository.findById(id);

		if (userOp.isPresent()) {

			User user = userOp.get();
			User userUpdate = userConverter.toEntity(userUpdateDTO);

			user.setUserName(userUpdate.getUsername());
			user.setEmail(userUpdate.getEmail());
			user.setPassword(userUpdate.getPassword());
			user.setPhone(userUpdate.getPhone());
			user.setProvince(userUpdate.getProvince());
			user.setDistrict(userUpdate.getDistrict());
			user.setWard(userUpdate.getWard());
			user.setAvatar(userUpdate.getWard());

			User result = userRepository.save(user);
			return userConverter.toDTO(result);
		} else {
			throw new EntityNotFoundException("Dữ liệu không tồn tại.");
		}

	}

	@Override
	public void remove(UUID id) {

		Optional<User> userOptional = userRepository.findById(id);

		if (userOptional.isPresent()) {
			User user = userOptional.get();
			// xóa liên kết trong bảng users_role trước
			userRepository.deleteUserRolesByUserIdd(id);
			
			userRepository.deleteById(id);
		} else {
			throw new EntityNotFoundException("Không tìm thấy tài khoản với id: " + id);
		}

	}

}
