package com.gotravel.gotravel.dto;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.gotravel.gotravel.entity.Role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
	private UUID userId;
	private String userName;
	private String email;
	private String password;
	private String phone;
	private String province;
	private String district;
	private String ward;
	private String avatar;
	private List<String> roles;
	
}
