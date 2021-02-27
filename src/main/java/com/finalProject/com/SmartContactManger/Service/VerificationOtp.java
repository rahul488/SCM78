package com.finalProject.com.SmartContactManger.Service;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VerificationOtp {
	
	@Autowired
	private EmailService emailService;
	
	Random random=new Random();
	
	public int createOtp(String email) {

		int otp=random.nextInt(999999);
		
		return otp;
		
	}
	
	public boolean sendEMail(int otp,String email) {
		
		String subject="Verification OTP from SCM";
		String message=""
					   +"<div style='bored:1px solid;paddinf:20px'>"
					   +"<h2>"
					   +"Please verify the email id by entering the otp for create a new Account"
					   +" "
					   +"<i>"+otp
					   +"</i>"
					   +"</h2>"
					   +"</div>";
		String to=email;
		
		boolean flag=emailService.sendEmail(subject, message, to);
		
		return flag;
	}

	public void sendWelcomeEmail(String email) {
		
		String subject="Welcome to SCM";
		String message=""
					   +"<h4>"
					   +"<i>"
					   +"Congratulations on joining us as a Member"
					   + "Now you can store your Contacts to our Cloud and you can retrieve it when you want."
		
					   +"</i>"
					   +"<div style='margin-right:20px'>"
					   +"<b>"
					   +"Thank You"
					   +"<b>"
					   +"</div>"
					   ;
					
		String to=email;
		
		boolean flag=emailService.sendEmail(subject, message, to);
		
		
	}

}
