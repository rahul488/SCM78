 package com.finalProject.com.SmartContactManger.Controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.finalProject.com.SmartContactManger.Dao.UserRepository;
import com.finalProject.com.SmartContactManger.Entity.ErrorMessage;
import com.finalProject.com.SmartContactManger.Entity.User;
import com.finalProject.com.SmartContactManger.Service.EmailService;
import com.finalProject.com.SmartContactManger.Service.VerificationOtp;


@Controller
public class HomeController {
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private VerificationOtp verificationOtp;
	
	
	@RequestMapping("/")
	public String home() {
		return "home";
	}
	
	@RequestMapping("/about")
	public String about(Model model) {
		model.addAttribute("title", "about");
		return "about";
	}
	@RequestMapping("/signup")
	public String signup(Model model) {
		model.addAttribute("title", "SignUp");
		model.addAttribute("user", new User());
		return "signup";
	}
	@PostMapping("/do-register")
	public String registerUser(@Valid @ModelAttribute("user")User user,
			BindingResult result,@RequestParam(value = "agreement",defaultValue = "false")
		boolean agreement,Model model,HttpSession session,@RequestParam("imageUrl") MultipartFile file
		) {
	try {
		if(!agreement) {
			//System.out.println("hii");
		 
			throw new Exception("Please accept terms and conditions");
		}
		String email=user.getEmail();
		if(userRepo.findByUserName(email) != null) {
			session.setAttribute("message", new ErrorMessage("Username Already exist", "alert-danger"));
			return "signup";
		}
		
		if(file.isEmpty()) {
			System.out.println("Empty fie");
		}
		else {
		//uploading file
			
			user.setImageUrl(file.getOriginalFilename());
			
			//get destination folder path
			File saveFile=new ClassPathResource("static/img").getFile();
			//save image in destionation folder and get path
			Path path=Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
			//copy file
			Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
		}
		
		int otp=verificationOtp.createOtp(email);
		boolean flag=verificationOtp.sendEMail(otp, email);
		
		if(flag) {
			session.setAttribute("otp", otp);
			session.setAttribute("email", email);
			session.setAttribute("User", user);
			return "newUserOtp";
		}
		else {
			session.setAttribute("message",new ErrorMessage("Enter a valid email id", "alert-danger"));
			return "signup";
		}
		}
		catch(Exception e) {
			e.printStackTrace();
			model.addAttribute("user", user);
			session.setAttribute("message", new ErrorMessage(e.getMessage()+"","alert-danger"));
			return "signup";
		}
		
	}
	@RequestMapping("/signin")
	public String login(Model model) {
		model.addAttribute("title", "Login");
		return "login";
	}
	@PostMapping("/welcome-otp")
	public String verifyOtp(@RequestParam("otp") int otp,HttpSession session) {
		
		try {
		int myOtp=(int)session.getAttribute("otp");
		
		if(myOtp == otp) {
			
			User myUser=(User)session.getAttribute("User");
			myUser.setRole("ROLE_USER");
			
			myUser.setPassword(passwordEncoder.encode(myUser.getPassword()));
			
			userRepo.save(myUser);
			
			session.setAttribute("message", new ErrorMessage("Success", "alert-primary"));
			
			verificationOtp.sendWelcomeEmail(myUser.getEmail());
			
			return "login";
		}
		else {
			session.setAttribute("message", new ErrorMessage("Wrong Otp", "alert-danger"));
			return "newUserOtp";
		}
		
		}
		catch(Exception e) {
			session.setAttribute("message", new ErrorMessage("Wrong Otp", "alert-danger"));
			
		}
		return "newUserOtp";
	}
}
