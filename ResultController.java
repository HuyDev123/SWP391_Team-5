package com.genx.adnmanagement.controller;

import com.genx.adnmanagement.entity.TestResult;
import com.genx.adnmanagement.entity.User;
import com.genx.adnmanagement.service.TestResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/results")
public class ResultController {

    @Autowired
    private TestResultService testResultService;

    // Staff Results Management Page
    @GetMapping("/management")
    public String resultsManagement(
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "technician", required = false) String technician,
            @RequestParam(value = "search", required = false) String search,
            Model model, HttpSession session) {
        
        // Check staff session
        User staff = (User) session.getAttribute("staff");
        if (staff == null) {
            return "redirect:/internal-login";
        }

        List<TestResult> results;
        
        if (search != null && !search.trim().isEmpty()) {
            results = testResultService.searchResults(search);
        } else {
            results = testResultService.filterByStatusAndTechnician(status, technician);
        }

        // Get statistics
        Map<String, Long> statusStats = testResultService.getStatusStatistics();
        
        model.addAttribute("results", results);
        model.addAttribute("statusStats", statusStats);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("selectedTechnician", technician);
        model.addAttribute("searchKeyword", search);
        model.addAttribute("staff", staff);
        
        // Counts for dashboard
        model.addAttribute("pendingCount", testResultService.countByStatus("PENDING"));
        model.addAttribute("completedCount", testResultService.countByStatus("COMPLETED"));
        model.addAttribute("reviewedCount", testResultService.countByStatus("REVIEWED"));
        model.addAttribute("deliveredCount", testResultService.countByStatus("DELIVERED"));

        return "results-management";
    }

    // Create new test result
    @PostMapping("/create")
    public String createResult(
            @RequestParam Integer bookingId,
            @RequestParam Integer serviceId,
            RedirectAttributes redirectAttributes,
            HttpSession session) {
        
        User staff = (User) session.getAttribute("staff");
        if (staff == null) {
            return "redirect:/internal-login";
        }

        try {
            TestResult result = testResultService.createTestResult(bookingId, serviceId);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Tạo kết quả xét nghiệm thành công! Mã: " + result.getResultCode());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Lỗi tạo kết quả: " + e.getMessage());
        }

        return "redirect:/results/management";
    }

    // View/Edit result details
    @GetMapping("/edit/{id}")
    public String editResult(@PathVariable Integer id, Model model, HttpSession session) {
        User staff = (User) session.getAttribute("staff");
        if (staff == null) {
            return "redirect:/internal-login";
        }

        Optional<TestResult> resultOpt = testResultService.findById(id);
        if (resultOpt.isPresent()) {
            TestResult result = resultOpt.get();
            model.addAttribute("result", result);
            model.addAttribute("canEdit", testResultService.canEditResult(result));
            model.addAttribute("canReview", testResultService.canReviewResult(result));
            model.addAttribute("canDeliver", testResultService.canDeliverResult(result));
            model.addAttribute("staff", staff);
            return "edit-result";
        }

        return "redirect:/results/management";
    }

    // Update test result
    @PostMapping("/update/{id}")
    public String updateResult(
            @PathVariable Integer id,
            @RequestParam BigDecimal probability,
            @RequestParam String conclusion,
            @RequestParam String detailedAnalysis,
            @RequestParam String testMethod,
            @RequestParam String labTechnician,
            RedirectAttributes redirectAttributes,
            HttpSession session) {
        
        User staff = (User) session.getAttribute("staff");
        if (staff == null) {
            return "redirect:/internal-login";
        }

        try {
            testResultService.updateResult(id, probability, conclusion, detailedAnalysis, testMethod, labTechnician);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật kết quả thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi cập nhật: " + e.getMessage());
        }

        return "redirect:/results/edit/" + id;
    }

    // Review result
    @PostMapping("/review/{id}")
    public String reviewResult(
            @PathVariable Integer id,
            @RequestParam String notes,
            RedirectAttributes redirectAttributes,
            HttpSession session) {
        
        User staff = (User) session.getAttribute("staff");
        if (staff == null) {
            return "redirect:/internal-login";
        }

        try {
            testResultService.reviewResult(id, staff.getFullName(), notes);
            redirectAttributes.addFlashAttribute("successMessage", "Duyệt kết quả thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi duyệt kết quả: " + e.getMessage());
        }

        return "redirect:/results/edit/" + id;
    }

    // Deliver result
    @PostMapping("/deliver/{id}")
    public String deliverResult(
            @PathVariable Integer id,
            @RequestParam String pdfFilePath,
            RedirectAttributes redirectAttributes,
            HttpSession session) {
        
        User staff = (User) session.getAttribute("staff");
        if (staff == null) {
            return "redirect:/internal-login";
        }

        try {
            testResultService.deliverResult(id, pdfFilePath);
            redirectAttributes.addFlashAttribute("successMessage", "Giao kết quả thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi giao kết quả: " + e.getMessage());
        }

        return "redirect:/results/edit/" + id;
    }

    // API endpoints for AJAX requests
    @GetMapping("/api/search")
    @ResponseBody
    public ResponseEntity<List<TestResult>> searchResults(@RequestParam String keyword) {
        List<TestResult> results = testResultService.searchResults(keyword);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/api/filter")
    @ResponseBody
    public ResponseEntity<List<TestResult>> filterResults(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String technician) {
        List<TestResult> results = testResultService.filterByStatusAndTechnician(status, technician);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/api/stats")
    @ResponseBody
    public ResponseEntity<Map<String, Long>> getStatistics() {
        Map<String, Long> stats = testResultService.getStatusStatistics();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/api/pending")
    @ResponseBody
    public ResponseEntity<List<TestResult>> getPendingResults() {
        List<TestResult> results = testResultService.findPendingResults();
        return ResponseEntity.ok(results);
    }

    @GetMapping("/api/need-review")
    @ResponseBody
    public ResponseEntity<List<TestResult>> getResultsNeedingReview() {
        List<TestResult> results = testResultService.findResultsNeedingReview();
        return ResponseEntity.ok(results);
    }

    // Customer result view
    @GetMapping("/view/{resultCode}")
    public String viewResult(@PathVariable String resultCode, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        Optional<TestResult> resultOpt = testResultService.findByResultCode(resultCode);
        if (resultOpt.isPresent()) {
            TestResult result = resultOpt.get();
            // Check if this result belongs to the user
            if (result.getBooking().getCustomer().getId().equals(user.getId())) {
                model.addAttribute("result", result);
                model.addAttribute("user", user);
                return "view-result";
            }
        }

        return "redirect:/test-history";
    }

    @GetMapping("/api/by-booking/{bookingId}")
    @ResponseBody
    public ResponseEntity<List<TestResult>> getResultsByBooking(@PathVariable Integer bookingId) {
        List<TestResult> results = testResultService.findByBookingId(bookingId);
        return ResponseEntity.ok(results);
    }
} 