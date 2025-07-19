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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/results")
public class ResultController {
    private static final Logger logger = LoggerFactory.getLogger(ResultController.class);
    
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
        logger.info("Getting all results");
        return testResultRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TestResult> getResultById(@PathVariable Integer id) {
        logger.info("Getting result by id: {}", id);
        Optional<TestResult> result = testResultRepository.findById(id);
        return result.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<TestResult> createResult(@RequestParam Integer bookingId,
                                                   @RequestParam Integer serviceId,
                                                   @RequestParam(required = false) String resultFile,
                                                   @RequestParam(required = false) Integer userId) {
        logger.info("Creating result - bookingId: {}, serviceId: {}, resultFile: {}, userId: {}", 
                   bookingId, serviceId, resultFile, userId);
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
            TestResult savedResult = testResultRepository.save(result);
            logger.info("Created result with id: {}", savedResult.getId());
            return ResponseEntity.ok(savedResult);
        }
        logger.error("Failed to create result - booking or service not found");
        return ResponseEntity.badRequest().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<TestResult> updateResult(@PathVariable Integer id,
                                                   @RequestParam(required = false) String resultFile,
                                                   @RequestParam(required = false) Integer userId) {
        logger.info("Updating result - id: {}, resultFile: {}, userId: {}", id, resultFile, userId);
        Optional<TestResult> resultOpt = testResultRepository.findById(id);
        if (resultOpt.isPresent()) {
            TestResult result = resultOpt.get();
            if (resultFile != null) result.setResultFile(resultFile);
            if (userId != null) {
                User user = userRepository.findById(userId).orElse(null);
                result.setUser(user);
            }
            TestResult savedResult = testResultRepository.save(result);
            logger.info("Updated result with id: {}", savedResult.getId());
            return ResponseEntity.ok(savedResult);
        }
        logger.error("Failed to update result - result not found with id: {}", id);
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResult(@PathVariable Integer id) {
        logger.info("Deleting result with id: {}", id);
        if (testResultRepository.existsById(id)) {
            testResultRepository.deleteById(id);
            logger.info("Deleted result with id: {}", id);
            return ResponseEntity.ok().build();
        }
        logger.error("Failed to delete result - result not found with id: {}", id);
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
        logger.info("Getting result file for bookingId: {}, serviceId: {}", bookingId, serviceId);
        Optional<TestResult> resultOpt = testResultRepository.findByBookingIdAndServiceId(bookingId, serviceId);
        if (resultOpt.isPresent()) {
            TestResult result = resultOpt.get();
            logger.info("Found result: {}", result);
            if (result.getResultFile() != null && !result.getResultFile().isBlank()) {
                // Có file, trả về link hoặc tên file
                HashMap<String, Object> response = new HashMap<>();
                response.put("hasFile", true);
                response.put("fileName", result.getResultFile());
                response.put("fileUrl", "/uploads/results/" + result.getResultFile());
                logger.info("Returning file info: {}", response);
                return ResponseEntity.ok().body(response);
            } else {
                logger.info("Result exists but no file");
                HashMap<String, Object> response = new HashMap<>();
                response.put("hasFile", false);
                response.put("message", "Chưa có file kết quả cho dịch vụ này");
                return ResponseEntity.ok().body(response);
            }
        } else {
            logger.info("No result found for bookingId: {}, serviceId: {}", bookingId, serviceId);
            HashMap<String, Object> response = new HashMap<>();
            response.put("hasFile", false);
            response.put("message", "Chưa có file kết quả cho dịch vụ này");
            return ResponseEntity.ok().body(response);
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
        logger.info("=== UPLOAD RESULT FILE START ===");
        logger.info("bookingId: {}, serviceId: {}, userId: {}", bookingId, serviceId, userId);
        logger.info("File info - name: {}, size: {}, contentType: {}", 
                   file.getOriginalFilename(), file.getSize(), file.getContentType());
        
        try {
            if (file.isEmpty()) {
                logger.error("File is empty");
                return ResponseEntity.badRequest().body("No file selected");
            }
            if (!file.getContentType().equalsIgnoreCase("application/pdf")) {
                logger.error("Invalid file type: {}", file.getContentType());
                return ResponseEntity.badRequest().body("Chỉ chấp nhận file PDF");
            }
            
            // Sử dụng đường dẫn tuyệt đối cho uploadDir
            String uploadDir = System.getProperty("user.dir") + File.separator + "uploads" + File.separator + "results" + File.separator;
            logger.info("Upload directory: {}", uploadDir);
            
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                boolean created = dir.mkdirs();
                logger.info("Created upload directory: {}", created);
            }
            
            String fileName = System.currentTimeMillis() + "_" + StringUtils.cleanPath(file.getOriginalFilename());
            File dest = new File(uploadDir + fileName);
            logger.info("Destination file: {}", dest.getAbsolutePath());
            
            file.transferTo(dest);
            logger.info("File transferred successfully");

            // Lưu vào DB
            Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
            Optional<Service> serviceOpt = serviceRepository.findById(serviceId);
            User user = null;
            if (userId != null) {
                user = userRepository.findById(userId).orElse(null);
            }
            
            logger.info("Booking found: {}, Service found: {}, User found: {}", 
                       bookingOpt.isPresent(), serviceOpt.isPresent(), user != null);
            
            if (bookingOpt.isPresent() && serviceOpt.isPresent()) {
                Optional<TestResult> resultOpt = testResultRepository.findByBookingIdAndServiceId(bookingId, serviceId);
                logger.info("Existing result found: {}", resultOpt.isPresent());
                
                TestResult result = resultOpt.orElseGet(TestResult::new);
                result.setBooking(bookingOpt.get());
                result.setService(serviceOpt.get());
                result.setResultFile(fileName);
                result.setUser(user);
                
                TestResult savedResult = testResultRepository.save(result);
                logger.info("Saved result with id: {}", savedResult.getId());
                
                HashMap<String, Object> response = new HashMap<>();
                response.put("fileName", fileName);
                response.put("fileUrl", "/uploads/results/" + fileName);
                response.put("testResultId", savedResult.getId());
                
                logger.info("=== UPLOAD RESULT FILE SUCCESS ===");
                logger.info("Response: {}", response);
                return ResponseEntity.ok(response);
            } else {
                logger.error("Booking or Service not found");
                return ResponseEntity.badRequest().body("Booking hoặc Service không tồn tại");
            }
        } catch (Exception e) {
            logger.error("=== UPLOAD RESULT FILE ERROR ===", e);
            e.printStackTrace();
            return ResponseEntity.status(500).body("Upload failed: " + e.getMessage());
        }
    }

    /**
     * API test để kiểm tra việc truy cập file
     */
    @GetMapping("/test-file/{fileName}")
    public ResponseEntity<?> testFileAccess(@PathVariable String fileName) {
        logger.info("Testing file access for: {}", fileName);
        try {
            String uploadDir = System.getProperty("user.dir") + File.separator + "uploads" + File.separator + "results" + File.separator;
            File file = new File(uploadDir + fileName);
            logger.info("Checking file: {}", file.getAbsolutePath());
            
            if (file.exists()) {
                HashMap<String, Object> response = new HashMap<>();
                response.put("exists", true);
                response.put("fileName", fileName);
                response.put("fileUrl", "/uploads/results/" + fileName);
                response.put("fileSize", file.length());
                response.put("lastModified", file.lastModified());
                logger.info("File exists: {}", response);
                return ResponseEntity.ok().body(response);
            } else {
                HashMap<String, Object> response = new HashMap<>();
                response.put("exists", false);
                response.put("fileName", fileName);
                response.put("uploadDir", uploadDir);
                logger.info("File does not exist: {}", response);
                return ResponseEntity.ok().body(response);
            }
        } catch (Exception e) {
            logger.error("Error testing file access", e);
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    /**
     * API lấy danh sách services với thông tin kết quả cho 1 booking
     */
    @GetMapping("/booking/{bookingId}/services")
    public ResponseEntity<?> getServicesWithResultsByBookingId(@PathVariable Integer bookingId) {
        logger.info("=== GET SERVICES WITH RESULTS START ===");
        logger.info("bookingId: {}", bookingId);
        
        try {
            Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
            if (!bookingOpt.isPresent()) {
                logger.error("Booking not found with id: {}", bookingId);
                return ResponseEntity.notFound().build();
            }

            Booking booking = bookingOpt.get();
            logger.info("Found booking: {}", booking.getId());
            
            java.util.List<java.util.Map<String, Object>> servicesWithResults = new java.util.ArrayList<>();

            // Lấy danh sách services từ bookingServices
            if (booking.getBookingServices() != null) {
                logger.info("Booking has {} services", booking.getBookingServices().size());
                
                for (com.genx.adnmanagement.entity.BookingService bookingService : booking.getBookingServices()) {
                    Service service = bookingService.getService();
                    java.util.Map<String, Object> serviceData = new java.util.HashMap<>();
                    serviceData.put("serviceId", service.getId());
                    serviceData.put("serviceName", service.getName());

                    // Tìm kết quả cho service này
                    Optional<TestResult> resultOpt = testResultRepository.findByBookingIdAndServiceId(bookingId, service.getId());
                    logger.info("Service {} - result found: {}", service.getId(), resultOpt.isPresent());
                    
                    if (resultOpt.isPresent() && resultOpt.get().getResultFile() != null) {
                        serviceData.put("resultFile", resultOpt.get().getResultFile());
                        serviceData.put("hasResult", true);
                        logger.info("Service {} has result file: {}", service.getId(), resultOpt.get().getResultFile());
                    } else {
                        serviceData.put("resultFile", null);
                        serviceData.put("hasResult", false);
                        logger.info("Service {} has no result file", service.getId());
                    }

                    servicesWithResults.add(serviceData);
                }
            } else {
                logger.warn("Booking has no services");
            }

            logger.info("=== GET SERVICES WITH RESULTS SUCCESS ===");
            logger.info("Returning {} services", servicesWithResults.size());
            logger.info("Services data: {}", servicesWithResults);
            
            return ResponseEntity.ok(servicesWithResults);
        } catch (Exception e) {
            logger.error("=== GET SERVICES WITH RESULTS ERROR ===", e);
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    /**
     * API serve file PDF trực tiếp
     */
    @GetMapping("/file/{fileName}")
    public ResponseEntity<?> serveFile(@PathVariable String fileName) {
        logger.info("Serving file: {}", fileName);
        try {
            String uploadDir = System.getProperty("user.dir") + File.separator + "uploads" + File.separator + "results" + File.separator;
            File file = new File(uploadDir + fileName);
            logger.info("File path: {}", file.getAbsolutePath());
            
            if (file.exists()) {
                logger.info("File exists, serving...");
                return ResponseEntity.ok()
                    .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                    .header("Content-Disposition", "inline; filename=\"" + fileName + "\"")
                    .body(java.nio.file.Files.readAllBytes(file.toPath()));
            } else {
                logger.error("File not found: {}", file.getAbsolutePath());
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error serving file", e);
            return ResponseEntity.status(500).body("Error serving file: " + e.getMessage());
        }
    }
}
