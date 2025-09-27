package com.myproject.OAS.Controller;

import java.net.http.HttpRequest;
import java.time.LocalDateTime;
import java.util.jar.Attributes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.myproject.OAS.Model.Enquiry;
import com.myproject.OAS.Model.Users;
import com.myproject.OAS.Model.Users.UserRole;
import com.myproject.OAS.Repository.EnquiryRepository;
import com.myproject.OAS.Repository.UsersRepository;

import jakarta.mail.Session;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {
	
	@Autowired
	private EnquiryRepository enquiryRepo;
	
	@Autowired
	private UsersRepository userRepo;
	
	

	
	@GetMapping("/")  
	public String showIndex() {
		return "index";
	}
	
	
	@GetMapping("/ContactUs")
	public String ShowContactUs(Model model) 
	{
		Enquiry enquiry = new Enquiry();
		model.addAttribute("enquiry", enquiry);
		return "contactus";
		
	}
	
	
	@PostMapping("/ContactUs")
	public String ContactUs(@ModelAttribute("enquiry") Enquiry enquiry , RedirectAttributes attributes)
	{
		try {
			enquiry.setEnquiryDate(LocalDateTime.now());
			enquiryRepo.save(enquiry);
			attributes.addFlashAttribute("msg", "Enquiry Successfully Submitted");
			
			return "redirect:/ContactUs";
		} catch (Exception e) {
			System.err.println("Error : "+e.getMessage());
			return "redirect:/ContactUs";
		}
	}
	
	


	 @GetMapping("/login")
	    public String loginPage() {
	        return "login";
	    }
	 
//	@PostMapping("/login")
//	 public String login(HttpServletRequest request, RedirectAttributes attributes,HttpSession session) {
//		 String role = request.getParameter("role");
//		 String userId = request.getParameter("userid");
//		 String password = request.getParameter("password");
//		 
//		 try {
//			 if(role.equals("ADMIN") && userRepo.existsByEmail(userId)) {
//				 Users admin = userRepo.findByEmail(userId);
//				 
//				 if(password.equals(admin.getPassword()) && admin.getRole().equals(UserRole.ADMIN)) {
//					 session.setAttribute("loggedInAdmin", admin);
//					 System.out.println("valid admin");
//					 return "redirect:/Admin/Dashboard";
//				 }
//			 }
//			 return "redirect:/login";
//		} catch (Exception e) {
//			// TODO: handle exception
//			attributes.addFlashAttribute("mgs", "Something Wents Wrongs");
//			return "redirect:/login";
//		}
//	 }
//	 
	 
	 @PostMapping("/login")
	
	 public String Login(RedirectAttributes attribute, HttpServletRequest request, HttpSession session) {
	     try {
	         String userType = request.getParameter("role");   
	         String userId   = request.getParameter("userid"); 
	         String password = request.getParameter("password");

	         if (userType != null && userType.equalsIgnoreCase(UserRole.ADMIN.name())) {
	             if (userRepo.existsByEmail(userId)) {
	                 Users admin = userRepo.findByEmail(userId);
	                 if (admin != null 
	                     && password.equals(admin.getPassword()) 
	                     && admin.getRole().equals(UserRole.ADMIN)) {

	                     session.setAttribute("loggedInAdmin", admin);
	                     System.err.println("✅ Valid Admin Login");
	                     return "redirect:/Admin/Dashboard";
	                 } else {
	                     attribute.addFlashAttribute("msg", "❌ Invalid Admin Credentials!");
	                     return "redirect:/login";
	                 }
	             } else {
	                 attribute.addFlashAttribute("msg", "❌ Admin User not found!");
	                 return "redirect:/login";
	             }
	         }

	         if (userType != null && userType.equalsIgnoreCase(UserRole.STUDENT.name())) {
	             if (userRepo.existsByEmail(userId)) {
	                 Users student = userRepo.findByEmail(userId);
	                 if (student != null 
	                     && password.equals(student.getPassword()) 
	                     && student.getRole().equals(UserRole.STUDENT)) {

	                     session.setAttribute("loggedInStudent", student);
	                     System.err.println("✅ Valid Student Login");
	                     return "redirect:/Student/Dashboard";
	                 } else {
	                     attribute.addFlashAttribute("msg", "❌ Invalid Student Credentials!");
	                     return "redirect:/login";
	                 }
	             } else {
	                 attribute.addFlashAttribute("msg", "❌ Student User not found!");
	                 return "redirect:/login";
	             }
	         }

	         attribute.addFlashAttribute("msg", "⚠️ Please select a valid role.");
	         return "redirect:/login";

	     } catch (Exception e) {
	         attribute.addFlashAttribute("msg", "Error: " + e.getMessage());
	         return "redirect:/login";
	     }
	 }
	 
	 
	 

	


	
	

	
	
	
}
