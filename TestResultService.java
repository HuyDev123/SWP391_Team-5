package com.genx.adnmanagement.service;

import com.genx.adnmanagement.entity.TestResult;
import com.genx.adnmanagement.entity.Booking;
import com.genx.adnmanagement.repository.TestResultRepository;
import com.genx.adnmanagement.repository.BookingRepository;
import com.genx.adnmanagement.repository.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;
import java.math.BigDecimal;

@Service
@Transactional
public class TestResultService {

    @Autowired
    private TestResultRepository testResultRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    // CRUD operations
    public TestResult save(TestResult testResult) {
        if (testResult.getResultCode() == null || testResult.getResultCode().isEmpty()) {
            testResult.setResultCode(generateResultCode());
        }
        testResult.setUpdatedAt(LocalDateTime.now());
        return testResultRepository.save(testResult);
    }

    public Optional<TestResult> findById(Integer id) {
        return testResultRepository.findById(id);
    }

    public List<TestResult> findAll() {
        return testResultRepository.findAll();
    }

    public void deleteById(Integer id) {
        testResultRepository.deleteById(id);
    }

    // Business logic methods
    public TestResult createTestResult(Integer bookingId, Integer serviceId) {
        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        Optional<com.genx.adnmanagement.entity.Service> serviceOpt = serviceRepository.findById(serviceId);

        if (bookingOpt.isPresent() && serviceOpt.isPresent()) {
            TestResult testResult = new TestResult(bookingOpt.get(), serviceOpt.get(), generateResultCode());
            return save(testResult);
        }
        throw new RuntimeException("Booking or Service not found");
    }

    public TestResult updateResult(Integer id, BigDecimal probability, String conclusion, 
                                  String detailedAnalysis, String testMethod, String labTechnician) {
        Optional<TestResult> resultOpt = testResultRepository.findById(id);
        if (resultOpt.isPresent()) {
            TestResult result = resultOpt.get();
            result.setProbabilityPercentage(probability);
            result.setConclusion(conclusion);
            result.setDetailedAnalysis(detailedAnalysis);
            result.setTestMethod(testMethod);
            result.setLabTechnician(labTechnician);
            result.setResultDate(LocalDateTime.now());
            result.setStatus("COMPLETED");
            return save(result);
        }
        throw new RuntimeException("Test result not found");
    }

    public TestResult reviewResult(Integer id, String reviewedBy, String notes) {
        Optional<TestResult> resultOpt = testResultRepository.findById(id);
        if (resultOpt.isPresent()) {
            TestResult result = resultOpt.get();
            result.setReviewedBy(reviewedBy);
            result.setNotes(notes);
            result.setStatus("REVIEWED");
            return save(result);
        }
        throw new RuntimeException("Test result not found");
    }

    public TestResult deliverResult(Integer id, String pdfFilePath) {
        Optional<TestResult> resultOpt = testResultRepository.findById(id);
        if (resultOpt.isPresent()) {
            TestResult result = resultOpt.get();
            result.setPdfFilePath(pdfFilePath);
            result.setStatus("DELIVERED");
            return save(result);
        }
        throw new RuntimeException("Test result not found");
    }

    // Query methods
    public List<TestResult> findByBookingId(Integer bookingId) {
        return testResultRepository.findByBookingIdOrderByCreatedAtDesc(bookingId);
    }

    public List<TestResult> findByStatus(String status) {
        return testResultRepository.findByStatus(status);
    }

    public List<TestResult> findPendingResults() {
        return testResultRepository.findPendingResults();
    }

    public List<TestResult> findResultsNeedingReview() {
        return testResultRepository.findResultsNeedingReview();
    }

    public List<TestResult> findByCustomerName(String customerName) {
        return testResultRepository.findByCustomerNameContaining(customerName);
    }

    public List<TestResult> findByLabTechnician(String technician) {
        return testResultRepository.findByLabTechnician(technician);
    }

    public Optional<TestResult> findByResultCode(String resultCode) {
        return testResultRepository.findByResultCode(resultCode);
    }

    // Statistics
    public Map<String, Long> getStatusStatistics() {
        List<Object[]> stats = testResultRepository.getStatusStatistics();
        Map<String, Long> result = new HashMap<>();
        
        for (Object[] stat : stats) {
            String status = (String) stat[0];
            Long count = (Long) stat[1];
            result.put(status, count);
        }
        
        return result;
    }

    public long countByStatus(String status) {
        return testResultRepository.countByStatus(status);
    }

    // Utility methods
    private String generateResultCode() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String randomNum = String.format("%03d", (int) (Math.random() * 1000));
        return "TR" + timestamp + randomNum;
    }

    public boolean canEditResult(TestResult testResult) {
        return "PENDING".equals(testResult.getStatus()) || "COMPLETED".equals(testResult.getStatus());
    }

    public boolean canReviewResult(TestResult testResult) {
        return "COMPLETED".equals(testResult.getStatus());
    }

    public boolean canDeliverResult(TestResult testResult) {
        return "REVIEWED".equals(testResult.getStatus());
    }

    // Search and filter
    public List<TestResult> searchResults(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAll();
        }
        return testResultRepository.findByCustomerNameContaining(keyword.trim());
    }

    public List<TestResult> filterByStatusAndTechnician(String status, String technician) {
        if (status != null && !status.isEmpty() && technician != null && !technician.isEmpty()) {
            return testResultRepository.findByStatus(status)
                .stream()
                .filter(r -> technician.equals(r.getLabTechnician()))
                .toList();
        } else if (status != null && !status.isEmpty()) {
            return testResultRepository.findByStatus(status);
        } else if (technician != null && !technician.isEmpty()) {
            return testResultRepository.findByLabTechnician(technician);
        }
        return findAll();
    }
} 