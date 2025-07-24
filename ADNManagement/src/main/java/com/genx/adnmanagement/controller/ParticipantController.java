package com.genx.adnmanagement.controller;

import com.genx.adnmanagement.entity.Booking;
import com.genx.adnmanagement.entity.BookingService;
import com.genx.adnmanagement.entity.Participant;
import com.genx.adnmanagement.entity.User;
import com.genx.adnmanagement.repository.BookingRepository;
import com.genx.adnmanagement.repository.BookingServiceRepository;
import com.genx.adnmanagement.repository.ParticipantRepository;
import com.genx.adnmanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/participants")
public class ParticipantController {

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private BookingRepository bookingRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private BookingServiceRepository bookingServiceRepository;

    // DTO class để tránh circular reference
    public static class ParticipantDTO {
        private Integer id;
        private String fullName;
        private String gender;
        private String birthday;
        private String phone;
        private String email;
        private String address;
        private String cccdNumber;
        private String cccdIssuedDate;
        private String cccdIssuedPlace;
        private String relationship;
        private String photoUrl;
        private String note;
        private BookingDTO booking;

        public ParticipantDTO(Participant participant) {
            this.id = participant.getId();
            this.fullName = participant.getFullName();
            this.gender = participant.getGender();
            this.birthday = participant.getBirthday() != null ? participant.getBirthday().toString() : null;
            this.phone = participant.getPhone();
            this.email = participant.getEmail();
            this.address = participant.getAddress();
            this.cccdNumber = participant.getCccdNumber();
            this.cccdIssuedDate = participant.getCccdIssuedDate() != null ? participant.getCccdIssuedDate().toString() : null;
            this.cccdIssuedPlace = participant.getCccdIssuedPlace();
            this.relationship = participant.getRelationship();
            this.photoUrl = participant.getPhotoUrl();
            this.note = participant.getNote();
            
            if (participant.getBooking() != null) {
                this.booking = new BookingDTO(participant.getBooking());
            }
        }

        // Getters
        public Integer getId() { return id; }
        public String getFullName() { return fullName; }
        public String getGender() { return gender; }
        public String getBirthday() { return birthday; }
        public String getPhone() { return phone; }
        public String getEmail() { return email; }
        public String getAddress() { return address; }
        public String getCccdNumber() { return cccdNumber; }
        public String getCccdIssuedDate() { return cccdIssuedDate; }
        public String getCccdIssuedPlace() { return cccdIssuedPlace; }
        public String getRelationship() { return relationship; }
        public String getPhotoUrl() { return photoUrl; }
        public String getNote() { return note; }
        public BookingDTO getBooking() { return booking; }
    }

    public static class BookingDTO {
        private Integer id;
        private String fullName;
        private String purpose;
        private String serviceType;

        public BookingDTO(Booking booking) {
            this.id = booking.getId();
            this.fullName = booking.getFullName();
            this.purpose = booking.getPurpose();
            this.serviceType = booking.getServiceType();
        }

        // Getters
        public Integer getId() { return id; }
        public String getFullName() { return fullName; }
        public String getPurpose() { return purpose; }
        public String getServiceType() { return serviceType; }
    }

    public static class CustomerDTO {
        private Integer id;
        private String fullName;
        private String email;

        public CustomerDTO(com.genx.adnmanagement.entity.User customer) {
            this.id = customer.getId();
            this.fullName = customer.getFullName();
            this.email = customer.getEmail();
        }

        // Getters
        public Integer getId() { return id; }
        public String getFullName() { return fullName; }
        public String getEmail() { return email; }
    }

