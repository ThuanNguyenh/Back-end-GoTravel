package com.gotravel.gotravel.service.impl;

import com.gotravel.gotravel.dto.UserDTO;
import com.gotravel.gotravel.entity.Role;

public interface IUserService extends IGeneralService<UserDTO>{
	
	public Role saveRole(Role role);
	
	public void addToUser(String username, String roleName);

}
