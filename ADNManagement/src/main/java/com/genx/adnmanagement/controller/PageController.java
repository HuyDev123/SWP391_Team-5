package com.genx.adnmanagement.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.genx.adnmanagement.entity.User;

import java.util.HashMap;
import java.util.Map;

@Controller
public class PageController {

    /**
     * Phương thức trợ giúp để thêm dữ liệu người dùng vào model nếu đã đăng nhập
     */
    private void addUserDataToModel(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            Map<String, Object> userData = new HashMap<>();
            userData.put("id", user.getId());
            userData.put("fullName", user.getFullName());
            userData.put("email", user.getEmail());
            userData.put("roleId", user.getRoleId());

            model.addAttribute("userData", userData);
        }
    }

    private String requireLogin(Model model, HttpSession session, String viewName) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        addUserDataToModel(model, session);
        return viewName;
    }

    /**
     * Thêm dữ liệu nhân viên vào model nếu đã đăng nhập staff
     */
    private void addStaffDataToModel(Model model, HttpSession session) {
        User staff = (User) session.getAttribute("staff");
        if (staff != null) {
            Map<String, Object> staffData = new HashMap<>();
            staffData.put("id", staff.getId());
            staffData.put("fullName", staff.getFullName());
            staffData.put("email", staff.getEmail());
            staffData.put("roleId", staff.getRoleId());
            model.addAttribute("staffData", staffData);
        }
    }

    /**
     * Ràng buộc đăng nhập staff, nếu chưa đăng nhập thì chuyển về /internal-login
     */
    private String requireStaffLogin(Model model, HttpSession session, String viewName) {
        if (session.getAttribute("staff") == null) {
            return "redirect:/internal-login";
        }
        addStaffDataToModel(model, session);
        return viewName;
    }


    @GetMapping({"/", "/home"})
    public String index(Model model, HttpSession session) {
        addUserDataToModel(model, session);
        return "index";
    }

    @GetMapping("/booking")
    public String booking(Model model, HttpSession session) {
        addUserDataToModel(model, session);
        return "booking";
    }

    @GetMapping("/services")
    public String services(Model model, HttpSession session) {
        addUserDataToModel(model, session);
        return "services";
    }

    @GetMapping("/knowledge")
    public String knowledge(Model model, HttpSession session) {
        addUserDataToModel(model, session);
        return "knowledge";
    }

    @GetMapping("/sampling-guide")
    public String samplingGuide(Model model, HttpSession session) {
        addUserDataToModel(model, session);
        return "sample-collection";
    }

    @GetMapping("/login")
    public String logIn(HttpSession session) {
        if (session.getAttribute("user") != null) {
            return "redirect:/home";
        }
        return "login";
    }

    @GetMapping("/appoinments-list")
    public String appoinments(Model model, HttpSession session) {
        return requireLogin(model, session, "appoinments");
    }

    @GetMapping("/test-history")
    public String historyTest(Model model, HttpSession session) {
        return requireLogin(model, session, "historytest");
    }

    @GetMapping("/internal-login")
    public String internalLogin(HttpSession session) {
        if (session.getAttribute("staff") != null) {
            return "redirect:/staff-home";
        }
        return "internal-login";
    }

    // ========== STAFF ENDPOINTS ==========
    
    @GetMapping("/staff-home")
    public String staffHome(Model model, HttpSession session) {
        return requireStaffLogin(model, session, "staff-index");
    }

    @GetMapping("/staff-booking")
    public String staffBooking(Model model, HttpSession session) {
        return requireStaffLogin(model, session, "staff-booking");
    }

    @GetMapping("/staff-appointments")
    public String staffAppointments(Model model, HttpSession session) {
        return requireStaffLogin(model, session, "staff-appoinments");
    }

    @GetMapping("/staff-kit-list")
    public String staffKitList(Model model, HttpSession session) {
        return requireStaffLogin(model, session, "list-kit");
    }

    @GetMapping("/staff-participants")
    public String staffParticipants(Model model, HttpSession session) {
        return requireStaffLogin(model, session, "participants-management");
    }

    @GetMapping("/staff-samples")
    public String staffSamples(Model model, HttpSession session) {
        return requireStaffLogin(model, session, "sample-management");
    }

    @GetMapping("/staff-results")
    public String staffResults(Model model, HttpSession session) {
        return requireStaffLogin(model, session, "results-management");
    }

    @GetMapping("/edit-appointment")
    public String appointmentDetails(Model model, HttpSession session) {
        return requireStaffLogin(model, session, "editappointment");
    }
}