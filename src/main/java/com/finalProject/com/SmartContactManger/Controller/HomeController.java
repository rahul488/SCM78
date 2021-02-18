 package com.finalProject.com.SmartContactManger.Controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.finalProject.com.SmartContactManger.Dao.UserRepository;
import com.finalProject.com.SmartContactManger.Entity.ErrorMessage;
import com.finalProject.com.SmartContactManger.Entity.User;


@Controller
public class HomeController {
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private UserRepository userRepo;
	
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
		boolean agreement,Model model,HttpSession session
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
		user.setRole("ROLE_USER");
		user.setEnabled(true);
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		userRepo.save(user);
		session.setAttribute("message", new ErrorMessage("Successfully Registered", "alert-success"));
		return "signup";
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

}
