package com.genx.adnmanagement.controller;

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
import com.genx.adnmanagement.service.EmailService;
import com.genx.adnmanagement.service.SurchargeService;

@RestController
@RequestMapping("/api/kits")
public class KitController {
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private BookingServiceRepository bookingServiceRepository;
    @Autowired
    private ServiceRepository serviceRepository;
    @Autowired
    private KitTypeRepository kitTypeRepository;
    
    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private SurchargeService surchargeService;

    // Customer endpoint to get their kits
    @GetMapping
    public ResponseEntity<?> getCustomerKits(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String method,
            @RequestParam(required = false) String kitStatus,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String appointmentId,
            @SessionAttribute(name = "user", required = false) User sessionUser,
            @SessionAttribute(name = "staff", required = false) User sessionStaff) {
        
        // Allow both user and staff access
        if (sessionUser == null && sessionStaff == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("ACCESS_DENIED: Bạn không có quyền truy cập trang này.");
        }

        try {
            Pageable pageable = PageRequest.of(page, size);
            
            // For customer, only show their own bookings
            Integer userId = sessionUser != null ? sessionUser.getId() : null;
            
            // Convert date string to LocalDate if provided
            LocalDate bookingDate = null;
            if (date != null && !date.isBlank()) {
                try {
                    bookingDate = LocalDate.parse(date);
                } catch (Exception e) {
                    // If date parsing fails, ignore the date filter
                }
            }
            
            // Convert mapped status back to original status for filtering
            String originalKitStatus = null;
            if (kitStatus != null && !kitStatus.isBlank()) {
                originalKitStatus = convertMappedStatusToOriginal(kitStatus);
            }
            
            // Tìm booking có phương thức "Tại nhà" và thuộc về user này
            Page<Booking> bookingPage = bookingRepository.findByCustomerIdAndIsCenterCollectedFalseWithFilters(
                userId,
                appointmentId,
                bookingDate != null ? bookingDate.toString() : null,
                originalKitStatus,
                pageable
            );

            List<Map<String, Object>> kitsList = new ArrayList<>();
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            for (Booking booking : bookingPage.getContent()) {
                Map<String, Object> kitDetails = new HashMap<>();
                kitDetails.put("appointmentId", booking.getId());
                kitDetails.put("customerName", booking.getFullName());
                kitDetails.put("method", booking.getIsCenterCollected() ? "Tại trung tâm" : "Tại nhà");
                kitDetails.put("bookingDate", booking.getBookingDate().format(dateTimeFormatter));
                kitDetails.put("kitStatus", booking.getKitStatus());
                kitDetails.put("kitStatusDisplay", mapKitStatusForCustomer(booking.getKitStatus()));

                // Lấy thông tin dịch vụ
                List<BookingService> bookingServices = bookingServiceRepository.findByBookingId(booking.getId());
                List<Map<String, Object>> services = new ArrayList<>();

                for (BookingService bs : bookingServices) {
                    Service service = bs.getService();
                    Map<String, Object> serviceDetail = new HashMap<>();
                    serviceDetail.put("serviceId", service.getId());
                    serviceDetail.put("serviceName", service.getName());
                    serviceDetail.put("adnService", booking.getIsAdministrative() ? "Hành chính" : "Dân sự");
                    services.add(serviceDetail);
                }

                kitDetails.put("services", services);
                kitsList.add(kitDetails);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("content", kitsList);
            response.put("totalPages", bookingPage.getTotalPages());
            response.put("totalElements", bookingPage.getTotalElements());
            response.put("page", bookingPage.getNumber());
            response.put("size", bookingPage.getSize());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi lấy danh sách kit: " + e.getMessage());
        }
    }

    // Customer endpoint to get kit details
    @GetMapping("/{appointmentId}")
    public ResponseEntity<?> getKitDetails(
            @PathVariable Integer appointmentId,
            @SessionAttribute(name = "user", required = false) User sessionUser,
            @SessionAttribute(name = "staff", required = false) User sessionStaff) {
        
        // Allow both user and staff access
        if (sessionUser == null && sessionStaff == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("ACCESS_DENIED: Bạn không có quyền truy cập trang này.");
        }

        try {
            Booking booking = bookingRepository.findById(appointmentId).orElse(null);
            if (booking == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy cuộc hẹn");
            }

            // For customer, only allow access to their own booking
            if (sessionUser != null && (booking.getCustomer() == null || !booking.getCustomer().getId().equals(sessionUser.getId()))) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Bạn không có quyền truy cập thông tin này");
            }

            Map<String, Object> kitDetails = new HashMap<>();
            kitDetails.put("appointmentId", booking.getId());
            kitDetails.put("customerName", booking.getFullName());
            kitDetails.put("method", booking.getIsCenterCollected() ? "Tại trung tâm" : "Tại nhà");
            kitDetails.put("bookingDate", booking.getBookingDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            kitDetails.put("createdDate", booking.getBookingDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            kitDetails.put("kitStatus", booking.getKitStatus());
            kitDetails.put("kitStatusDisplay", mapKitStatusForCustomer(booking.getKitStatus()));

            // Lấy thông tin dịch vụ
            List<BookingService> bookingServices = bookingServiceRepository.findByBookingId(booking.getId());
            List<Map<String, Object>> services = new ArrayList<>();

            for (BookingService bs : bookingServices) {
                Service service = bs.getService();
                Map<String, Object> serviceDetail = new HashMap<>();
                serviceDetail.put("serviceId", service.getId());
                serviceDetail.put("serviceName", service.getName());
                serviceDetail.put("adnService", booking.getIsAdministrative() ? "Hành chính" : "Dân sự");
                services.add(serviceDetail);
            }

            kitDetails.put("services", services);
            return ResponseEntity.ok(kitDetails);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi lấy chi tiết kit: " + e.getMessage());
        }
    }

    // Customer endpoint to confirm kit received
    @PostMapping("/{appointmentId}/received")
    public ResponseEntity<?> confirmKitReceived(
            @PathVariable Integer appointmentId,
            @SessionAttribute(name = "user", required = false) User sessionUser,
            @SessionAttribute(name = "staff", required = false) User sessionStaff) {
        
        // Allow both user and staff access
        if (sessionUser == null && sessionStaff == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("ACCESS_DENIED: Bạn không có quyền truy cập trang này.");
        }

        try {
            Booking booking = bookingRepository.findById(appointmentId).orElse(null);
            if (booking == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy cuộc hẹn");
            }

            // For customer, only allow access to their own booking
            if (sessionUser != null && (booking.getCustomer() == null || !booking.getCustomer().getId().equals(sessionUser.getId()))) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Bạn không có quyền thực hiện thao tác này");
            }

            // Check if current status is "Đã gửi"
            if (!"Đã gửi".equals(booking.getKitStatus())) {
                return ResponseEntity.badRequest().body("Chỉ có thể xác nhận nhận kit khi trạng thái là 'Đã gửi'");
            }

            // Update status to "Đã giao thành công" (tương ứng với "Đã nhận kit" trong mapping)
            booking.setKitStatus("Đã giao thành công");
            // booking.setKitReceiveDate(LocalDate.now()); // Tạm thời comment lại
            bookingRepository.save(booking);

            return ResponseEntity.ok("Đã xác nhận nhận kit thành công");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi xác nhận nhận kit: " + e.getMessage());
        }
    }

    // Customer endpoint to confirm kit returned
    @PostMapping("/{appointmentId}/returned")
    public ResponseEntity<?> confirmKitReturned(
            @PathVariable Integer appointmentId,
            @SessionAttribute(name = "user", required = false) User sessionUser,
            @SessionAttribute(name = "staff", required = false) User sessionStaff) {
        
        // Allow both user and staff access
        if (sessionUser == null && sessionStaff == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("ACCESS_DENIED: Bạn không có quyền truy cập trang này.");
        }

        try {
            Booking booking = bookingRepository.findById(appointmentId).orElse(null);
            if (booking == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy cuộc hẹn");
            }

            // For customer, only allow access to their own booking
            if (sessionUser != null && (booking.getCustomer() == null || !booking.getCustomer().getId().equals(sessionUser.getId()))) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Bạn không có quyền thực hiện thao tác này");
            }

            // Check if current status is "Đã giao thành công" (tương ứng với "Đã nhận kit" trong mapping)
            if (!"Đã giao thành công".equals(booking.getKitStatus())) {
                return ResponseEntity.badRequest().body("Chỉ có thể xác nhận trả kit khi trạng thái là 'Đã nhận kit'");
            }

            // Update status to "Chưa nhận mẫu"
            booking.setKitStatus("Chưa nhận mẫu");
            bookingRepository.save(booking);

            return ResponseEntity.ok("Đã xác nhận trả kit thành công");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi xác nhận trả kit: " + e.getMessage());
        }
    }


