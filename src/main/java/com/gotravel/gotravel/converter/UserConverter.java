package com.gotravel.gotravel.converter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gotravel.gotravel.dto.UserDTO;
import com.gotravel.gotravel.entity.Role;
import com.gotravel.gotravel.entity.User;
import com.gotravel.gotravel.repository.RoleCustomRepository;

@Component
public class UserConverter {

	@Autowired
	private RoleCustomRepository roleCustomRepository;
	
	public UserDTO toDTO(User user) {

		UserDTO userDTO = new UserDTO();

		userDTO.setUserId(user.getId());
		userDTO.setUserName(user.getUsername());
		userDTO.setEmail(user.getEmail());
		userDTO.setPhone(user.getPhone());
		userDTO.setProvince(user.getProvince());
		userDTO.setDistrict(user.getDistrict());
		userDTO.setWard(user.getWard());
		userDTO.setAvatar(user.getAvatar());
		
		// roles
		List<Role> roles = roleCustomRepository.getRole(user);
		List<String> roleNames = roles.stream()
				.map(Role::getName)
				.collect(Collectors.toList());

		userDTO.setRoles(roleNames);

		return userDTO;

	}

	// to entity
	public User toEntity(UserDTO userDTO) {

		User user = new User();

		user.setUserName(userDTO.getUserName());
		user.setEmail(userDTO.getEmail());
		user.setPhone(userDTO.getPhone());
		user.setProvince(userDTO.getProvince());
		user.setDistrict(userDTO.getDistrict());
		user.setWard(userDTO.getWard());
		user.setAvatar(userDTO.getAvatar());

		return user;

	}

}
