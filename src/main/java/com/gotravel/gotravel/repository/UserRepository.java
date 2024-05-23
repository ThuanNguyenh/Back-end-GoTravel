package com.gotravel.gotravel.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.gotravel.gotravel.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, UUID>{
	
	Optional<User> findByEmail(String email);
	
	@Modifying
	@Query(value = "DELETE FROM users_role where user_id = :userId", nativeQuery = true)
	void deleteUserRolesByUserIdd(@Param("userId") UUID userId);

}