    @GetMapping("/staff")
    public ResponseEntity<?> getKits(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @SessionAttribute(name = "staff", required = false) User sessionStaff) {
        
        if (sessionStaff == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("ACCESS_DENIED: Bạn không có quyền truy cập trang này.");
        }

        try {
            // Đảm bảo search và status là null nếu rỗng hoặc chỉ chứa khoảng trắng
            if (search != null && search.isBlank()) search = null;
            if (status != null && status.isBlank()) status = null;
            Pageable pageable = PageRequest.of(page, size);
            
            // Tìm tất cả booking có phương thức "Tại nhà" và trạng thái từ "Chưa lấy mẫu" trở đi
            Page<Booking> bookingPage = bookingRepository.findByIsCenterCollectedFalseWithFilters(
                search,
                status, // status parameter này giờ là kitStatus
                pageable
            );

            List<Map<String, Object>> kitsList = new ArrayList<>();
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            for (Booking booking : bookingPage.getContent()) {

                // Lấy thông tin dịch vụ
                List<BookingService> bookingServices = bookingServiceRepository.findByBookingId(booking.getId());

                for (BookingService bs : bookingServices) {
                    Service service = bs.getService();
                    Map<String, Object> kitDetails = new HashMap<>();
                    kitDetails.put("appointmentId", booking.getId());
                    kitDetails.put("customerName", booking.getFullName());
                    kitDetails.put("address", booking.getAddress());
                    kitDetails.put("status", booking.getKitStatus());
                    kitDetails.put("serviceId", service.getId());
                    kitDetails.put("serviceName", service.getName());
                    kitDetails.put("kitType", bs.getKitType() != null ? bs.getKitType().getName() : null);
                    // Bổ sung phương thức thanh toán
                    kitDetails.put("paymentMethod", booking.getPaymentMethod());
                    // Bổ sung remainingAmount
                    double totalAmount = bookingServices.stream().mapToDouble(b -> b.getService().getPrice().doubleValue()).sum();
                    double totalPaid = paymentRepository.findByBooking_Id(booking.getId()).stream().mapToDouble(p -> p.getAmount().doubleValue()).sum();
                    double remainingAmount = totalAmount - totalPaid;
                    kitDetails.put("remainingAmount", remainingAmount);
                    // kitDetails.put("kitCode", bs.getKitCode()); // Tạm thời comment lại
                    // kitDetails.put("sendDate", booking.getKitSendDate()); // Tạm thời comment lại
                    // kitDetails.put("receiveDate", booking.getKitReceiveDate()); // Tạm thời comment lại
                    // kitDetails.put("note", booking.getKitNote()); // Tạm thời comment lại
                    
                    kitsList.add(kitDetails);
                }
            }

            Map<String, Object> response = new HashMap<>();
            response.put("content", kitsList);
            response.put("totalPages", bookingPage.getTotalPages());
            response.put("totalElements", bookingPage.getTotalElements());
            response.put("pageable", Map.of(
                "pageNumber", bookingPage.getNumber(),
                "pageSize", bookingPage.getSize()
            ));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi lấy danh sách kit: " + e.getMessage());
        }
    }

