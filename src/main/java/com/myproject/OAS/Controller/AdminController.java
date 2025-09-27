package com.myproject.OAS.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.myproject.OAS.Model.Enquiry;
import com.myproject.OAS.Repository.EnquiryRepository;
import com.myproject.OAS.Repository.UsersRepository;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/Admin")
public class AdminController {
	
	@Autowired
	private HttpSession session;

	@Autowired
	private UsersRepository userRepo;
	
	@Autowired
	private EnquiryRepository enquiryRepo;
	
	@GetMapping("/Dashboard")
	public String showDashobard() 
	{
		if (session.getAttribute("loggedInAdmin") == null) {
			return "redirect:/Login";
		}
		
		return "Admin/Dashboard";
	}
	
	
	@GetMapping("/AddStudent")
	public String ShowAddStudent() 
	{
		if (session.getAttribute("loggedInAdmin") == null) {
			return "redirect:/Login";
			
		}
		return "Admin/AddStudent";
	}
	
	@GetMapping("/Enquiry")
	public String ShowEnquiry(Model model) {
		if (session.getAttribute("loggedInAdmin") == null) {
			return "redirect:/Login";
			
		}
		List<Enquiry> enquiries = enquiryRepo.findAll();
		model.addAttribute("enquiries", enquiries);
		return "Admin/Enquiry";
		
	}
	
	 @GetMapping("/DeleteEnquiry")
	 public String DeleteEnquiry(@RequestParam("id") long id)
	 {
		  enquiryRepo.deleteById(id);
		  return "redirect:/Admin/Enquiry";
	}
	
}








