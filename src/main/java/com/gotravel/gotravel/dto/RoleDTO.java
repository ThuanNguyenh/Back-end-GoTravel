package com.gotravel.gotravel.dto;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleDTO {
	
	private UUID roleId;
	private String roleName;
	private List<UserDTO> users;

}