    @GetMapping("/appointment/{appointmentId}/services")
    public ResponseEntity<?> getAppointmentServices(@PathVariable Integer appointmentId) {
        try {
            Booking booking = bookingRepository.findById(appointmentId).orElse(null);
            if (booking == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy cuộc hẹn");
            }

            List<BookingService> bookingServices = bookingServiceRepository.findByBookingId(appointmentId);
            List<Map<String, Object>> services = new ArrayList<>();

            for (BookingService bs : bookingServices) {
                Service service = bs.getService();
                Map<String, Object> serviceDetail = new HashMap<>();
                serviceDetail.put("serviceId", service.getId());
                serviceDetail.put("serviceName", service.getName());
                serviceDetail.put("customerName", booking.getFullName());
                
                // Thêm thông tin kitType hiện tại nếu có
                if (bs.getKitType() != null) {
                    serviceDetail.put("kitTypeId", bs.getKitType().getId());
                    serviceDetail.put("kitTypeName", bs.getKitType().getName());
                } else {
                    serviceDetail.put("kitTypeId", null);
                    serviceDetail.put("kitTypeName", null);
                }
                
                services.add(serviceDetail);
            }

            return ResponseEntity.ok(services);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi lấy dịch vụ: " + e.getMessage());
        }
    }

