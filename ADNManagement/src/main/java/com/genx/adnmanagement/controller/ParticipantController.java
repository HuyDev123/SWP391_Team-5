package com.genx.adnmanagement.controller;

import com.genx.adnmanagement.entity.Booking;
import com.genx.adnmanagement.entity.Participant;
import com.genx.adnmanagement.repository.BookingRepository;
import com.genx.adnmanagement.repository.ParticipantRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
        private String email;
        private String phone;
        private CustomerDTO customer;

        public BookingDTO(Booking booking) {
            this.id = booking.getId();
            this.fullName = booking.getFullName();
            this.email = booking.getEmail();
            this.phone = booking.getPhone();
            
            if (booking.getCustomer() != null) {
                this.customer = new CustomerDTO(booking.getCustomer());
            }
        }

        // Getters
        public Integer getId() { return id; }
        public String getFullName() { return fullName; }
        public String getEmail() { return email; }
        public String getPhone() { return phone; }
        public CustomerDTO getCustomer() { return customer; }
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
    public ResponseEntity<?> getAllParticipants() {
        try {
            System.out.println("Getting all participants...");
            List<Participant> participants = participantRepository.findAll();
            System.out.println("Found " + participants.size() + " participants");
            
            // Convert to DTOs
            List<ParticipantDTO> participantDTOs = participants.stream()
                .map(ParticipantDTO::new)
                .collect(Collectors.toList());
                
            return ResponseEntity.ok(participantDTOs);
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

    @GetMapping("/search")
    public ResponseEntity<?> searchParticipants(@RequestParam(required = false) String searchTerm) {
        try {
            List<Participant> participants;
            
            // Log để debug
            System.out.println("Search term: " + (searchTerm != null ? searchTerm : "NULL"));
            
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                // Nếu searchTerm null hoặc empty, load tất cả
                participants = participantRepository.findAll();
                System.out.println("Loading all participants: " + participants.size());
            } else {
                String trimmedTerm = searchTerm.trim();
                
                // Kiểm tra xem searchTerm có phải là số không
                try {
                    Integer numericValue = Integer.parseInt(trimmedTerm);
                    
                    // Thử tìm theo booking ID trước
                    participants = participantRepository.findByBookingId(numericValue);
                    if (!participants.isEmpty()) {
                        System.out.println("Found " + participants.size() + " participants by booking ID: " + numericValue);
                    } else {
                        // Nếu không tìm thấy theo booking ID, thử tìm theo customer ID
                        participants = participantRepository.findByCustomerId(numericValue);
                        System.out.println("Found " + participants.size() + " participants by customer ID: " + numericValue);
                    }
                } catch (NumberFormatException e) {
                    // Nếu không phải số, tìm kiếm theo text
                    participants = participantRepository.searchParticipantsByText(trimmedTerm);
                    System.out.println("Found " + participants.size() + " participants by text search: " + trimmedTerm);
                }
                
                // Loại bỏ trùng lặp dựa trên ID của participant
                participants = participants.stream()
                    .distinct()
                    .toList();
            }
            
            // Convert to DTOs
            List<ParticipantDTO> participantDTOs = participants.stream()
                .map(ParticipantDTO::new)
                .collect(Collectors.toList());
                
            return ResponseEntity.ok(participantDTOs);
        } catch (Exception e) {
            System.err.println("Error in searchParticipants: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi: " + e.getMessage());
        }
    }
} 