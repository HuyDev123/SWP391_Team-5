package com.genx.adnmanagement.controller;

import com.genx.adnmanagement.dto.FeedbackRequest;
import com.genx.adnmanagement.entity.*;
import com.genx.adnmanagement.repository.*;
import java.util.ArrayList;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class FeedbackController {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private UserRepository userRepository;

    // Serve trang feedback.html
    @GetMapping("/feedback")
    public String feedbackPage() {
        return "feedback";
    }

    // Lấy thông tin booking cho feedback
    @GetMapping("/booking-info/{bookingId}")
    @ResponseBody
    public ResponseEntity<?> getBookingInfo(@PathVariable Integer bookingId) {
        try {
            Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
            if (bookingOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Booking booking = bookingOpt.get();
            Map<String, Object> response = new HashMap<>();
            response.put("id", booking.getId());
            response.put("customerName", booking.getCustomerName());
            response.put("bookingDate", booking.getBookingDate());
            response.put("method", booking.getIsCenterCollected() ? "Tại cơ sở" : "Tại nhà");
            response.put("status", booking.getStatus());
            
            // Lấy thông tin services từ bookingServices
            List<Map<String, Object>> services = new java.util.ArrayList<>();
            if (booking.getBookingServices() != null) {
                for (int i = 0; i < booking.getBookingServices().size(); i++) {
                    BookingService bookingService = booking.getBookingServices().get(i);
                    Service service = bookingService.getService();
                    Map<String, Object> serviceInfo = new HashMap<>();
                    serviceInfo.put("id", i); // Index của service (để frontend dùng)
                    serviceInfo.put("serviceId", service.getId()); // ID thực tế của service (để backend dùng)
                    serviceInfo.put("name", service.getName());
                    serviceInfo.put("type", booking.getPurpose()); // Dân sự hoặc Hành chính
                    serviceInfo.put("price", service.getPrice());
                    services.add(serviceInfo);
                }
            }
            response.put("services", services);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("message", "Lỗi khi lấy thông tin booking: " + e.getMessage()));
        }
    }

    // API endpoints
    @RestController
    @RequestMapping("/api/feedback")
    @CrossOrigin(origins = "*")
    public class FeedbackApiController {

        // Tạo feedback mới
        @PostMapping("/submit")
        public ResponseEntity<?> submitFeedback(@Valid @RequestBody FeedbackRequest request, HttpSession session) {
            try {
                // Kiểm tra xem đã có feedback cho booking và service này chưa
                if (feedbackRepository.existsByBookingIdAndServiceId(request.getBookingId(), request.getServiceId())) {
                    return ResponseEntity.badRequest().body(Map.of("message", "Đã có feedback cho booking và service này"));
                }

                // Lấy booking
                Optional<Booking> bookingOpt = bookingRepository.findById(request.getBookingId());
                if (bookingOpt.isEmpty()) {
                    return ResponseEntity.badRequest().body(Map.of("message", "Không tìm thấy booking"));
                }

                // Lấy service
                Optional<Service> serviceOpt = serviceRepository.findById(request.getServiceId());
                if (serviceOpt.isEmpty()) {
                    return ResponseEntity.badRequest().body(Map.of("message", "Không tìm thấy service"));
                }

                // Lấy user từ session hoặc từ request
                User user = null;
                if (request.getUserId() != null) {
                    Optional<User> userOpt = userRepository.findById(request.getUserId());
                    user = userOpt.orElse(null);
                } else {
                    // Lấy user từ session
                    Integer userId = (Integer) session.getAttribute("userId");
                    if (userId != null) {
                        Optional<User> userOpt = userRepository.findById(userId);
                        user = userOpt.orElse(null);
                    }
                }

                // Lấy staff
                User staff = null;
                if (request.getStaffId() != null) {
                    Optional<User> staffOpt = userRepository.findById(request.getStaffId());
                    staff = staffOpt.orElse(null);
                }

                // Tạo feedback
                Feedback feedback = new Feedback();
                feedback.setBooking(bookingOpt.get());
                feedback.setService(serviceOpt.get());
                feedback.setUser(user);
                feedback.setStaff(staff);
                            feedback.setRating(request.getRating());
            feedback.setComment(request.getComment() != null ? request.getComment().trim() : null);

                Feedback savedFeedback = feedbackRepository.save(feedback);

                Map<String, Object> response = new HashMap<>();
                response.put("message", "Feedback đã được gửi thành công");
                response.put("feedbackId", savedFeedback.getId());
                response.put("bookingId", request.getBookingId());
                response.put("serviceId", request.getServiceId());

                return ResponseEntity.ok(response);

            } catch (Exception e) {
                return ResponseEntity.internalServerError().body(Map.of("message", "Lỗi khi gửi feedback: " + e.getMessage()));
            }
        }

        // Lấy feedback theo booking ID
        @GetMapping("/booking/{bookingId}")
        public ResponseEntity<?> getFeedbackByBooking(@PathVariable Integer bookingId) {
            try {
                List<Feedback> feedbacks = feedbackRepository.findByBookingId(bookingId);
                return ResponseEntity.ok(feedbacks);
            } catch (Exception e) {
                return ResponseEntity.internalServerError().body(Map.of("message", "Lỗi khi lấy feedback: " + e.getMessage()));
            }
        }

        // Lấy feedback theo user ID
        @GetMapping("/user/{userId}")
        public ResponseEntity<?> getFeedbackByUser(@PathVariable Integer userId) {
            try {
                List<Feedback> feedbacks = feedbackRepository.findByUserId(userId);
                return ResponseEntity.ok(feedbacks);
            } catch (Exception e) {
                return ResponseEntity.internalServerError().body(Map.of("message", "Lỗi khi lấy feedback: " + e.getMessage()));
            }
        }

        // Lấy feedback theo staff ID
        @GetMapping("/staff/{staffId}")
        public ResponseEntity<?> getFeedbackByStaff(@PathVariable Integer staffId) {
            try {
                List<Feedback> feedbacks = feedbackRepository.findByStaffId(staffId);
                return ResponseEntity.ok(feedbacks);
            } catch (Exception e) {
                return ResponseEntity.internalServerError().body(Map.of("message", "Lỗi khi lấy feedback: " + e.getMessage()));
            }
        }

        // Kiểm tra xem đã có feedback cho booking và service chưa
        @GetMapping("/check/{bookingId}/{serviceId}")
        public ResponseEntity<?> checkFeedbackExists(@PathVariable Integer bookingId, @PathVariable Integer serviceId) {
            try {
                boolean exists = feedbackRepository.existsByBookingIdAndServiceId(bookingId, serviceId);
                Map<String, Object> response = new HashMap<>();
                response.put("exists", exists);
                if (exists) {
                    Optional<Feedback> feedback = feedbackRepository.findByBookingIdAndServiceId(bookingId, serviceId);
                    response.put("feedback", feedback.orElse(null));
                }
                return ResponseEntity.ok(response);
            } catch (Exception e) {
                return ResponseEntity.internalServerError().body(Map.of("message", "Lỗi khi kiểm tra feedback: " + e.getMessage()));
            }
        }

        // Lấy điểm trung bình rating theo staff
        @GetMapping("/rating/staff/{staffId}")
        public ResponseEntity<?> getAverageRatingByStaff(@PathVariable Integer staffId) {
            try {
                Double averageRating = feedbackRepository.getAverageRatingByStaffId(staffId);
                Map<String, Object> response = new HashMap<>();
                response.put("staffId", staffId);
                response.put("averageRating", averageRating != null ? averageRating : 0.0);
                return ResponseEntity.ok(response);
            } catch (Exception e) {
                return ResponseEntity.internalServerError().body(Map.of("message", "Lỗi khi lấy điểm trung bình: " + e.getMessage()));
            }
        }

        // Lấy điểm trung bình rating theo service
        @GetMapping("/rating/service/{serviceId}")
        public ResponseEntity<?> getAverageRatingByService(@PathVariable Integer serviceId) {
            try {
                Double averageRating = feedbackRepository.getAverageRatingByServiceId(serviceId);
                Map<String, Object> response = new HashMap<>();
                response.put("serviceId", serviceId);
                response.put("averageRating", averageRating != null ? averageRating : 0.0);
                return ResponseEntity.ok(response);
            } catch (Exception e) {
                return ResponseEntity.internalServerError().body(Map.of("message", "Lỗi khi lấy điểm trung bình: " + e.getMessage()));
            }
        }

        // Lấy tất cả feedback
        @GetMapping("/all")
        public ResponseEntity<?> getAllFeedback() {
            try {
                List<Feedback> feedbacks = feedbackRepository.findAll();
                return ResponseEntity.ok(feedbacks);
            } catch (Exception e) {
                return ResponseEntity.internalServerError().body(Map.of("message", "Lỗi khi lấy tất cả feedback: " + e.getMessage()));
            }
        }

        // Lấy tất cả feedback cho một booking
        @GetMapping("/all/{bookingId}")
        public ResponseEntity<?> getAllFeedbackForBooking(@PathVariable Integer bookingId) {
            List<Feedback> feedbackList = feedbackRepository.findByBookingId(bookingId);
            List<Map<String, Object>> result = new ArrayList<>();
            for (Feedback fb : feedbackList) {
                Map<String, Object> map = new HashMap<>();
                map.put("serviceId", fb.getService().getId());
                map.put("exists", true);
                map.put("feedbackId", fb.getId());
                map.put("rating", fb.getRating());
                map.put("comment", fb.getComment());
                result.add(map);
            }
            return ResponseEntity.ok(result);
        }
    }
} 