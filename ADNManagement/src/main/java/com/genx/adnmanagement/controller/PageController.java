package com.genx.adnmanagement.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.genx.adnmanagement.entity.User;
import com.genx.adnmanagement.repository.ServiceRepository;
import com.genx.adnmanagement.entity.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import java.text.Normalizer;
import java.util.ArrayList;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class PageController {
    @Autowired
    private ServiceRepository serviceRepository;

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

    private String requireLogin(Model model, HttpSession session, String viewName, HttpServletRequest request) {
        if (session.getAttribute("user") == null) {
            // Lưu URL hiện tại để redirect sau khi login
            String currentUrl = request.getRequestURI() + (request.getQueryString() != null ? "?" + request.getQueryString() : "");
            session.setAttribute("redirectAfterLogin", currentUrl);
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
    private String requireStaffLogin(Model model, HttpSession session, String viewName, HttpServletRequest request) {
        if (session.getAttribute("staff") == null) {
            // Lưu URL hiện tại để redirect sau khi login
            String currentUrl = request.getRequestURI() + (request.getQueryString() != null ? "?" + request.getQueryString() : "");
            session.setAttribute("redirectAfterStaffLogin", currentUrl);
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
    public String booking(Model model, HttpSession session, HttpServletRequest request) {
        return requireLogin(model, session, "booking", request);
    }

    @GetMapping("/services")
    public String services(Model model, HttpSession session) {
        addUserDataToModel(model, session);
        List<Service> services = serviceRepository.findByIsActiveTrue();
        List<Map<String, Object>> serviceList = new ArrayList<>();
        for (Service service : services) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", service.getName());
            map.put("description", service.getDescription());
            map.put("price", service.getPrice());
            map.put("id", service.getId());
            map.put("bannerImage", toBannerImage(service.getName()));
            serviceList.add(map);
        }
        model.addAttribute("services", serviceList);
        return "services";
    }

    private static String toBannerImage(String serviceName) {
        if (serviceName == null) return "dna-banner.jpg";
        // Bỏ tiền tố
        String name = serviceName.replaceFirst("Xét nghiệm ADN", "").trim();
        // Bỏ dấu tiếng Việt
        name = Normalizer.normalize(name, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        // Chuyển về lower trước, sau đó chỉ giữ lại ký tự alphabet và nối bằng dấu gạch ngang
        name = name.toLowerCase().replaceAll("[^a-z]+", "-").replaceAll("^-+|-+$", "");
        return name + "-banner.jpg";
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
    public String appoinments(Model model, HttpSession session, HttpServletRequest request) {
        return requireLogin(model, session, "appoinments", request);
    }

    @GetMapping("/kits")
    public String kits(Model model, HttpSession session, HttpServletRequest request) {
        return requireLogin(model, session, "kit-management-customer", request);
    }

    @GetMapping("/test-history")
    public String historyTest(Model model, HttpSession session, HttpServletRequest request) {
        return requireLogin(model, session, "historytest", request);
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
    public String staffHome(Model model, HttpSession session, HttpServletRequest request) {
        return requireStaffLogin(model, session, "staff-index", request);
    }

    @GetMapping("/staff-booking")
    public String staffBooking(Model model, HttpSession session, HttpServletRequest request) {
        return requireStaffLogin(model, session, "staff-booking", request);
    }

    @GetMapping("/staff-appointments")
    public String staffAppointments(Model model, HttpSession session, HttpServletRequest request) {
        return requireStaffLogin(model, session, "staff-appoinments", request);
    }

    @GetMapping("/staff-kit-list")
    public String staffKitList(Model model, HttpSession session, HttpServletRequest request) {
        return requireStaffLogin(model, session, "list-kit", request);
    }

    @GetMapping("/staff-participants")
    public String staffParticipants(Model model, HttpSession session, HttpServletRequest request) {
        return requireStaffLogin(model, session, "participants-management", request);
    }

    @GetMapping("/staff-samples")
    public String staffSamples(Model model, HttpSession session, HttpServletRequest request) {
        return requireStaffLogin(model, session, "sample-management", request);
    }

    @GetMapping("/staff-results")
    public String staffResults(Model model, HttpSession session, HttpServletRequest request) {
        return requireStaffLogin(model, session, "results-management", request);
    }

    @GetMapping("/edit-appointment")
    public String appointmentDetails(Model model, HttpSession session, HttpServletRequest request) {
        return requireStaffLogin(model, session, "editappointment", request);
    }
}