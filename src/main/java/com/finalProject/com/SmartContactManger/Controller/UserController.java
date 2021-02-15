package com.finalProject.com.SmartContactManger.Controller;

import java.security.Principal;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.finalProject.com.SmartContactManger.Dao.ContactRepository;
import com.finalProject.com.SmartContactManger.Dao.UserRepository;
import com.finalProject.com.SmartContactManger.Entity.Contact;
import com.finalProject.com.SmartContactManger.Entity.ErrorMessage;
import com.finalProject.com.SmartContactManger.Entity.User;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;



@Controller
@RequestMapping("/users")
public class UserController {
	@Autowired
	private UserRepository userRepo;
	@Autowired
	private ContactRepository contactRepo;
	@Autowired
	private BCryptPasswordEncoder encoder;
	
	@RequestMapping("/index")
	public String index() {
		return "User/index";
	}
	
	//for common data
	@ModelAttribute
	public void getUser(Model model,Principal principal) {
			  //get username
				String userName=principal.getName();
				System.out.println(userName);
				//get name of user
				User user=userRepo.findByUserName(userName);
				//send user to user-dashboard
				model.addAttribute("user", user);
	}
	//user dashboard
	@RequestMapping("/list")
	public String userDashboard(Model model,Principal principal) {
		
		return "User/user-dashboard";
	}
	//add contacts
	@GetMapping("/add-contact")
	public String openAddContactFrom(Model model) {
		model.addAttribute("contact", new Contact());
		return "User/add-contact";
	}
	//validate contacts
	@PostMapping("/process-contact")
	public String processContact(@ModelAttribute Contact contact
			,Principal principal,HttpSession session) {
		try {
		String name=principal.getName();
		User user=this.userRepo.findByUserName(name);
		contact.setImg("contact.png");
		user.getContacts().add(contact);
		contact.setUser(user); 
		this.userRepo.save(user);
		
		session.setAttribute("message", new ErrorMessage("Success", "alert-success"));
		
		System.out.println(contact);
		}
		catch(Exception e) {
			e.printStackTrace();
			session.setAttribute("message", new ErrorMessage("Error", "alert-danger"));
		}
		return "User/add-contact";
	}
	//show contacts
	@GetMapping("/show-contacts/{page}")
	public String showContacts(@PathVariable("page")int page,Model m,Principal principal) {
		String email=principal.getName();
		
		User user=this.userRepo.findByUserName(email);
		
		Pageable pageable=PageRequest.of(page, 4);
		
		Page<Contact> contacts=this.contactRepo.findContactsBuUser(user.getId(),pageable);
		
		m.addAttribute("contacts", contacts);
		m.addAttribute("currentPage", page);
		m.addAttribute("totalPage", contacts.getTotalPages());
		
		return "User/show_contacts";
		
		//handle pagination 
		//--------------------------------------
		
		
	}
	@RequestMapping("/contact/{id}")
	public String showContactDetails(@PathVariable("id")int id,Model model
				,Principal principal) {
		
		Optional<Contact> contactOptional=contactRepo.findById(id);
		Contact contact=contactOptional.get();
		if(authorizeUser(principal,contact)) {
			model.addAttribute("contact", contact);
		}
		return "User/contact_details";
	}
	
	//delete contact
	@GetMapping("/delete/{id}")
	public String deleteContact(@PathVariable("id") int id,Model model,
				Principal principal,HttpSession session) {
		
		Optional<Contact> contactId=contactRepo.findById(id);
		Contact contact=contactId.get();
		
		if(authorizeUser(principal, contact)) {
			contact.setUser(null);
			
			this.contactRepo.delete(contact);
			session.setAttribute("message", new ErrorMessage("Deleted Succesfully", "alert-success"));
		}
		return "redirect:/users/show-contacts/0";
	}
	@RequestMapping("/update/{id}")
	public String updateContact(@PathVariable("id") int id,Principal principal,
			Model model) {
		Optional<Contact> contactOptional=contactRepo.findById(id);
		//retrieve Contact from conatactOptional
		Contact contact=contactOptional.get();
		//check if logged user id is equal to the conatct's user id
		if(authorizeUser(principal,contact)) {
			model.addAttribute("contact", contact);
		}
		
		return "User/update-contact";
	}
	@PostMapping("/update-contact")
	public String updateContactDetails(@ModelAttribute Contact contact,
			Principal principal,HttpSession session) {
		
			User user=userRepo.findByUserName(principal.getName());
			contact.setUser(user);
			contactRepo.save(contact);
			session.setAttribute("message", new ErrorMessage("Updated Successfully", "alert-success"));
		
		
		
		return "redirect:/users/show-contacts/0";
	}
	public boolean authorizeUser(Principal principal,Contact contact) {
				//get logged user
				String email=principal.getName();
				
				//retrieve email
				User user=userRepo.findByUserName(email);
				
				System.out.println(contact.getUser().getId());
				if(user.getId() == contact.getUser().getId()) {
					return true;
				}
				
		return false;
	}
	@RequestMapping("/settings")
	public String settingContacts() {
		return "User/settings";
	}
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("oldpw") String oldPassword,
								@RequestParam("newpw")String newPassword,
								Principal principal,HttpSession session) {
		
		String email=principal.getName();
		User currentUser=userRepo.findByUserName(email);
		
		if(encoder.matches(oldPassword, currentUser.getPassword())) {
			currentUser.setPassword(encoder.encode(newPassword));
			userRepo.save(currentUser);
			session.setAttribute("message", new ErrorMessage("Your password is successfully Chnaged", "alert-success"));
		}else {
			session.setAttribute("message", new ErrorMessage("Please enter correct old-password", "alert-danger"));
			return "redirect:/users/settings";
			
		}
		
		return "redirect:/users/settings";
	}
	//payment integration
	@PostMapping("/create_order")
	@ResponseBody
	public String createOrder(@RequestBody Map<String,Object> data) throws Exception {
		System.out.println("func. executed");
		
		System.out.println(data);
		
		int amt=Integer.valueOf(data.get("amount").toString());
		
		var client=new RazorpayClient("rzp_test_EcvsAXSTCmnlAX", "KZB1Mu0cmlviw4ohOrA1Vr1P");
		
		JSONObject request=new JSONObject();
		
		request.put("amount", amt*100);
		request.put("currency", "INR");
		request.put("receipt", "txn_23459");
		
		Order order=client.Orders.create(request);
		
		System.out.println(order);
		
		return order.toString();
	}
}