    @GetMapping
    public ResponseEntity<?> getAllParticipants(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            System.out.println("Getting all participants with pagination - page: " + page + ", size: " + size);
            
            Pageable pageable = PageRequest.of(page, size);
            Page<Participant> participantPage = participantRepository.findAllWithPagination(pageable);
            
            System.out.println("Found " + participantPage.getTotalElements() + " participants in " + participantPage.getTotalPages() + " pages");
            
            // Convert to DTOs
            List<ParticipantDTO> participantDTOs = participantPage.getContent().stream()
                .map(ParticipantDTO::new)
                .collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("content", participantDTOs);
            response.put("totalPages", participantPage.getTotalPages());
            response.put("totalElements", participantPage.getTotalElements());
            response.put("page", participantPage.getNumber());
            response.put("size", participantPage.getSize());
                
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Error in getAllParticipants: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getParticipantById(@PathVariable Integer id) {
        try {
            Optional<Participant> participant = participantRepository.findById(id);
            if (participant.isPresent()) {
                ParticipantDTO participantDTO = new ParticipantDTO(participant.get());
                return ResponseEntity.ok(participantDTO);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy người tham gia với ID: " + id);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> addParticipant(@RequestBody Participant participant) {
        try {
            // Validate booking exists
            if (participant.getBooking() == null || participant.getBooking().getId() == null) {
                return ResponseEntity.badRequest().body("Thiếu thông tin cuộc hẹn");
            }
            
            Optional<Booking> bookingOpt = bookingRepository.findById(participant.getBooking().getId());
            if (bookingOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy cuộc hẹn");
            }
            
            participant.setBooking(bookingOpt.get());
            Participant savedParticipant = participantRepository.save(participant);
            return ResponseEntity.ok("Thêm người tham gia thành công");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateParticipant(@PathVariable Integer id, @RequestBody Participant participant) {
        try {
            if (!participantRepository.existsById(id)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy người tham gia với ID: " + id);
            }
            
            // Validate booking exists if provided
            if (participant.getBooking() != null && participant.getBooking().getId() != null) {
                Optional<Booking> bookingOpt = bookingRepository.findById(participant.getBooking().getId());
                if (bookingOpt.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy cuộc hẹn");
                }
                participant.setBooking(bookingOpt.get());
            }
            
            participant.setId(id);
            participantRepository.save(participant);
            return ResponseEntity.ok("Cập nhật người tham gia thành công");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteParticipant(@PathVariable Integer id) {
        try {
            if (!participantRepository.existsById(id)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy người tham gia với ID: " + id);
            }
            participantRepository.deleteById(id);
            return ResponseEntity.ok("Xóa người tham gia thành công");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi: " + e.getMessage());
        }
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<?> getBookingDetails(@PathVariable String bookingId) {
        try {
            Integer id = Integer.parseInt(bookingId);
            Optional<Booking> booking = bookingRepository.findById(id);
            
            if (booking.isPresent()) {
                Booking foundBooking = booking.get();
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("bookingId", foundBooking.getId());
                response.put("customerName", foundBooking.getFullName());
                response.put("serviceType", foundBooking.getServiceType());
                response.put("purpose", foundBooking.getPurpose());
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("error", "Không tìm thấy cuộc hẹn với mã: " + bookingId);
                return ResponseEntity.ok(response);
            }
        } catch (NumberFormatException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", "Mã cuộc hẹn không hợp lệ: " + bookingId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", "Lỗi: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/booking/{bookingId}/participants")
    public ResponseEntity<?> getParticipantsByBooking(@PathVariable Integer bookingId) {
        try {
            List<Participant> participants = participantRepository.findByBooking_Id(bookingId);
            
            // Convert to DTOs
            List<ParticipantDTO> participantDTOs = participants.stream()
                .map(ParticipantDTO::new)
                .collect(Collectors.toList());
                
            return ResponseEntity.ok(participantDTOs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi: " + e.getMessage());
        }
    }

    @GetMapping("/staff")
    public ResponseEntity<?> getAllStaff() {
        try {
            // Lấy tất cả user có roleId = 3 (staff)
            List<User> staffList = userRepository.findAll().stream()
                .filter(user -> user.getRoleId() == 3 && user.getIsActive())
                .collect(Collectors.toList());
            
            // Convert to DTOs
            List<Map<String, Object>> staffDTOs = staffList.stream()
                .map(staff -> {
                    Map<String, Object> dto = new HashMap<>();
                    dto.put("id", staff.getId());
                    dto.put("fullName", staff.getFullName());
                    dto.put("email", staff.getEmail());
                    return dto;
                })
                .collect(Collectors.toList());
                
            return ResponseEntity.ok(staffDTOs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi: " + e.getMessage());
        }
    }
    
    @GetMapping("/booking/{bookingId}/services")
    public ResponseEntity<?> getServicesByBooking(@PathVariable Integer bookingId) {
        try {
            List<BookingService> bookingServices = bookingServiceRepository.findByBookingId(bookingId);
            
            List<Map<String, Object>> serviceDTOs = bookingServices.stream()
                .map(bs -> {
                    Map<String, Object> dto = new HashMap<>();
                    dto.put("id", bs.getService().getId());
                    dto.put("name", bs.getService().getName());
                    dto.put("description", bs.getService().getDescription());
                    dto.put("price", bs.getService().getPrice());
                    dto.put("sampleQuantity", bs.getSampleQuantity());
                    return dto;
                })
                .collect(Collectors.toList());
                
            return ResponseEntity.ok(serviceDTOs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi: " + e.getMessage());
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchParticipants(
            @RequestParam(required = false) String searchTerm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Participant> participantPage;
            
            // Log để debug
            System.out.println("Search term: " + (searchTerm != null ? searchTerm : "NULL") + ", page: " + page + ", size: " + size);
            
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                // Nếu searchTerm null hoặc empty, load tất cả với phân trang
                participantPage = participantRepository.findAllWithPagination(pageable);
                System.out.println("Loading all participants with pagination: " + participantPage.getTotalElements());
            } else {
                String trimmedTerm = searchTerm.trim();
                
                // Kiểm tra xem searchTerm có phải là số không
                try {
                    Integer numericValue = Integer.parseInt(trimmedTerm);
                    
                    // Thử tìm theo booking ID trước
                    participantPage = participantRepository.findByBookingIdWithPagination(numericValue, pageable);
                    if (!participantPage.getContent().isEmpty()) {
                        System.out.println("Found " + participantPage.getTotalElements() + " participants by booking ID: " + numericValue);
                    } else {
                        // Nếu không tìm thấy theo booking ID, thử tìm theo customer ID
                        participantPage = participantRepository.findByCustomerIdWithPagination(numericValue, pageable);
                        System.out.println("Found " + participantPage.getTotalElements() + " participants by customer ID: " + numericValue);
                    }
                } catch (NumberFormatException e) {
                    // Nếu không phải số, tìm kiếm theo text
                    participantPage = participantRepository.searchParticipantsByTextWithPagination(trimmedTerm, pageable);
                    System.out.println("Found " + participantPage.getTotalElements() + " participants by text search: " + trimmedTerm);
                }
            }
            
            // Convert to DTOs
            List<ParticipantDTO> participantDTOs = participantPage.getContent().stream()
                .map(ParticipantDTO::new)
                .collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("content", participantDTOs);
            response.put("totalPages", participantPage.getTotalPages());
            response.put("totalElements", participantPage.getTotalElements());
            response.put("page", participantPage.getNumber());
            response.put("size", participantPage.getSize());
                
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Error in searchParticipants: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi: " + e.getMessage());
        }
    }
} 