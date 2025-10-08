package com.myproject.OAS.Controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.myproject.OAS.Model.StudyMaterial;
import com.myproject.OAS.Model.Users;
import com.myproject.OAS.Model.StudyMaterial.MaterialType;
import com.myproject.OAS.Repository.StudyMaterialRepository;
import com.myproject.OAS.Repository.UsersRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/Student")
public class StudentController {

    @Autowired
    private HttpSession session;

    @Autowired
    private UsersRepository userRepo;

    @Autowired
    private StudyMaterialRepository materialRepo;

 // ✅ Student Dashboard
    @GetMapping("/Dashboard")
    public String showDashboard(Model model) {
        Users student = (Users) session.getAttribute("loggedInStudent");
        if (student == null) return "redirect:/login";

        model.addAttribute("student", student);
        long  studymaterial = materialRepo.countByMaterialTypeAndProgramAndBranchAndYear(MaterialType.Study_Material,student.getProgram(),student.getBranch(),student.getYear());
        long  assignments  = materialRepo.countByMaterialTypeAndProgramAndBranchAndYear(MaterialType.Assignment,student.getProgram(),student.getBranch(),student.getYear());
        
        
        model.addAttribute("studymaterial", studymaterial);
        model.addAttribute("assignments", assignments);
        

        return "Student/Dashboard"; // templates/Student/Dashboard.html file
    }



    // ✅ Study Material Page
    @GetMapping("/StudyMaterial")
    public String showStudyMaterial(Model model) {
        Users student = (Users) session.getAttribute("loggedInStudent");
        if (student == null) {
            return "redirect:/login";
        }

        List<StudyMaterial> studyMaterials = materialRepo.findAllByMaterialTypeAndProgramAndBranchAndYear(
                MaterialType.Study_Material,
                student.getProgram(),
                student.getBranch(),
                student.getYear()
        );

        model.addAttribute("student", student);
        model.addAttribute("studyMaterials", studyMaterials);
        model.addAttribute("activePage", "studymaterial");

        return "Student/StudyMaterial";
    }
    // ✅ Assignment Page
    @GetMapping("/Assignment")
    public String showAssignment(Model model) {
        Users student = (Users) session.getAttribute("loggedInStudent");
        if (student == null) return "redirect:/login";

        List<StudyMaterial> assignments = materialRepo.findAllByMaterialTypeAndProgramAndBranchAndYear(
                MaterialType.Assignment, student.getProgram(), student.getBranch(), student.getYear());

        model.addAttribute("student", student);
        model.addAttribute("assignments", assignments);
        model.addAttribute("activePage", "assignment");

        return "Student/Assignment";
    }

    // ✅ View Profile Page
    @GetMapping("/ViewProfile")
    public String showViewProfile(Model model) {
        Users student = (Users) session.getAttribute("loggedInStudent");
        if (student == null) return "redirect:/login";

        model.addAttribute("student", student);
        model.addAttribute("activePage", "profile");

        return "Student/ViewProfile";
    }
    @GetMapping("/ChangePassword")
	 public String showChangePassword() {
    	Users student = (Users) session.getAttribute("loggedInStudent");
        if (student == null) return "redirect:/login";
	     return "Student/ChangePassword";
	 }
	 
	 
	 @PostMapping("/ChangePassword")
	 public String ChangePassword(HttpServletRequest request, Model model) {
	     try {
	         // Admin session check
	    	 Users loggedInStudent = (Users) session.getAttribute("loggedInStudent");
	         if (loggedInStudent == null) {
	        	 System.out.println("invalid");
	        	 return "redirect:/login";}

	         // Get form parameters
	         String oldPassword = request.getParameter("oldPassword");
	         String newPassword = request.getParameter("newPassword");
	         String confirmPassword = request.getParameter("confirmPassword");

	         // 1. Verify old password
	         if (!loggedInStudent.getPassword().equals(oldPassword)) {
	             model.addAttribute("msg", "Old password is incorrect!");
	             return "Student/ChangePassword";
	         }

	         // 2. Check new password match
	         if (!newPassword.equals(confirmPassword)) {
	             model.addAttribute("msg", "New password and confirm password do not match!");
	             return "Student/ChangePassword";
	         }

	         // 3. Update password in database
	         loggedInStudent.setPassword(newPassword);
	         userRepo.save(loggedInStudent); // Save updated password

	         // 4. Show success message
	         model.addAttribute("msg", "Password changed successfully!");
	         return "Student/ChangePassword";

	     } catch (Exception e) {
	         e.printStackTrace();
	         model.addAttribute("msg", "Something went wrong. Please try again!");
	         return "Student/ChangePassword";
	     }
	 }
	
    
   

}
