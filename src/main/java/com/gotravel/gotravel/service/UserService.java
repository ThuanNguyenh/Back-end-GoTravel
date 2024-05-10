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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserDTO findById(UUID id) {
		// TODO Auto-generated method stub
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
	public UserDTO update(UUID id, UserDTO t) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void remove(UUID id) {
		// TODO Auto-generated method stub

	}

}
