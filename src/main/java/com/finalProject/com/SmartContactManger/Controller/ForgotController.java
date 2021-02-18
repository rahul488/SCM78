package com.finalProject.com.SmartContactManger.Controller;

import java.util.Random;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.finalProject.com.SmartContactManger.Dao.UserRepository;
import com.finalProject.com.SmartContactManger.Entity.ErrorMessage;
import com.finalProject.com.SmartContactManger.Entity.User;
import com.finalProject.com.SmartContactManger.Service.EmailService;

@Controller
public class ForgotController {
	
	@Autowired
	private EmailService mailService;
	@Autowired
	private UserRepository userRepo;
	@Autowired
	private BCryptPasswordEncoder encoder;
	
	Random random=new Random();
	
	
	//email id form open handler
	
	@RequestMapping("/forgot")
	public String openEmailForm() {
		return "forgot";
	}
	@PostMapping("/send-otp")
	public String sendOtp(@RequestParam("email")String email,
				HttpSession session) {
		
		System.out.println(email);
		
		//check email is valid or not
		User user=userRepo.findByUserName(email);
		
		if(user == null) {
			session.setAttribute("message",new ErrorMessage("The email is not registered yet", "alert-danger"));
			return "forgot";
		}
		
		//generating otp of 6 digits
		
		
		int otp=random.nextInt(999999);
		
		//send otp to email
		
		String subject="OTP from SCM";
		String message=""
					   +"<div style='bored:1px solid;paddinf:20px'>"
					   +"<h1>"
					   +"OTP is"
					   +" "
					   +"<b>"+otp
					   +"</b>"
					   +"</h1>"
					   +"</div>";
		String to=email;
		
		boolean flag=mailService.sendEmail(subject, message, to);
		
		if(flag) {
			session.setAttribute("otp", otp);
			session.setAttribute("email", email);
			return "verify_otp";
		}
		else {
			session.setAttribute("message", new ErrorMessage("Check your email id !!","alert-danger"));
			return "forgot";
		}
		
		
	}
	@PostMapping("/verify-otp")
	public String verifyOtp(@RequestParam("otp")int otp,HttpSession session) {
		
		int myOtp=(int)session.getAttribute("otp");
		String email=(String)session.getAttribute("email");
		
		if(myOtp == otp) {
			return "password_change_form";
		}
		else {
			session.setAttribute("message", new ErrorMessage("You have enterd wrong Otp", "alert-danger"));
			return  "verify_otp";
		}
		
		
	}
	@PostMapping("/new-password")
	public String newPassword(@RequestParam("newpw")String newpw,
				@RequestParam("conformpw")String conformpw,
				HttpSession session) {
		
		String email=(String)session.getAttribute("email");
		
		if(newpw.equals(conformpw)) {
			String password=encoder.encode(newpw);
			
			User user=userRepo.findByUserName(email);
			
			user.setPassword(password);
			userRepo.save(user);
			session.setAttribute("message", new ErrorMessage("Password Reset Successfully", "alert-success"));
		}
		else {
			session.setAttribute("email", email);
			session.setAttribute("message", new ErrorMessage("Conform password must be same as password", "alert-danger"));
		}
		return "password_change_form"; 
	}
	
}
