package com.myproject.OAS.Controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.myproject.OAS.Model.Enquiry;
import com.myproject.OAS.Model.Users;
import com.myproject.OAS.Model.Users.UserRole;
import com.myproject.OAS.Model.Users.UserStatus;
import com.myproject.OAS.Repository.EnquiryRepository;
import com.myproject.OAS.Repository.UsersRepository;

import jakarta.servlet.http.HttpServletRequest;
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
			return "redirect:/login";
		}
		
		return "Admin/Dashboard";
	}
	
	
	@GetMapping("/AddStudent")
	public String ShowAddStudent(Model model) 
	{
		if (session.getAttribute("loggedInAdmin") == null) {
			return "redirect:/login";
			
		}
		Users student = new Users();
		model.addAttribute("student", student);
		
		return "Admin/AddStudent";
	}
	
	@PostMapping("/AddStudent")
	public String AddStudent(@ModelAttribute("student") Users student, RedirectAttributes attributes) {
	    try {
	        // Check if email already exists
	        if (userRepo.existsByEmail(student.getEmail())) {
	            attributes.addFlashAttribute("msg", "User already exists!!");
	            return "redirect:/Admin/AddStudent";                
	        }

	        // Set default values
	        student.setPassword("Password123");
	        student.setRole(UserRole.STUDENT);
	        student.setStatus(UserStatus.PENDING);
	        student.setRollNo("E" + System.currentTimeMillis()); // unique enrollment number
	        student.setRegDate(LocalDateTime.now());

	        // Save student
	        userRepo.save(student);

	        // Success message
	        attributes.addFlashAttribute(
	            "msg", 
	            "Registration Successful, Enrollment No: " + student.getRollNo() + 
	            ", Password: " + student.getPassword()
	        );

	        return "redirect:/Admin/AddStudent";

	    } catch (Exception e) {
	        e.printStackTrace(); // for debugging
	        attributes.addFlashAttribute("msg", "Error: " + e.getMessage());
	        return "redirect:/Admin/AddStudent";
	    }
	}
	
	@GetMapping("/NewStudents")
	public String ShowNewStudents(Model model) 
	{
		if (session.getAttribute("loggedInAdmin") == null) {
			return "redirect:/login";
			
		}
		List<Users> newStudents = userRepo.findAllByRoleAndStatus(UserRole.STUDENT, UserStatus.PENDING);
		model.addAttribute("newStudents", newStudents);
		
		return "Admin/NewStudents";
	}
	
	@GetMapping("/ApproveStudent")
	public String approvedStudent(@RequestParam("id") long id, RedirectAttributes attr) {
	    if (session.getAttribute("loggedInAdmin") == null) {
	        return "redirect:/login";
	    }
	    try {
	        Users student = userRepo.findById(id)
	            .orElseThrow(() -> new IllegalArgumentException("Invalid Student ID:" + id));

	        student.setStatus(UserStatus.APPROVED);
	        userRepo.save(student);

	        attr.addFlashAttribute("msg", "Student " + student.getName() + " approved successfully and email sent.");
	    } catch (Exception e) {
	        attr.addFlashAttribute("msg", "Error approving student " + e.getMessage());
	    }
	    return "redirect:/Admin/ManageStudents";  // better to redirect back to student list
	}
	
//	@GetMapping("/UploadMaterial")
//	 public String uploadMaterial(HttpSession session) {
//	     if (session.getAttribute("loggedInAdmin") == null) {
//	         return "redirect:/login";  // Redirect if not logged in
//	     }
//	     return "Admin/UploadMaterial"; // Thymeleaf template
//	 }
	
	
	@GetMapping("/UploadMaterial")
	public String ShowUploadMaterial(Model model)
	{
		if(session.getAttribute("loggedInAdmin") == null) {
			return "redirect:/Login";
		}
		return "Admin/UploadMaterial";
		
	}


	 
	

	
	


	
	
	
	@GetMapping("/Enquiry")
	public String ShowEnquiry(Model model) {
		if (session.getAttribute("loggedInAdmin") == null) {
			return "redirect:/login";
			
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
	 
	 

	 
	 
	 
	 
	 
	 
	 
	 
	 @GetMapping("/ChangePassword")
	 public String showChangePassword() {
	     if (session.getAttribute("loggedInAdmin") == null) {
	         return "redirect:/login";
	     }
	     return "Admin/ChangePassword";
	 }
	 
	 
	 @PostMapping("/ChangePassword")
	 public String ChangePassword(HttpServletRequest request, Model model) {
	     try {
	         // Admin session check
	         Users loggedInAdmin = (Users) request.getSession().getAttribute("loggedInAdmin");
	         if (loggedInAdmin == null) {
	             return "redirect:/login";
	         }

	         // Get form parameters
	         String oldPassword = request.getParameter("oldPassword");
	         String newPassword = request.getParameter("newPassword");
	         String confirmPassword = request.getParameter("confirmPassword");

	         // 1. Verify old password
	         if (!loggedInAdmin.getPassword().equals(oldPassword)) {
	             model.addAttribute("msg", "Old password is incorrect!");
	             return "Admin/ChangePassword";
	         }

	         // 2. Check new password match
	         if (!newPassword.equals(confirmPassword)) {
	             model.addAttribute("msg", "New password and confirm password do not match!");
	             return "Admin/ChangePassword";
	         }

	         // 3. Update password in database
	         loggedInAdmin.setPassword(newPassword);
	         userRepo.save(loggedInAdmin); // Save updated password

	         // 4. Show success message
	         model.addAttribute("msg", "Password changed successfully!");
	         return "Admin/ChangePassword";

	     } catch (Exception e) {
	         e.printStackTrace();
	         model.addAttribute("msg", "Something went wrong. Please try again!");
	         return "Admin/ChangePassword";
	     }
	 }
	

	 @GetMapping("/ManageStudents")
	 public String showManageStudents(Model model) {
	     if (session.getAttribute("loggedInAdmin") == null) {
	         return "redirect:/login";
	     }
	     List<Users> students = userRepo.findAllByRoleAndStatusOrStatus(
	             UserRole.STUDENT,
	             UserStatus.APPROVED,
	             UserStatus.DISABLED
	     );
	     model.addAttribute("student", students);
	     return "Admin/ManageStudents";
	 }
	 

	 
	 

	
}








