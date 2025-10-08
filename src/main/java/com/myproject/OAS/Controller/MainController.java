package com.myproject.OAS.Controller;

import java.io.File;
import java.io.IOException;
import java.net.http.HttpRequest;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.jar.Attributes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.myproject.OAS.Model.Enquiry;
import com.myproject.OAS.Model.Users;
import com.myproject.OAS.Model.Users.UserRole;
import com.myproject.OAS.Model.Users.UserStatus;
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
	
	@Autowired
	private HttpSession session;
	
	
	  // inject images folder path (from application.properties)
    @Value("${app.upload.dir:${user.dir}/src/main/resources/static/images}")
    private String uploadDir;
	
	@GetMapping("/")  
	public String showIndex() {
		return "index";
	}
	
	
	

    @GetMapping("/Registration")
    public String showRegistration(HttpSession session, Model model) {
        if (session.getAttribute("NewStudent") == null) {
            return "redirect:/login";
        }
        Users student = (Users) session.getAttribute("NewStudent");
        if(student.getUtrNo() != null) {
        	return "redirect:/successfully";
        }
        model.addAttribute("student", student);
        return "Registration";
    }

 @PostMapping("/Registration")
public String updateRegistration(HttpServletRequest request,
                                 @RequestParam("paymentImage") MultipartFile paymentImage,
                                 HttpSession session,
                                 Model model) {

    // 1️⃣ Session se user object le lo
    Users student = (Users) session.getAttribute("NewStudent");
    if (student == null) {
        return "redirect:/login";
    }
   

    // 2️⃣ Form fields manually get karo
    String fatherName = request.getParameter("fatherName");
    String motherName = request.getParameter("motherName");
    String gender     = request.getParameter("gender");
    String address    = request.getParameter("address");
    String utrNo      = request.getParameter("utrNo");

    // 3️⃣ User object me set karo
    student.setFatherName(fatherName);
    student.setMotherName(motherName);
    student.setGender(gender);
    student.setAddress(address);
    student.setUtrNo(utrNo);

    try {
        // 4️⃣ Agar payment image hai
        if (paymentImage != null && !paymentImage.isEmpty()) {

            // Folder path jahan image save hogi
            String uploadDir = "uploads/";  // project root ke andar
            Path uploadPath = Paths.get(uploadDir);

            // Folder agar exist nahi karta to create karo
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // File save karna
            String fileName = paymentImage.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            Files.write(filePath, paymentImage.getBytes());

            // DB me file ka path save karo
            student.setPaymentImage("/" + uploadDir + fileName);
        }

        // 5️⃣ Database me save karo
        userRepo.save(student);

        model.addAttribute("success", "Registration updated successfully!");
    } catch (IOException e) {
        e.printStackTrace();
        model.addAttribute("error", "Error uploading payment image.");
    }

    // 6️⃣ Model me student wapas bhej do
    model.addAttribute("student", student);

    return "redirect:/successfully";
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
	             if (userRepo.existsByRollNo(userId)) {
	                 Users student = userRepo.findByRollNo(userId);
	                 if (student != null 
	                     && password.equals(student.getPassword()) 
	                     && student.getRole().equals(UserRole.STUDENT) && student.getStatus().equals(UserStatus.APPROVED)) {

	                     session.setAttribute("loggedInStudent", student);
	                     //System.err.println("✅ Valid Student Login");
	                     return "redirect:/Student/Dashboard";
	                 } else if(student != null 
	                     && password.equals(student.getPassword()) 
	                     && student.getRole().equals(UserRole.STUDENT) && student.getStatus().equals(UserStatus.PENDING)) {
	                	 session.setAttribute("NewStudent", student);
	                    // System.err.println("✅ Valid Student Login");
	                     return "redirect:/Registration";
	                 }
	                 else if(student != null 
		                     && password.equals(student.getPassword()) 
		                     && student.getRole().equals(UserRole.STUDENT) && student.getStatus().equals(UserStatus.DISABLED)) {
	                	 attribute.addFlashAttribute("msg", "❌ Disables Student Credentials! please Contact Admin");
	                     return "redirect:/login";
		                 }
	                 else {
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
	 
	 
	 

	 
	 @GetMapping("/logout")
	 public String showLogout() {
		 session.invalidate();
		 return "redirect:/login";
	 }
	 
	 
	 @GetMapping("/successfully")
	 public String showsuccessfully() {
		 return "/successfully";
	 }
	 

	
  @GetMapping("/Park")
  public String showPark() {
	  return "/ThePark";
  }
  
  
  @GetMapping("/Facilities")
  public String showFacilities() {
	  return "/Facilities";
  }
  

  @GetMapping("/news-events")
  public String showNewsEvents() {
      return "NewsEvents"; // File name in templates folder (without .html)
  }


	
	

	
	
	
}
