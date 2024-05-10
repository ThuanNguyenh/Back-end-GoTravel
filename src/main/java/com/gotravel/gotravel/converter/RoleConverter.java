package com.gotravel.gotravel.converter;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gotravel.gotravel.dto.RoleDTO;
import com.gotravel.gotravel.dto.UserDTO;
import com.gotravel.gotravel.entity.Role;
import com.gotravel.gotravel.entity.User;

@Component
public class RoleConverter {
	
	
	public RoleDTO toDTO(Role role) {
		
		RoleDTO roleDTO = new RoleDTO();
		
		roleDTO.setRoleId(role.getId());
		roleDTO.setRoleName(role.getName());
		
		
		
		return roleDTO;
		
	}

}
