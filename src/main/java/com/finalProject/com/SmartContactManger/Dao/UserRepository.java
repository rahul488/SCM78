package com.finalProject.com.SmartContactManger.Dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.finalProject.com.SmartContactManger.Entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {
	@Query("select u from User u where u.email=:email")
	User findByUserName(@Param("email")String email);
}
