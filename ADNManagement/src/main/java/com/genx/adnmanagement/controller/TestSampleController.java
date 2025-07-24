package com.genx.adnmanagement.controller;

import com.genx.adnmanagement.entity.Participant;
import com.genx.adnmanagement.entity.Service;
import com.genx.adnmanagement.entity.TestSample;
import com.genx.adnmanagement.entity.User;
import com.genx.adnmanagement.repository.ParticipantRepository;
import com.genx.adnmanagement.repository.ServiceRepository;
import com.genx.adnmanagement.repository.TestSampleRepository;
import com.genx.adnmanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/test-samples")
public class TestSampleController {

    @Autowired
    private TestSampleRepository testSampleRepository;

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private UserRepository userRepository;

    // DTO class để tránh circular reference
    public static class TestSampleDTO {
        private Integer id;
        private String sampleCode;
        private String sampleType;
        private String collectedAt;
        private String note;
        private ServiceDTO service;
        private ParticipantDTO participant;
        private UserDTO collectedBy;

        public TestSampleDTO(TestSample testSample) {
            this.id = testSample.getId();
            this.sampleCode = testSample.getSampleCode();
            this.sampleType = testSample.getSampleType();
            this.collectedAt = testSample.getCollectedAt() != null ? testSample.getCollectedAt().toString() : null;
            this.note = testSample.getNote();
            
            if (testSample.getService() != null) {
                this.service = new ServiceDTO(testSample.getService());
            }
            
            if (testSample.getParticipant() != null) {
                this.participant = new ParticipantDTO(testSample.getParticipant());
            }
            
            if (testSample.getCollectedBy() != null) {
                this.collectedBy = new UserDTO(testSample.getCollectedBy());
            }
        }

        // Getters
        public Integer getId() { return id; }
        public String getSampleCode() { return sampleCode; }
        public String getSampleType() { return sampleType; }
        public String getCollectedAt() { return collectedAt; }
        public String getNote() { return note; }
        public ServiceDTO getService() { return service; }
        public ParticipantDTO getParticipant() { return participant; }
        public UserDTO getCollectedBy() { return collectedBy; }
    }

    public static class ServiceDTO {
        private Integer id;
        private String name;
        private String description;

        public ServiceDTO(Service service) {
            this.id = service.getId();
            this.name = service.getName();
            this.description = service.getDescription();
        }

        // Getters
        public Integer getId() { return id; }
        public String getName() { return name; }
        public String getDescription() { return description; }
    }

    public static class ParticipantDTO {
        private Integer id;
        private String fullName;
        private BookingDTO booking;

        public ParticipantDTO(Participant participant) {
            this.id = participant.getId();
            this.fullName = participant.getFullName();
            
            if (participant.getBooking() != null) {
                this.booking = new BookingDTO(participant.getBooking());
            }
        }

        // Getters
        public Integer getId() { return id; }
        public String getFullName() { return fullName; }
        public BookingDTO getBooking() { return booking; }
    }

    public static class BookingDTO {
        private Integer id;
        private String fullName;

        public BookingDTO(com.genx.adnmanagement.entity.Booking booking) {
            this.id = booking.getId();
            this.fullName = booking.getFullName();
        }

        // Getters
        public Integer getId() { return id; }
        public String getFullName() { return fullName; }
    }

    public static class UserDTO {
        private Integer id;
        private String fullName;
        private String email;

        public UserDTO(User user) {
            this.id = user.getId();
            this.fullName = user.getFullName();
            this.email = user.getEmail();
        }

        // Getters
        public Integer getId() { return id; }
        public String getFullName() { return fullName; }
        public String getEmail() { return email; }
    }

