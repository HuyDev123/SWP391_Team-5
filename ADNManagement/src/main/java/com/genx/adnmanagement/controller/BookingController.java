package com.genx.adnmanagement.controller;

import com.genx.adnmanagement.dto.BookingRequest;
import com.genx.adnmanagement.entity.*;
import com.genx.adnmanagement.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.LocalDate;

@RestController
@RequestMapping("/booking")
public class BookingController {
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private ServiceRepository serviceRepository;
    @Autowired
    private BookingServiceRepository bookingServiceRepository;
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/book")
    public ResponseEntity<?> createBooking(@RequestBody BookingRequest request, @SessionAttribute(name = "user", required = false) User sessionUser) {
        try {
            // Kiểm tra userId nếu có gửi từ client
            if (request.getUserId() != null) {
                if (sessionUser == null || !request.getUserId().equals(sessionUser.getId())) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .header("X-Session-Status", "EXPIRED")
                            .body("SESSION_EXPIRED: Phiên đăng nhập đã hết hạn, đang tải lại trang...");
                }
            }
            // Kiểm tra serviceIds hợp lệ
            List<Integer> serviceIds = request.getServiceIds();
            if (serviceIds == null || serviceIds.isEmpty()) {
                return ResponseEntity.badRequest().body("Vui lòng chọn ít nhất một dịch vụ xét nghiệm hợp lệ.");
            }
            List<Service> validServices = new ArrayList<>();
            for (Integer serviceId : serviceIds) {
                Service service = serviceRepository.findById(serviceId).orElse(null);
                if (service == null) {
                    return ResponseEntity.badRequest().body("Dịch vụ không hợp lệ: " + serviceId);
                }
                validServices.add(service);
            }
            Booking booking = new Booking();
            booking.setFullName(request.getFullName());
            booking.setEmail(request.getEmail());
            booking.setPhone(request.getPhone());
            booking.setIsAdministrative(request.getIsAdministrative());
            booking.setIsCenterCollected(request.getIsCenterCollected());
            booking.setAddress(request.getAddress());
            booking.setNote(request.getNote());
            booking.setCenterSampleDate(request.getCenterSampleDate());
            booking.setCenterSampleTime(request.getCenterSampleTime());
            booking.setBookingDate(LocalDateTime.now());
            booking.setStatus("Đã đặt");

            // Nếu đã đăng nhập, set user cho booking
            if (sessionUser != null) {
                booking.setCustomer(sessionUser);
            }

            Booking savedBooking = bookingRepository.save(booking);

            List<BookingService> bookingServices = new ArrayList<>();
            for (Service service : validServices) {
                BookingService bs = new BookingService();
                bs.setBooking(savedBooking);
                bs.setService(service);
                bookingServices.add(bs);
            }
            bookingServiceRepository.saveAll(bookingServices);
            return ResponseEntity.ok().body("Đặt lịch thành công!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Đặt lịch thất bại: " + e.getMessage());
        }
    }

    @GetMapping("/appointments")
    public ResponseEntity<?> getAppointmentsByUserIdV2(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) Integer serviceId,
            @SessionAttribute(name = "user", required = false) User sessionUser) {
        if (sessionUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("SESSION_EXPIRED: Phiên đăng nhập đã hết hạn, vui lòng đăng nhập lại.");
        }
        int userId = sessionUser.getId();
        try {
            Pageable pageable = PageRequest.of(page, size);
            LocalDate bookingDate = null;
            if (date != null && !date.isBlank()) {
                bookingDate = LocalDate.parse(date);
            }
            Page<Booking> bookingPage = bookingRepository.findByUserIdAndFilters(
                userId,
                (status == null || status.isBlank()) ? null : status,
                bookingDate,
                serviceId,
                pageable
            );

            List<Map<String, Object>> appointmentsList = new ArrayList<>();
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            for (Booking booking : bookingPage.getContent()) {
                Map<String, Object> appointmentDetails = new HashMap<>();
                appointmentDetails.put("id", booking.getId());
                appointmentDetails.put("customerName", booking.getFullName());
                appointmentDetails.put("email", booking.getEmail());
                appointmentDetails.put("phone", booking.getPhone());
                appointmentDetails.put("address", booking.getAddress());
                appointmentDetails.put("note", booking.getNote());

                String ADNService = booking.getIsAdministrative() ? "Hành chính" : "Dân sự";
                appointmentDetails.put("ADNService", ADNService);

                String method = booking.getIsCenterCollected() ? "Tại trung tâm" : "Tại nhà";
                appointmentDetails.put("method", method);
                appointmentDetails.put("bookingDate", booking.getBookingDate().format(dateTimeFormatter));

                if (booking.getCenterSampleDate() != null) {
                    String appointmentDateTime = booking.getCenterSampleDate().toString();
                    if (booking.getCenterSampleTime() != null) {
                        appointmentDateTime += " " + booking.getCenterSampleTime();
                    }
                    appointmentDetails.put("appointmentDateTime", appointmentDateTime);
                } else {
                    appointmentDetails.put("appointmentDateTime", null);
                }

                appointmentDetails.put("status", booking.getStatus());

                List<BookingService> bookingServices = bookingServiceRepository.findByBooking_Id(booking.getId());
                List<Map<String, Object>> services = new ArrayList<>();

                for (BookingService bs : bookingServices) {
                    Service service = bs.getService();
                    Map<String, Object> serviceDetail = new HashMap<>();
                    serviceDetail.put("service", service.getName());
                    serviceDetail.put("serviceId", service.getId());
                    serviceDetail.put("price", service.getPrice());
                    services.add(serviceDetail);
                }

                appointmentDetails.put("services", services);
                appointmentsList.add(appointmentDetails);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("content", appointmentsList);
            response.put("totalPages", bookingPage.getTotalPages());
            response.put("totalElements", bookingPage.getTotalElements());
            response.put("page", bookingPage.getNumber());
            response.put("size", bookingPage.getSize());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi lấy danh sách lịch hẹn: " + e.getMessage());
        }
    }

    @PostMapping("/cancel")
    public ResponseEntity<?> cancelAppointment(
            @RequestParam Integer bookingId,
            @RequestParam String reason,
            @SessionAttribute(name = "user", required = false) User sessionUser) {
        try {
            Booking booking = bookingRepository.findById(bookingId).orElse(null);

            if (booking == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy lịch hẹn");
            }

            if (sessionUser == null || (booking.getStaff() != null && !booking.getStaff().getId().equals(sessionUser.getId()))) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Bạn không có quyền hủy lịch hẹn này");
            }

            // Chỉ cho phép hủy các lịch hẹn có trạng thái "Đã đặt" hoặc "Đang chờ lấy mẫu"
            if (!booking.getStatus().equals("Đã đặt") && !booking.getStatus().equals("Đang chờ lấy mẫu")) {
                return ResponseEntity.badRequest().body("Không thể hủy lịch hẹn trong trạng thái hiện tại");
            }

            booking.setStatus("Đã hủy");
            // Ghi lý do hủy vào note, xuống dòng nếu đã có note cũ
            String oldNote = booking.getNote();
            String newNote = (oldNote == null || oldNote.isBlank() ? "" : oldNote + "\n\n") + "Lý do hủy: " + reason;
            booking.setNote(newNote);
            bookingRepository.save(booking);

            return ResponseEntity.ok("Hủy lịch hẹn thành công");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi hủy lịch hẹn: " + e.getMessage());
        }
    }

    // Staff endpoints
    @GetMapping("/staff/appointments")
    public ResponseEntity<?> getAllAppointmentsForStaff(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) Integer serviceId,
            @RequestParam(required = false) String searchQuery,
            @SessionAttribute(name = "staff", required = false) User sessionStaff) {
        
        // Check if staff session exists
        if (sessionStaff == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("ACCESS_DENIED: Bạn không có quyền truy cập trang này.");
        }

        try {
            Pageable pageable = PageRequest.of(page, size);
            LocalDate bookingDate = null;
            if (date != null && !date.isBlank()) {
                bookingDate = LocalDate.parse(date);
            }

            Page<Booking> bookingPage = bookingRepository.findAllWithFilters(
                (status == null || status.isBlank()) ? null : status,
                bookingDate,
                serviceId,
                (searchQuery == null || searchQuery.isBlank()) ? null : searchQuery,
                pageable
            );

            List<Map<String, Object>> appointmentsList = new ArrayList<>();
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            for (Booking booking : bookingPage.getContent()) {
                Map<String, Object> appointmentDetails = new HashMap<>();
                appointmentDetails.put("id", booking.getId());
                if (booking.getCustomer() != null) {
                    appointmentDetails.put("customerId", booking.getCustomer().getId());
                } else {
                    appointmentDetails.put("customerId", null);
                }
                appointmentDetails.put("customerName", booking.getFullName());
                appointmentDetails.put("email", booking.getEmail());
                appointmentDetails.put("phone", booking.getPhone());
                appointmentDetails.put("address", booking.getAddress());
                appointmentDetails.put("note", booking.getNote());

                String ADNService = booking.getIsAdministrative() ? "Hành chính" : "Dân sự";
                appointmentDetails.put("ADNService", ADNService);

                String method = booking.getIsCenterCollected() ? "Tại trung tâm" : "Tại nhà";
                appointmentDetails.put("method", method);
                appointmentDetails.put("bookingDate", booking.getBookingDate().format(dateTimeFormatter));

                if (booking.getCenterSampleDate() != null) {
                    String appointmentDateTime = booking.getCenterSampleDate().toString();
                    if (booking.getCenterSampleTime() != null) {
                        appointmentDateTime += " " + booking.getCenterSampleTime();
                    }
                    appointmentDetails.put("appointmentDateTime", appointmentDateTime);
                } else {
                    appointmentDetails.put("appointmentDateTime", null);
                }

                appointmentDetails.put("status", booking.getStatus());

                List<BookingService> bookingServices = bookingServiceRepository.findByBooking_Id(booking.getId());
                List<Map<String, Object>> services = new ArrayList<>();

                for (BookingService bs : bookingServices) {
                    Service service = bs.getService();
                    Map<String, Object> serviceDetail = new HashMap<>();
                    serviceDetail.put("service", service.getName());
                    serviceDetail.put("serviceId", service.getId());
                    serviceDetail.put("price", service.getPrice());
                    services.add(serviceDetail);
                }

                appointmentDetails.put("services", services);
                // Add staff information if available
                if (booking.getStaff() != null) {
                    appointmentDetails.put("staffFullName", booking.getStaff().getFullName());
                }
                appointmentsList.add(appointmentDetails);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("content", appointmentsList);
            response.put("totalPages", bookingPage.getTotalPages());
            response.put("totalElements", bookingPage.getTotalElements());
            response.put("page", bookingPage.getNumber());
            response.put("size", bookingPage.getSize());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi lấy danh sách lịch hẹn: " + e.getMessage());
        }
    }

    @GetMapping("/appointments/{id}")
    public ResponseEntity<?> getAppointmentById(@PathVariable Integer id, @SessionAttribute(name = "staff", required = false) User sessionStaff) {
        if (sessionStaff == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("ACCESS_DENIED: Bạn không có quyền truy cập trang này.");
        }
        Booking booking = bookingRepository.findById(id).orElse(null);
        if (booking == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy lịch hẹn");
        }
        Map<String, Object> appointmentDetails = new HashMap<>();
        appointmentDetails.put("id", booking.getId());
        if (booking.getCustomer() != null) {
            appointmentDetails.put("customerId", booking.getCustomer().getId());
        } else {
            appointmentDetails.put("customerId", null);
        }
        appointmentDetails.put("customerName", booking.getFullName());
        appointmentDetails.put("email", booking.getEmail());
        appointmentDetails.put("phone", booking.getPhone());
        appointmentDetails.put("address", booking.getAddress());
        appointmentDetails.put("note", booking.getNote());
        String ADNService = booking.getIsAdministrative() ? "Hành chính" : "Dân sự";
        appointmentDetails.put("ADNService", ADNService);
        String method = booking.getIsCenterCollected() ? "Tại trung tâm" : "Tại nhà";
        appointmentDetails.put("method", method);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        appointmentDetails.put("bookingDate", booking.getBookingDate().format(dateTimeFormatter));
        if (booking.getCenterSampleDate() != null) {
            String appointmentDateTime = booking.getCenterSampleDate().toString();
            if (booking.getCenterSampleTime() != null) {
                appointmentDateTime += " " + booking.getCenterSampleTime();
            }
            appointmentDetails.put("appointmentDateTime", appointmentDateTime);
        } else {
            appointmentDetails.put("appointmentDateTime", null);
        }
        appointmentDetails.put("status", booking.getStatus());
        // Dịch vụ
        List<BookingService> bookingServices = bookingServiceRepository.findByBooking_Id(booking.getId());
        List<Map<String, Object>> services = new ArrayList<>();
        for (BookingService bs : bookingServices) {
            Service service = bs.getService();
            Map<String, Object> serviceDetail = new HashMap<>();
            serviceDetail.put("service", service.getName());
            serviceDetail.put("serviceId", service.getId());
            serviceDetail.put("price", service.getPrice());
            services.add(serviceDetail);
        }
        appointmentDetails.put("services", services);
        if (booking.getStaff() != null) {
            appointmentDetails.put("staffFullName", booking.getStaff().getFullName());
        }
        return ResponseEntity.ok(appointmentDetails);
    }

    @PutMapping("/appointments/{id}")
    public ResponseEntity<?> updateAppointmentById(@PathVariable Integer id, @RequestBody Map<String, Object> payload, @SessionAttribute(name = "staff", required = false) User sessionStaff) {
        if (sessionStaff == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("ACCESS_DENIED: Bạn không có quyền thực hiện thao tác này.");
        }
        Booking booking = bookingRepository.findById(id).orElse(null);
        if (booking == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy lịch hẹn");
        }
        // Cập nhật các trường
        booking.setFullName((String) payload.get("customerName"));
        booking.setEmail((String) payload.get("email"));
        booking.setPhone((String) payload.get("phone"));
        booking.setAddress((String) payload.get("address"));
        booking.setStatus((String) payload.get("status"));
        booking.setNote((String) payload.get("note"));
        // Loại dịch vụ
        String ADNService = (String) payload.get("ADNService");
        booking.setIsAdministrative("Hành chính".equals(ADNService));
        // Phương thức lấy mẫu
        String method = (String) payload.get("method");
        booking.setIsCenterCollected("Tại trung tâm".equals(method));
        // Ngày giờ hẹn
        String bookingDateStr = (String) payload.get("bookingDate");
        if (bookingDateStr != null && !bookingDateStr.isBlank()) {
            booking.setBookingDate(LocalDateTime.parse(bookingDateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
        String appointmentDateTime = (String) payload.get("appointmentDateTime");
        if (appointmentDateTime != null && !appointmentDateTime.isBlank()) {
            String[] parts = appointmentDateTime.split(" ");
            if (parts.length > 0) {
                booking.setCenterSampleDate(java.time.LocalDate.parse(parts[0]));
            }
            if (parts.length > 1) {
                booking.setCenterSampleTime(java.time.LocalTime.parse(parts[1]));
            }
        } else {
            booking.setCenterSampleDate(null);
            booking.setCenterSampleTime(null);
        }
        // Nếu trạng thái là Đã hủy và có cancelReason, ghi vào note
        if ("Đã hủy".equals(payload.get("status")) && payload.get("cancelReason") != null && !payload.get("cancelReason").toString().isBlank()) {
            String oldNote = booking.getNote();
            String newNote = (oldNote == null || oldNote.isBlank() ? "" : oldNote + "\n\n") + "Lý do hủy: " + payload.get("cancelReason");
            booking.setNote(newNote);
        }
        // Cập nhật dịch vụ
        List<Map<String, Object>> services = (List<Map<String, Object>>) payload.get("services");
        if (services != null) {
            bookingServiceRepository.deleteAll(bookingServiceRepository.findByBooking_Id(booking.getId()));
            for (Map<String, Object> serviceObj : services) {
                Integer serviceId = (Integer) serviceObj.get("serviceId");
                Service service = serviceRepository.findById(serviceId).orElse(null);
                if (service != null) {
                    BookingService bs = new BookingService();
                    bs.setBooking(booking);
                    bs.setService(service);
                    bookingServiceRepository.save(bs);
                }
            }
        }
        bookingRepository.save(booking);
        return ResponseEntity.ok("Cập nhật lịch hẹn thành công");
    }

    // API cập nhật trạng thái nhanh cho staff
    @PutMapping("/staff/appointments/{id}/status")
    public ResponseEntity<?> updateAppointmentStatusByStaff(@PathVariable Integer id, @RequestBody Map<String, Object> payload, @SessionAttribute(name = "staff", required = false) User sessionStaff) {
        if (sessionStaff == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("ACCESS_DENIED: Bạn không có quyền thực hiện thao tác này.");
        }
        Booking booking = bookingRepository.findById(id).orElse(null);
        if (booking == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy lịch hẹn");
        }
        String newStatus = (String) payload.get("status");
        if (newStatus == null || newStatus.isBlank()) {
            return ResponseEntity.badRequest().body("Trạng thái không hợp lệ");
        }
        booking.setStatus(newStatus);
        bookingRepository.save(booking);
        return ResponseEntity.ok("Cập nhật trạng thái thành công");
    }
}
