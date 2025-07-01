package com.genx.adnmanagement.controller;

import com.genx.adnmanagement.entity.TestResult;
import com.genx.adnmanagement.entity.Booking;
import com.genx.adnmanagement.entity.Service;
import com.genx.adnmanagement.entity.User;
import com.genx.adnmanagement.repository.TestResultRepository;
import com.genx.adnmanagement.repository.BookingRepository;
import com.genx.adnmanagement.repository.ServiceRepository;
import com.genx.adnmanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;
import org.springframework.http.MediaType;
import java.io.File;
import java.util.HashMap;

@RestController
@RequestMapping("/results")
public class ResultController {
    @Autowired
    private TestResultRepository testResultRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private ServiceRepository serviceRepository;
    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public List<TestResult> getAllResults() {
        return testResultRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TestResult> getResultById(@PathVariable Integer id) {
        Optional<TestResult> result = testResultRepository.findById(id);
        return result.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<TestResult> createResult(@RequestParam Integer bookingId,
                                                   @RequestParam Integer serviceId,
                                                   @RequestParam(required = false) String resultFile,
                                                   @RequestParam(required = false) Integer userId) {
        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        Optional<Service> serviceOpt = serviceRepository.findById(serviceId);
        User user = null;
        if (userId != null) {
            user = userRepository.findById(userId).orElse(null);
        }
        if (bookingOpt.isPresent() && serviceOpt.isPresent()) {
            TestResult result = new TestResult();
            result.setBooking(bookingOpt.get());
            result.setService(serviceOpt.get());
            result.setResultFile(resultFile);
            result.setUser(user);
            return ResponseEntity.ok(testResultRepository.save(result));
        }
        return ResponseEntity.badRequest().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<TestResult> updateResult(@PathVariable Integer id,
                                                   @RequestParam(required = false) String resultFile,
                                                   @RequestParam(required = false) Integer userId) {
        Optional<TestResult> resultOpt = testResultRepository.findById(id);
        if (resultOpt.isPresent()) {
            TestResult result = resultOpt.get();
            if (resultFile != null) result.setResultFile(resultFile);
            if (userId != null) {
                User user = userRepository.findById(userId).orElse(null);
                result.setUser(user);
            }
            return ResponseEntity.ok(testResultRepository.save(result));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResult(@PathVariable Integer id) {
        if (testResultRepository.existsById(id)) {
            testResultRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * API lấy file kết quả cho 1 dịch vụ trong 1 booking
     * @param bookingId id của booking
     * @param serviceId id của service
     * @return thông tin file kết quả (nếu có), hoặc trạng thái chưa có file
     */
    @GetMapping("/file")
    public ResponseEntity<?> getResultFileForService(@RequestParam Integer bookingId, @RequestParam Integer serviceId) {
        Optional<TestResult> resultOpt = testResultRepository.findByBookingIdAndServiceId(bookingId, serviceId);
        if (resultOpt.isPresent()) {
            TestResult result = resultOpt.get();
            if (result.getResultFile() != null && !result.getResultFile().isBlank()) {
                // Có file, trả về link hoặc tên file
                return ResponseEntity.ok().body(new java.util.HashMap<String, Object>() {{
                    put("hasFile", true);
                    put("fileName", result.getResultFile());
                    put("fileUrl", "/uploads/results/" + result.getResultFile());
                }});
            } else {
                return ResponseEntity.ok().body(new java.util.HashMap<String, Object>() {{
                    put("hasFile", false);
                    put("message", "Chưa có file kết quả cho dịch vụ này");
                }});
            }
        } else {
            return ResponseEntity.ok().body(new java.util.HashMap<String, Object>() {{
                put("hasFile", false);
                put("message", "Chưa có file kết quả cho dịch vụ này");
            }});
        }
    }

    /**
     * API upload file PDF kết quả
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadResultFile(
            @RequestParam("bookingId") Integer bookingId,
            @RequestParam("serviceId") Integer serviceId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "userId", required = false) Integer userId
    ) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("No file selected");
            }
            if (!file.getContentType().equalsIgnoreCase("application/pdf")) {
                return ResponseEntity.badRequest().body("Chỉ chấp nhận file PDF");
            }
            // Sử dụng đường dẫn tuyệt đối cho uploadDir
            String uploadDir = System.getProperty("user.dir") + File.separator + "uploads" + File.separator + "results" + File.separator;
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                boolean created = dir.mkdirs();
            }
            String fileName = System.currentTimeMillis() + "_" + org.springframework.util.StringUtils.cleanPath(file.getOriginalFilename());
            File dest = new File(uploadDir + fileName);
            file.transferTo(dest);

            // Lưu vào DB
            Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
            Optional<Service> serviceOpt = serviceRepository.findById(serviceId);
            User user = null;
            if (userId != null) {
                user = userRepository.findById(userId).orElse(null);
            }
            if (bookingOpt.isPresent() && serviceOpt.isPresent()) {
                Optional<TestResult> resultOpt = testResultRepository.findByBookingIdAndServiceId(bookingId, serviceId);
                TestResult result = resultOpt.orElseGet(TestResult::new);
                result.setBooking(bookingOpt.get());
                result.setService(serviceOpt.get());
                result.setResultFile(fileName);
                result.setUser(user);
                testResultRepository.save(result);
                return ResponseEntity.ok(new java.util.HashMap<String, Object>() {{
                    put("fileName", fileName);
                    put("fileUrl", "/uploads/results/" + fileName);
                    put("testResultId", result.getId());
                }});
            } else {
                return ResponseEntity.badRequest().body("Booking hoặc Service không tồn tại");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Upload failed: " + e.getMessage());
        }
    }
} 