    @GetMapping
    public ResponseEntity<?> getAllTestSamples(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            System.out.println("Getting all test samples with pagination - page: " + page + ", size: " + size);
            
            Pageable pageable = PageRequest.of(page, size);
            Page<TestSample> testSamplePage = testSampleRepository.findAllWithPagination(pageable);
            
            System.out.println("Found " + testSamplePage.getTotalElements() + " test samples in " + testSamplePage.getTotalPages() + " pages");
            
            // Convert to DTOs
            List<TestSampleDTO> testSampleDTOs = testSamplePage.getContent().stream()
                .map(TestSampleDTO::new)
                .collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("content", testSampleDTOs);
            response.put("totalPages", testSamplePage.getTotalPages());
            response.put("totalElements", testSamplePage.getTotalElements());
            response.put("page", testSamplePage.getNumber());
            response.put("size", testSamplePage.getSize());
                
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Error in getAllTestSamples: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTestSampleById(@PathVariable Integer id) {
        try {
            Optional<TestSample> testSample = testSampleRepository.findById(id);
            if (testSample.isPresent()) {
                TestSampleDTO testSampleDTO = new TestSampleDTO(testSample.get());
                return ResponseEntity.ok(testSampleDTO);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy mẫu xét nghiệm với ID: " + id);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> addTestSample(@RequestBody TestSample testSample) {
        try {
            // Validate participant exists
            if (testSample.getParticipant() == null || testSample.getParticipant().getId() == null) {
                return ResponseEntity.badRequest().body("Thiếu thông tin người tham gia");
            }
            
            Optional<Participant> participantOpt = participantRepository.findById(testSample.getParticipant().getId());
            if (participantOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy người tham gia");
            }
            
            // Validate service exists
            if (testSample.getService() == null || testSample.getService().getId() == null) {
                return ResponseEntity.badRequest().body("Thiếu thông tin dịch vụ");
            }
            
            Optional<Service> serviceOpt = serviceRepository.findById(testSample.getService().getId());
            if (serviceOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy dịch vụ");
            }
            
            // Validate collected by user exists if provided
            if (testSample.getCollectedBy() != null && testSample.getCollectedBy().getId() != null) {
                Optional<User> userOpt = userRepository.findById(testSample.getCollectedBy().getId());
                if (userOpt.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy người thu thập");
                }
                testSample.setCollectedBy(userOpt.get());
            }
            
            testSample.setParticipant(participantOpt.get());
            testSample.setService(serviceOpt.get());
            
            // Set collected time if not provided
            if (testSample.getCollectedAt() == null) {
                testSample.setCollectedAt(LocalDateTime.now());
            }
            
            TestSample savedTestSample = testSampleRepository.save(testSample);
            return ResponseEntity.ok("Thêm mẫu xét nghiệm thành công");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTestSample(@PathVariable Integer id, @RequestBody TestSample testSample) {
        try {
            if (!testSampleRepository.existsById(id)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy mẫu xét nghiệm với ID: " + id);
            }
            
            // Validate participant exists if provided
            if (testSample.getParticipant() != null && testSample.getParticipant().getId() != null) {
                Optional<Participant> participantOpt = participantRepository.findById(testSample.getParticipant().getId());
                if (participantOpt.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy người tham gia");
                }
                testSample.setParticipant(participantOpt.get());
            }
            
            // Validate service exists if provided
            if (testSample.getService() != null && testSample.getService().getId() != null) {
                Optional<Service> serviceOpt = serviceRepository.findById(testSample.getService().getId());
                if (serviceOpt.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy dịch vụ");
                }
                testSample.setService(serviceOpt.get());
            }
            
            // Validate collected by user exists if provided
            if (testSample.getCollectedBy() != null && testSample.getCollectedBy().getId() != null) {
                Optional<User> userOpt = userRepository.findById(testSample.getCollectedBy().getId());
                if (userOpt.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy người thu thập");
                }
                testSample.setCollectedBy(userOpt.get());
            }
            
            testSample.setId(id);
            testSampleRepository.save(testSample);
            return ResponseEntity.ok("Cập nhật mẫu xét nghiệm thành công");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTestSample(@PathVariable Integer id) {
        try {
            if (!testSampleRepository.existsById(id)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy mẫu xét nghiệm với ID: " + id);
            }
            testSampleRepository.deleteById(id);
            return ResponseEntity.ok("Xóa mẫu xét nghiệm thành công");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi: " + e.getMessage());
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchTestSamples(
            @RequestParam(required = false) String searchTerm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<TestSample> testSamplePage;
            
            // Log để debug
            System.out.println("Search term: " + (searchTerm != null ? searchTerm : "NULL") + ", page: " + page + ", size: " + size);
            
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                // Nếu searchTerm null hoặc empty, load tất cả với phân trang
                testSamplePage = testSampleRepository.findAllWithPagination(pageable);
                System.out.println("Loading all test samples with pagination: " + testSamplePage.getTotalElements());
            } else {
                String trimmedTerm = searchTerm.trim();
                
                // Kiểm tra xem searchTerm có phải là số không
                try {
                    Integer numericValue = Integer.parseInt(trimmedTerm);
                    
                    // Thử tìm theo sample code trước
                    Optional<TestSample> sampleOpt = testSampleRepository.findBySampleCode(trimmedTerm);
                    if (sampleOpt.isPresent()) {
                        // Tạo Page với một item duy nhất
                        List<TestSample> singleSample = List.of(sampleOpt.get());
                        testSamplePage = new org.springframework.data.domain.PageImpl<>(
                            singleSample, pageable, 1
                        );
                        System.out.println("Found test sample by sample code: " + trimmedTerm);
                    } else {
                        // Nếu không tìm thấy theo sample code, thử tìm theo participant ID
                        testSamplePage = testSampleRepository.findByParticipantIdWithPagination(numericValue, pageable);
                        System.out.println("Found " + testSamplePage.getTotalElements() + " test samples by participant ID: " + numericValue);
                    }
                } catch (NumberFormatException e) {
                    // Nếu không phải số, tìm kiếm theo text
                    testSamplePage = testSampleRepository.searchTestSamplesByTextWithPagination(trimmedTerm, pageable);
                    System.out.println("Found " + testSamplePage.getTotalElements() + " test samples by text search: " + trimmedTerm);
                }
            }
            
            // Convert to DTOs
            List<TestSampleDTO> testSampleDTOs = testSamplePage.getContent().stream()
                .map(TestSampleDTO::new)
                .collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("content", testSampleDTOs);
            response.put("totalPages", testSamplePage.getTotalPages());
            response.put("totalElements", testSamplePage.getTotalElements());
            response.put("page", testSamplePage.getNumber());
            response.put("size", testSamplePage.getSize());
                
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Error in searchTestSamples: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi: " + e.getMessage());
        }
    }

    @GetMapping("/participant/{participantId}/services")
    public ResponseEntity<?> getServicesByParticipant(@PathVariable Integer participantId) {
        try {
            Optional<Participant> participantOpt = participantRepository.findById(participantId);
            if (participantOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy người tham gia");
            }
            
            Participant participant = participantOpt.get();
            List<Service> services = new ArrayList<>();
            
            // Lấy services từ booking của participant
            if (participant.getBooking() != null && participant.getBooking().getBookingServices() != null) {
                services = participant.getBooking().getBookingServices().stream()
                    .map(bookingService -> bookingService.getService())
                    .collect(Collectors.toList());
            }
            
            // Convert to DTOs
            List<ServiceDTO> serviceDTOs = services.stream()
                .map(ServiceDTO::new)
                .collect(Collectors.toList());
                
            return ResponseEntity.ok(serviceDTOs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi: " + e.getMessage());
        }
    }
} 