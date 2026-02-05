package com.rm.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rm.user.entity.User;


public interface UserRepository extends JpaRepository<User, Long>{
	User getByUid(String uid);
	boolean existsByUid(String uid);
	boolean existsByEmail(String email);
}