    @GetMapping("/services/{serviceId}/kit-types")
    public ResponseEntity<?> getKitTypesForService(@PathVariable Integer serviceId) {
        try {
            Service service = serviceRepository.findById(serviceId).orElse(null);
            if (service == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy dịch vụ");
            }

            // Lấy kit types phù hợp với dịch vụ này
            List<KitType> kitTypes = kitTypeRepository.findByServiceId(serviceId);
            
            List<Map<String, Object>> kitTypesList = new ArrayList<>();
            for (KitType kitType : kitTypes) {
                Map<String, Object> kitTypeDetail = new HashMap<>();
                kitTypeDetail.put("id", kitType.getId());
                kitTypeDetail.put("name", kitType.getName());
                kitTypeDetail.put("description", kitType.getDescription());
                kitTypesList.add(kitTypeDetail);
            }

            return ResponseEntity.ok(kitTypesList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi lấy loại kit: " + e.getMessage());
        }
    }

    @PostMapping("/select-multiple")
    public ResponseEntity<?> selectMultipleKits(@RequestBody List<Map<String, Object>> kitSelections) {
        try {
            // Lấy booking ID từ selection đầu tiên (tất cả đều cùng booking ID)
            Integer bookingId = null;
            if (!kitSelections.isEmpty()) {
                bookingId = convertToInteger(kitSelections.get(0).get("appointmentId"));
            }
            
            // Lấy booking một lần duy nhất
            Booking booking = null;
            if (bookingId != null) {
                booking = bookingRepository.findById(bookingId).orElse(null);
            }
            
            // Kiểm tra booking null trước khi xử lý
            if (booking == null) {
                return ResponseEntity.badRequest().body("Booking not found or invalid booking ID");
            }
            
            for (Map<String, Object> selection : kitSelections) {
                Integer appointmentId = convertToInteger(selection.get("appointmentId"));
                Integer serviceId = convertToInteger(selection.get("serviceId"));
                Integer kitTypeId = convertToInteger(selection.get("kitTypeId"));

                // Validate required fields
                if (appointmentId == null || serviceId == null || kitTypeId == null) {
                    return ResponseEntity.badRequest().body("Thiếu thông tin bắt buộc: appointmentId, serviceId, hoặc kitTypeId");
                }

                BookingService bookingService = bookingServiceRepository.findByBookingIdAndServiceId(appointmentId, serviceId).orElse(null);
                if (bookingService == null) continue;

                KitType kitType = kitTypeRepository.findById(kitTypeId).orElse(null);
                if (kitType != null) {
                    bookingService.setKitType(kitType);
                }

                // bookingService.setKitCode(kitCode); // Tạm thời comment lại
                // booking.setKitNote(note); // Tạm thời comment lại

                bookingServiceRepository.save(bookingService);
            }
            
            // Lưu booking một lần duy nhất sau khi xử lý tất cả booking services
            if (booking != null) {
                // Chỉ cập nhật trạng thái nếu hiện tại là "Chưa chọn kit"
                if ("Chưa chọn kit".equals(booking.getKitStatus())) {
                    booking.setKitStatus("Chưa gửi");
                }
                bookingRepository.save(booking);
            }

            return ResponseEntity.ok("Đã chọn kit thành công");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi chọn kit: " + e.getMessage());
        }
    }

    @PutMapping("/update-multiple")
    public ResponseEntity<?> updateMultipleKits(@RequestBody List<Map<String, Object>> kitSelections) {
        try {
            // Lấy booking ID từ selection đầu tiên (tất cả đều cùng booking ID)
            Integer bookingId = null;
            if (!kitSelections.isEmpty()) {
                bookingId = convertToInteger(kitSelections.get(0).get("appointmentId"));
            }
            
            // Lấy booking một lần duy nhất
            Booking booking = null;
            if (bookingId != null) {
                booking = bookingRepository.findById(bookingId).orElse(null);
            }
            
            // Kiểm tra booking null trước khi xử lý
            if (booking == null) {
                return ResponseEntity.badRequest().body("Booking not found or invalid booking ID");
            }
            
            // Kiểm tra trạng thái hiện tại - chỉ cho phép cập nhật khi "Chưa gửi"
            if (!"Chưa gửi".equals(booking.getKitStatus())) {
                return ResponseEntity.badRequest().body("Chỉ có thể cập nhật kit khi trạng thái là 'Chưa gửi'");
            }
            
            for (Map<String, Object> selection : kitSelections) {
                Integer appointmentId = convertToInteger(selection.get("appointmentId"));
                Integer serviceId = convertToInteger(selection.get("serviceId"));
                Integer kitTypeId = convertToInteger(selection.get("kitTypeId"));

                // Validate required fields
                if (appointmentId == null || serviceId == null || kitTypeId == null) {
                    return ResponseEntity.badRequest().body("Thiếu thông tin bắt buộc: appointmentId, serviceId, hoặc kitTypeId");
                }

                BookingService bookingService = bookingServiceRepository.findByBookingIdAndServiceId(appointmentId, serviceId).orElse(null);
                if (bookingService == null) continue;

                KitType kitType = kitTypeRepository.findById(kitTypeId).orElse(null);
                if (kitType != null) {
                    bookingService.setKitType(kitType);
                }

                bookingServiceRepository.save(bookingService);
            }

            return ResponseEntity.ok("Đã cập nhật kit thành công");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi cập nhật kit: " + e.getMessage());
        }
    }

    @PutMapping("/appointment/{appointmentId}/send")
    public ResponseEntity<?> sendKits(@PathVariable Integer appointmentId, @RequestBody Map<String, Object> payload) {
        try {
            Booking booking = bookingRepository.findById(appointmentId).orElse(null);
            if (booking == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy cuộc hẹn");
            }

            // 1. Kiểm tra phương thức thanh toán đã được chọn chưa
            if (booking.getPaymentMethod() == null || booking.getPaymentMethod().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Vui lòng chọn phương thức thanh toán trước khi gửi kit");
            }

            // 2. Kiểm tra tất cả dịch vụ trong booking đã có kitType chưa
            List<BookingService> bookingServices = bookingServiceRepository.findByBookingId(appointmentId);
            for (BookingService bs : bookingServices) {
                if (bs.getKitType() == null) {
                    return ResponseEntity.badRequest().body("Vui lòng chọn kit cho tất cả dịch vụ trước khi gửi");
                }
            }

            // 3. Kiểm tra trạng thái hiện tại có phải "Chưa gửi" không
            if (!"Chưa gửi".equals(booking.getKitStatus())) {
                return ResponseEntity.badRequest().body("Chỉ có thể gửi kit khi trạng thái là 'Chưa gửi'");
            }

            // 4. Thực hiện gửi kit
            booking.setKitStatus("Đã gửi");
            bookingRepository.save(booking);

            // 5. Gửi email thông báo kit đã được gửi
            try {
                if (booking.getEmail() != null && !booking.getEmail().isBlank()) {
                    String subject = "Thông báo: Kit xét nghiệm ADN đã được gửi";
                    StringBuilder text = new StringBuilder();
                    text.append("Chào ").append(booking.getFullName()).append(",\n\n");
                    text.append("Kit xét nghiệm ADN cho lịch hẹn (Mã: ").append(booking.getId()).append(") đã được gửi tới bạn.\n");
                    text.append("Vui lòng theo dõi quá trình nhận kit và cập nhật trạng thái trên hệ thống khi đã nhận kit hoặc gửi lại kit sau khi lấy mẫu.\n");
                    text.append("Bạn có thể theo dõi và cập nhật trạng thái tại: http://localhost:8080/kits\n");
                    text.append("Nếu có thắc mắc, vui lòng liên hệ trung tâm để được hỗ trợ.\n");
                    text.append("Trân trọng!\nTrung tâm xét nghiệm ADN Genx");
                    emailService.sendSimpleEmail(booking.getEmail(), subject, text.toString());
                }
            } catch (Exception e) {
                // Không throw lỗi gửi mail, chỉ log
            }

            return ResponseEntity.ok("Đã gửi kit thành công");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi gửi kit: " + e.getMessage());
        }
    }

    @PutMapping("/appointment/{appointmentId}/status")
    public ResponseEntity<?> updateKitStatus(@PathVariable Integer appointmentId, @RequestBody Map<String, Object> payload) {
        try {
            Booking booking = bookingRepository.findById(appointmentId).orElse(null);
            if (booking == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy cuộc hẹn");
            }

            String newStatus = (String) payload.get("status");
            if (newStatus != null) {
                // Kiểm tra ràng buộc thanh toán khi cập nhật thành "Đã nhận mẫu"
                if ("Đã nhận mẫu".equals(newStatus)) {
                    // Không kiểm tra thanh toán ở đây nữa, đã tách ra endpoint riêng
                }

                booking.setKitStatus(newStatus);
                
                // Logic tự động: Khi kit status = "Đã nhận mẫu" → tự động cập nhật booking status = "Đã lấy mẫu"
                if ("Đã nhận mẫu".equals(newStatus) && "Chưa lấy mẫu".equals(booking.getStatus())) {
                    booking.setStatus("Đã lấy mẫu");
                }
            }

            // String receiveDate = (String) payload.get("receiveDate");
            // if (receiveDate != null) {
            //     booking.setKitReceiveDate(LocalDate.parse(receiveDate));
            // }

            bookingRepository.save(booking);
            return ResponseEntity.ok("Cập nhật trạng thái kit thành công");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi cập nhật trạng thái kit: " + e.getMessage());
        }
    }

    // Kiểm tra đủ điều kiện gửi kit (>=10% nếu BANK_TRANSFER)
    @GetMapping("/appointment/{appointmentId}/check-send-kit-payment")
    public ResponseEntity<?> checkSendKitPayment(@PathVariable Integer appointmentId) {
        Booking booking = bookingRepository.findById(appointmentId).orElse(null);
        if (booking == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy cuộc hẹn");
        }
        if (!"BANK_TRANSFER".equals(booking.getPaymentMethod())) {
            // Nếu không phải chuyển khoản, luôn cho phép gửi kit
            return ResponseEntity.ok(Map.of("ok", true));
        }
        List<BookingService> bookingServices = bookingServiceRepository.findByBookingId(appointmentId);
        // Tính tổng tiền dịch vụ (bao gồm extra per sample fee)
        java.math.BigDecimal totalServiceAmount = java.math.BigDecimal.ZERO;
        for (BookingService bs : bookingServices) {
            Service service = bs.getService();
            java.math.BigDecimal extraSampleFee = service.getExtraPerSampleFee() != null ? service.getExtraPerSampleFee() : java.math.BigDecimal.ZERO;
            int extraSamples = Math.max(0, bs.getSampleQuantity() - 2);
            java.math.BigDecimal extraSampleCost = extraSampleFee.multiply(java.math.BigDecimal.valueOf(extraSamples));
            java.math.BigDecimal serviceTotal = service.getPrice().add(extraSampleCost);
            totalServiceAmount = totalServiceAmount.add(serviceTotal);
        }
        // Tính surcharge
        java.math.BigDecimal totalSurchargeAmount = java.math.BigDecimal.ZERO;
        List<Surcharge> activeSurcharges = surchargeService.getActiveSurcharges();
        for (Surcharge surcharge : activeSurcharges) {
            if (booking != null && isSurchargeApplicableForBooking(surcharge, booking)) {
                java.math.BigDecimal surchargeAmount = calculateSurchargeAmount(surcharge, booking);
                if (surchargeAmount.compareTo(java.math.BigDecimal.ZERO) > 0) {
                    totalSurchargeAmount = totalSurchargeAmount.add(surchargeAmount);
                }
            }
        }
        java.math.BigDecimal totalAmount = totalServiceAmount.add(totalSurchargeAmount);
        java.math.BigDecimal totalPaid = paymentRepository.findByBooking_Id(appointmentId).stream().map(p -> p.getAmount()).reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
        java.math.BigDecimal minRequired = totalAmount.multiply(java.math.BigDecimal.valueOf(0.1)).setScale(0, java.math.RoundingMode.UP);
        java.math.BigDecimal needToPay = minRequired.subtract(totalPaid);
        List<Payment> payments = paymentRepository.findByBooking_Id(appointmentId);
        boolean hasPendingPayment = payments.stream().anyMatch(p -> p.getAmount() != null && p.getAmount().doubleValue() == 0);
        if (totalPaid.compareTo(minRequired) < 0) {
            Map<String, Object> resp = new HashMap<>();
            resp.put("ok", false);
            resp.put("totalPaid", totalPaid);
            resp.put("totalAmount", totalAmount);
            resp.put("remainingAmount", needToPay);
            resp.put("hasPendingPayment", hasPendingPayment);
            resp.put("paymentPercentage", totalAmount.compareTo(java.math.BigDecimal.ZERO) > 0 ? totalPaid.multiply(java.math.BigDecimal.valueOf(100)).divide(totalAmount, 1, java.math.RoundingMode.HALF_UP) : java.math.BigDecimal.ZERO);
            if (hasPendingPayment) {
                resp.put("message", String.format("Không thể gửi kit. Khách hàng chưa thanh toán đủ tối thiểu 10%%. Có thanh toán cần xử lý. Đã thanh toán: %s VND / Tối thiểu: %s VND (Thiếu: %s VND)", totalPaid.toPlainString(), minRequired.toPlainString(), needToPay.toPlainString()));
            } else {
                resp.put("message", String.format("Không thể gửi kit. Khách hàng chưa thanh toán đủ tối thiểu 10%%. Đã thanh toán: %s VND / Tối thiểu: %s VND (Thiếu: %s VND)", totalPaid.toPlainString(), minRequired.toPlainString(), needToPay.toPlainString()));
            }
            return ResponseEntity.ok(resp);
        }
        return ResponseEntity.ok(Map.of("ok", true));
    }

    // Kiểm tra đủ điều kiện nhận mẫu (đủ 100%)
    @GetMapping("/appointment/{appointmentId}/check-receive-sample-payment")
    public ResponseEntity<?> checkReceiveSamplePayment(@PathVariable Integer appointmentId) {
        Booking booking = bookingRepository.findById(appointmentId).orElse(null);
        if (booking == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy cuộc hẹn");
        }
        List<BookingService> bookingServices = bookingServiceRepository.findByBookingId(appointmentId);
        // Tính tổng tiền dịch vụ (bao gồm extra per sample fee)
        java.math.BigDecimal totalServiceAmount = java.math.BigDecimal.ZERO;
        for (BookingService bs : bookingServices) {
            Service service = bs.getService();
            java.math.BigDecimal extraSampleFee = service.getExtraPerSampleFee() != null ? service.getExtraPerSampleFee() : java.math.BigDecimal.ZERO;
            int extraSamples = Math.max(0, bs.getSampleQuantity() - 2);
            java.math.BigDecimal extraSampleCost = extraSampleFee.multiply(java.math.BigDecimal.valueOf(extraSamples));
            java.math.BigDecimal serviceTotal = service.getPrice().add(extraSampleCost);
            totalServiceAmount = totalServiceAmount.add(serviceTotal);
        }
        // Tính surcharge
        java.math.BigDecimal totalSurchargeAmount = java.math.BigDecimal.ZERO;
        List<Surcharge> activeSurcharges = surchargeService.getActiveSurcharges();
        for (Surcharge surcharge : activeSurcharges) {
            if (booking != null && isSurchargeApplicableForBooking(surcharge, booking)) {
                java.math.BigDecimal surchargeAmount = calculateSurchargeAmount(surcharge, booking);
                if (surchargeAmount.compareTo(java.math.BigDecimal.ZERO) > 0) {
                    totalSurchargeAmount = totalSurchargeAmount.add(surchargeAmount);
                }
            }
        }
        java.math.BigDecimal totalAmount = totalServiceAmount.add(totalSurchargeAmount);
        java.math.BigDecimal totalPaid = paymentRepository.findByBooking_Id(appointmentId).stream().map(p -> p.getAmount()).reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
        java.math.BigDecimal needToPay = totalAmount.subtract(totalPaid);
        List<Payment> payments = paymentRepository.findByBooking_Id(appointmentId);
        boolean hasPendingPayment = payments.stream().anyMatch(p -> p.getAmount() != null && p.getAmount().doubleValue() == 0);
        if (totalPaid.compareTo(totalAmount) < 0) {
            Map<String, Object> resp = new HashMap<>();
            resp.put("ok", false);
            resp.put("totalPaid", totalPaid);
            resp.put("totalAmount", totalAmount);
            resp.put("remainingAmount", needToPay);
            resp.put("hasPendingPayment", hasPendingPayment);
            resp.put("paymentPercentage", totalAmount.compareTo(java.math.BigDecimal.ZERO) > 0 ? totalPaid.multiply(java.math.BigDecimal.valueOf(100)).divide(totalAmount, 1, java.math.RoundingMode.HALF_UP) : java.math.BigDecimal.ZERO);
            if (hasPendingPayment) {
                resp.put("message", String.format("Không thể cập nhật trạng thái 'Đã nhận mẫu'. Khách hàng chưa thanh toán đủ. Có thanh toán cần xử lý. Đã thanh toán: %s VND / Tổng tiền: %s VND (Thiếu: %s VND)", totalPaid.toPlainString(), totalAmount.toPlainString(), needToPay.toPlainString()));
            } else {
                resp.put("message", String.format("Không thể cập nhật trạng thái 'Đã nhận mẫu'. Khách hàng chưa thanh toán đủ. Đã thanh toán: %s VND / Tổng tiền: %s VND (Thiếu: %s VND)", totalPaid.toPlainString(), totalAmount.toPlainString(), needToPay.toPlainString()));
            }
            return ResponseEntity.ok(resp);
        }
        return ResponseEntity.ok(Map.of("ok", true));
    }

    // Nhắc khách hàng thanh toán đủ 10% để gửi kit
    @PostMapping("/appointment/{appointmentId}/remind-10-percent-payment")
    public ResponseEntity<?> remind10PercentPayment(@PathVariable Integer appointmentId) {
        try {
            Booking booking = bookingRepository.findById(appointmentId).orElse(null);
            if (booking == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy cuộc hẹn");
            }
            if (booking.getEmail() == null || booking.getEmail().isBlank()) {
                return ResponseEntity.badRequest().body("Không có email khách hàng để gửi nhắc thanh toán");
            }
            // Tính tổng tiền dịch vụ (bao gồm extra per sample fee)
            java.math.BigDecimal totalServiceAmount = java.math.BigDecimal.ZERO;
            java.util.List<BookingService> bookingServices = bookingServiceRepository.findByBookingId(appointmentId);
            for (BookingService bs : bookingServices) {
                Service service = bs.getService();
                java.math.BigDecimal extraSampleFee = service.getExtraPerSampleFee() != null ? service.getExtraPerSampleFee() : java.math.BigDecimal.ZERO;
                int extraSamples = Math.max(0, bs.getSampleQuantity() - 2);
                java.math.BigDecimal extraSampleCost = extraSampleFee.multiply(java.math.BigDecimal.valueOf(extraSamples));
                java.math.BigDecimal serviceTotal = service.getPrice().add(extraSampleCost);
                totalServiceAmount = totalServiceAmount.add(serviceTotal);
            }
            // Tính surcharge
            java.math.BigDecimal totalSurchargeAmount = java.math.BigDecimal.ZERO;
            java.util.List<Surcharge> activeSurcharges = surchargeService.getActiveSurcharges();
            for (Surcharge surcharge : activeSurcharges) {
                if (booking != null && isSurchargeApplicableForBooking(surcharge, booking)) {
                    java.math.BigDecimal surchargeAmount = calculateSurchargeAmount(surcharge, booking);
                    if (surchargeAmount.compareTo(java.math.BigDecimal.ZERO) > 0) {
                        totalSurchargeAmount = totalSurchargeAmount.add(surchargeAmount);
                    }
                }
            }
            java.math.BigDecimal totalAmount = totalServiceAmount.add(totalSurchargeAmount);
            java.math.BigDecimal totalPaid = paymentRepository.findByBooking_Id(appointmentId).stream().map(p -> p.getAmount()).reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
            java.math.BigDecimal minRequired = totalAmount.multiply(java.math.BigDecimal.valueOf(0.1)).setScale(0, java.math.RoundingMode.UP);
            java.math.BigDecimal needToPay = minRequired.subtract(totalPaid);
            if (needToPay.compareTo(java.math.BigDecimal.ZERO) <= 0) {
                return ResponseEntity.ok("Khách hàng đã đủ điều kiện, không cần nhắc thanh toán");
            }

            try {
                String subject = "Nhắc thanh toán lịch hẹn xét nghiệm ADN";
                StringBuilder text = new StringBuilder();
                text.append("Chào ").append(booking.getFullName()).append(",\n\n");
                text.append("Lịch hẹn xét nghiệm ADN của bạn (Mã: ").append(booking.getId()).append(") cần thanh toán trước tối thiểu 10% tổng phí để được gửi kit lấy mẫu.\n");
                text.append("Tổng phí: ").append(String.format("%,.0f", totalAmount)).append(" VNĐ\n");
                text.append("Bạn đã thanh toán: ").append(String.format("%,.0f", totalPaid)).append(" VNĐ\n");
                text.append("Bạn cần thanh toán thêm ít nhất: ").append(String.format("%,.0f", needToPay)).append(" VNĐ để đủ điều kiện gửi kit.\n");
                text.append("Vui lòng đăng nhập hệ thống để thực hiện thanh toán hoặc liên hệ trung tâm để được hỗ trợ.\n");
                text.append("Xem chi tiết lịch hẹn và thanh toán tại: http://localhost:8080/appoinments-list\n");
                text.append("Trân trọng!\nTrung tâm xét nghiệm ADN Genx");
                emailService.sendSimpleEmail(booking.getEmail(), subject, text.toString());
                return ResponseEntity.ok("Đã gửi nhắc thanh toán tới khách hàng");
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Lỗi khi gửi email nhắc thanh toán: " + e.getMessage());
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi gửi nhắc thanh toán: " + e.getMessage());
        }
    }

    // Nhắc khách hàng thanh toán đủ 100% để nhận mẫu
    @PostMapping("/appointment/{appointmentId}/remind-full-payment")
    public ResponseEntity<?> remindFullPayment(@PathVariable Integer appointmentId) {
        try {
            Booking booking = bookingRepository.findById(appointmentId).orElse(null);
            if (booking == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy cuộc hẹn");
            }
            if (booking.getEmail() == null || booking.getEmail().isBlank()) {
                return ResponseEntity.badRequest().body("Không có email khách hàng để gửi nhắc thanh toán");
            }
            // Tính tổng tiền dịch vụ (bao gồm extra per sample fee)
            java.math.BigDecimal totalServiceAmount = java.math.BigDecimal.ZERO;
            java.util.List<BookingService> bookingServices = bookingServiceRepository.findByBookingId(appointmentId);
            for (BookingService bs : bookingServices) {
                Service service = bs.getService();
                java.math.BigDecimal extraSampleFee = service.getExtraPerSampleFee() != null ? service.getExtraPerSampleFee() : java.math.BigDecimal.ZERO;
                int extraSamples = Math.max(0, bs.getSampleQuantity() - 2);
                java.math.BigDecimal extraSampleCost = extraSampleFee.multiply(java.math.BigDecimal.valueOf(extraSamples));
                java.math.BigDecimal serviceTotal = service.getPrice().add(extraSampleCost);
                totalServiceAmount = totalServiceAmount.add(serviceTotal);
            }
            // Tính surcharge
            java.math.BigDecimal totalSurchargeAmount = java.math.BigDecimal.ZERO;
            java.util.List<Surcharge> activeSurcharges = surchargeService.getActiveSurcharges();
            for (Surcharge surcharge : activeSurcharges) {
                if (booking != null && isSurchargeApplicableForBooking(surcharge, booking)) {
                    java.math.BigDecimal surchargeAmount = calculateSurchargeAmount(surcharge, booking);
                    if (surchargeAmount.compareTo(java.math.BigDecimal.ZERO) > 0) {
                        totalSurchargeAmount = totalSurchargeAmount.add(surchargeAmount);
                    }
                }
            }
            java.math.BigDecimal totalAmount = totalServiceAmount.add(totalSurchargeAmount);
            java.math.BigDecimal totalPaid = paymentRepository.findByBooking_Id(appointmentId).stream().map(p -> p.getAmount()).reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
            java.math.BigDecimal needToPay = totalAmount.subtract(totalPaid);
            if (needToPay.compareTo(java.math.BigDecimal.ZERO) <= 0) {
                return ResponseEntity.ok("Khách hàng đã thanh toán đủ, không cần nhắc thanh toán");
            }

            try {
                String subject = "Nhắc thanh toán đủ lịch hẹn xét nghiệm ADN";
                StringBuilder text = new StringBuilder();
                text.append("Chào ").append(booking.getFullName()).append(",\n\n");
                text.append("Lịch hẹn xét nghiệm ADN của bạn (Mã: ").append(booking.getId()).append(") cần thanh toán đủ 100% tổng phí để tiếp tục quy trình xét nghiệm.\n");
                text.append("Tổng phí: ").append(String.format("%,.0f", totalAmount)).append(" VNĐ\n");
                text.append("Bạn đã thanh toán: ").append(String.format("%,.0f", totalPaid)).append(" VNĐ\n");
                text.append("Bạn cần thanh toán thêm: ").append(String.format("%,.0f", needToPay)).append(" VNĐ để hoàn tất thanh toán.\n");
                text.append("Vui lòng đăng nhập hệ thống để thực hiện thanh toán hoặc liên hệ trung tâm để được hỗ trợ.\n");
                text.append("Xem chi tiết lịch hẹn và thanh toán tại: http://localhost:8080/appoinments-list\n");
                text.append("Trân trọng!\nTrung tâm xét nghiệm ADN Genx");
                emailService.sendSimpleEmail(booking.getEmail(), subject, text.toString());
                return ResponseEntity.ok("Đã gửi nhắc thanh toán đủ tới khách hàng");
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Lỗi khi gửi email nhắc thanh toán đủ: " + e.getMessage());
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi gửi nhắc thanh toán đủ: " + e.getMessage());
        }
    }

    // Convert mapped status back to original status for filtering
    private String convertMappedStatusToOriginal(String mappedStatus) {
        return switch (mappedStatus) {
            case "Chưa nhận kit" -> "Đã gửi";
            case "Đã nhận kit" -> "Đã giao thành công";
            case "Đã trả kit" -> "Chưa nhận mẫu";
            case "Hoàn thành lấy mẫu" -> "Đã nhận mẫu";
            case "Lỗi mẫu" -> "Lỗi mẫu";
            default -> mappedStatus;
        };
    }

    // Map kit status from internal values to customer-friendly display names
    private String mapKitStatusForCustomer(String originalStatus) {
        return switch (originalStatus) {
            case "Chưa chọn kit" -> "Chưa chọn kit";
            case "Chưa gửi" -> "Chưa gửi";
            case "Đã gửi" -> "Chưa nhận kit";
            case "Đã giao thành công" -> "Đã nhận kit";
            case "Chưa nhận mẫu" -> "Đã trả kit";
            case "Đã nhận mẫu" -> "Hoàn thành lấy mẫu";
            case "Lỗi mẫu" -> "Lỗi mẫu";
            default -> originalStatus;
        };
    }

    // Helper method to safely convert Object to Integer
    private Integer convertToInteger(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Integer) {
            return (Integer) value;
        }
        if (value instanceof String) {
            try {
                return Integer.parseInt((String) value);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return null;
    }

    // Helper method để kiểm tra xem phụ phí có áp dụng cho booking này không
    private boolean isSurchargeApplicableForBooking(Surcharge surcharge, Booking booking) {
        // Nếu tên phụ phí chứa từ khóa "hành chính", chỉ áp dụng cho booking hành chính
        if (surcharge.getName().toLowerCase().contains("hành chính") || 
            surcharge.getName().toLowerCase().contains("administrative") ||
            surcharge.getName().toLowerCase().contains("legal")) {
            return booking.getIsAdministrative() != null && booking.getIsAdministrative();
        }
        // Các phụ phí khác áp dụng cho tất cả booking
        return true;
    }

    // Helper method để tính phụ phí cho một loại surcharge
    private java.math.BigDecimal calculateSurchargeAmount(Surcharge surcharge, Booking booking) {
        switch (surcharge.getUnit()) {
            case "participant":
                // Tính theo số người tham gia
                return surcharge.getFeePerUnit().multiply(java.math.BigDecimal.valueOf(booking.getNumParticipants()));
            case "booking":
                // Tính theo booking (1 lần)
                return surcharge.getFeePerUnit();
            case "sample":
                // Tính theo tổng số mẫu (cần tính từ BookingService)
                // Sẽ được tính riêng trong BookingServiceRepository
                return java.math.BigDecimal.ZERO;
            default:
                return java.math.BigDecimal.ZERO;
        }
    }
}

