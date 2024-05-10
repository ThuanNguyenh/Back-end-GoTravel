package com.gotravel.gotravel.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gotravel.gotravel.entity.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID>{
	
	Role findByName(String role);

}
