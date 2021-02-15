package com.finalProject.com.SmartContactManger.Dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.finalProject.com.SmartContactManger.Entity.Contact;
import com.finalProject.com.SmartContactManger.Entity.User;

public interface ContactRepository extends JpaRepository<Contact, Integer>{
	
	//pagination
	@Query("select c from Contact c where c.user.id=:userId")
	
	//pageable required current page and current pagesize
	
	public Page<Contact> findContactsBuUser(@Param("userId")int userId,Pageable pageable);
	
	List<Contact> findByNameContainingAndUser(String name,User user);
}
