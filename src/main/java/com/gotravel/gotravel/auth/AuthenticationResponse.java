package com.gotravel.gotravel.auth;

import java.util.Set;
import java.util.UUID;

import com.gotravel.gotravel.dto.UserDTO;
import com.gotravel.gotravel.entity.Role;
import com.gotravel.gotravel.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {

	private String token;
	private String refreshToken;
	
	private UserDTO user;
	
}
