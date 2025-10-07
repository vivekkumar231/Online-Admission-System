package com.myproject.OAS.Controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.myproject.OAS.Model.StudyMaterial;
import com.myproject.OAS.Model.Users;
import com.myproject.OAS.Model.StudyMaterial.MaterialType;
import com.myproject.OAS.Repository.StudyMaterialRepository;
import com.myproject.OAS.Repository.UsersRepository;

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
        model.addAttribute("activePage", "dashboard");

        return "Student/Dashboard"; // templates/Student/Dashboard.html file
    }



    // ✅ Study Material Page
    @GetMapping("/StudyMaterial")
    public String showStudyMaterial(Model model) {
        Users student = (Users) session.getAttribute("loggedInStudent");
        if (student == null) return "redirect:/login";

        List<StudyMaterial> studyMaterials = materialRepo.findAllByMaterialTypeAndProgramAndBranchAndYear(
                MaterialType.Study_Material, student.getProgram(), student.getBranch(), student.getYear());

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
    
   

}
