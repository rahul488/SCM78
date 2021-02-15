package com.finalProject.com.SmartContactManger.Controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.finalProject.com.SmartContactManger.Dao.ContactRepository;
import com.finalProject.com.SmartContactManger.Dao.UserRepository;
import com.finalProject.com.SmartContactManger.Entity.Contact;
import com.finalProject.com.SmartContactManger.Entity.User;

@RestController
public class SearchController {
	
	@Autowired
	private UserRepository userRepo;
	@Autowired
	private ContactRepository contactRepo;
	
	//search handler
	
	@GetMapping("/search/{query}")
	public ResponseEntity<?> search(@PathVariable("query") String query, Principal principal){
		System.out.println(query);
		
		User user=this.userRepo.findByUserName(principal.getName());
		
		List<Contact> contacts= this.contactRepo.findByNameContainingAndUser(query, user);
		
		return ResponseEntity.ok(contacts);
	}

}
