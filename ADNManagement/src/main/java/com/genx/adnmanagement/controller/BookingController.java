package com.genx.adnmanagement.controller;

import com.genx.adnmanagement.dto.BookingRequest;
import com.genx.adnmanagement.entity.*;
import com.genx.adnmanagement.entity.Surcharge;
import com.genx.adnmanagement.repository.*;
import com.genx.adnmanagement.repository.PaymentRepository;
import com.genx.adnmanagement.service.SurchargeService;
import com.genx.adnmanagement.service.BankInfoService;
import com.genx.adnmanagement.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.LocalDate;
import java.util.Optional;

@RestController
@RequestMapping("/booking")
public class BookingController {
    private static final Logger logger = LoggerFactory.getLogger(BookingController.class);

    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private ServiceRepository serviceRepository;
    @Autowired
    private BookingServiceRepository bookingServiceRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TestResultRepository testResultRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private ParticipantRepository participantRepository;
    @Autowired
    private TestSampleRepository testSampleRepository;
    
    @Autowired
    private SurchargeService surchargeService;
    
    @Autowired
    private BankInfoService bankInfoService;

    @Autowired
    private EmailService emailService;

    // Status flow for all bookings (both at-home and at-center)
    private static final List<String> STATUS_SEQUENCE = List.of(
            "Đã đặt", "Chưa thanh toán", "Chưa lấy mẫu", "Đã lấy mẫu", "Đã trả kết quả"
    );

    private boolean isValidStatusTransition(String current, String next, boolean isCenterCollected) {
        if ("Đã hủy".equals(next)) return true;
        int currentIdx = STATUS_SEQUENCE.indexOf(current);
        int nextIdx = STATUS_SEQUENCE.indexOf(next);
        // Only allow next status to be exactly one step ahead
        return currentIdx != -1 && nextIdx != -1 && nextIdx == currentIdx + 1;
    }

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
                if (serviceId == null) {
                    return ResponseEntity.badRequest().body("Service ID không hợp lệ: null");
                }
                Service service = serviceRepository.findById(serviceId).orElse(null);
                if (service == null) {
                    return ResponseEntity.badRequest().body("Dịch vụ không hợp lệ: " + serviceId);
                }
                validServices.add(service);
            }
            
            // Tạo booking
            Booking booking = new Booking();
            booking.setFullName(request.getFullName());
            booking.setEmail(request.getEmail());
            booking.setPhone(request.getPhone());
            booking.setIsAdministrative(request.getIsAdministrative());
            booking.setIsCenterCollected(request.getIsCenterCollected());
            booking.setAddress(request.getAddress());
            booking.setNote(request.getNote());
            
            // Set số người tham gia với giá trị mặc định là 2 nếu không được truyền
            booking.setNumParticipants(request.getNumParticipants() != null ? request.getNumParticipants() : 2);

            // Chỉ set date/time nếu có giá trị hợp lệ
            if (request.getCenterSampleDate() != null) {
                booking.setCenterSampleDate(request.getCenterSampleDate());
            }
            if (request.getCenterSampleTime() != null) {
                booking.setCenterSampleTime(request.getCenterSampleTime());
            }
            
            booking.setBookingDate(LocalDateTime.now());
            
            // Set trạng thái ban đầu dựa trên phương thức lấy mẫu
            if (request.getIsCenterCollected()) {
                // Tại trung tâm: trạng thái ban đầu là "Đã đặt"
                booking.setStatus("Đã đặt");
            } else {
                // Tại nhà: trạng thái ban đầu là "Chưa thanh toán"
                booking.setStatus("Chưa thanh toán");
            }
            
            // Không set kitStatus ban đầu, sẽ set khi staff xác nhận

            // Nếu đã đăng nhập, set user cho booking
            if (sessionUser != null) {
                booking.setCustomer(sessionUser);
            }

            // Lưu booking trước
            Booking savedBooking = bookingRepository.save(booking);

            // Tạo BookingService entities
            List<BookingService> bookingServices = new ArrayList<>();
            List<Integer> sampleCounts = request.getSampleCounts();
            for (int i = 0; i < validServices.size(); i++) {
                Service service = validServices.get(i);
                BookingService bs = new BookingService();
                bs.setBooking(savedBooking);
                bs.setService(service);
                bs.setKitType(null);
                // Nếu có sampleCounts và hợp lệ, set sampleQuantity, mặc định là 2
                int sampleQuantity = 2;
                if (sampleCounts != null && sampleCounts.size() > i && sampleCounts.get(i) != null && sampleCounts.get(i) >= 2) {
                    sampleQuantity = sampleCounts.get(i);
                }
                bs.setSampleQuantity(sampleQuantity);
                bookingServices.add(bs);
            }
            
            // Lưu BookingService entities
            if (!bookingServices.isEmpty()) {
                bookingServiceRepository.saveAll(bookingServices);
            }
            // Tính tổng tiền booking (bao gồm phụ phí)
            java.math.BigDecimal serviceAmount = bookingServiceRepository.getTotalBookingAmountWithExtraFees(savedBooking.getId());
            java.math.BigDecimal surchargeAmount = surchargeService.calculateSurchargeForBooking(savedBooking);
            java.math.BigDecimal totalAmount = serviceAmount.add(surchargeAmount);
            java.math.BigDecimal depositAmount = totalAmount.multiply(java.math.BigDecimal.valueOf(0.1)).setScale(0, java.math.RoundingMode.UP);
            // Gửi email xác nhận đặt lịch
            try {
                if (savedBooking.getEmail() != null && !savedBooking.getEmail().isBlank()) {
                    String subject = "Xác nhận đặt lịch xét nghiệm ADN";
                    StringBuilder text = new StringBuilder();
                    text.append("Chào ").append(savedBooking.getFullName()).append(",\n\n");
                    text.append("Bạn đã đặt lịch xét nghiệm thành công. Thông tin lịch hẹn:\n");
                    text.append("- Mã lịch hẹn: ").append(savedBooking.getId()).append("\n");
                    text.append("- Ngày đặt: ").append(savedBooking.getBookingDate().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n");
                    text.append("- Trạng thái: ").append(savedBooking.getStatus()).append("\n");
                    text.append("- Phương thức lấy mẫu: ").append(savedBooking.getIsCenterCollected() ? "Tại trung tâm" : "Tại nhà").append("\n");
                    if (!savedBooking.getIsCenterCollected()) {
                        text.append("\nLưu ý: Bạn đã chọn lấy mẫu tại nhà. Vui lòng đăng nhập vào hệ thống và chọn phương thức thanh toán cho lịch hẹn này.\n");
                        text.append("Nếu chọn chuyển khoản, bạn cần chuyển ít nhất 10% tổng phí (tối thiểu: ")
                            .append(java.text.NumberFormat.getInstance(new java.util.Locale("vi", "VN")).format(depositAmount)).append(" VNĐ")
                            .append(") để được gửi kit lấy mẫu.\n");
                        text.append("Nếu sau 7 ngày chưa thực hiện thanh toán khoản phí này, chúng tôi sẽ hủy lịch hẹn của bạn.\n");
                    }
                    text.append("\nBạn có thể xem chi tiết và quản lý lịch hẹn tại đây: ");
                    text.append("http://localhost:8080/appoinments-list\n");
                    text.append("\nCảm ơn bạn đã sử dụng dịch vụ của chúng tôi!");
                    emailService.sendSimpleEmail(savedBooking.getEmail(), subject, text.toString());
                }
            } catch (Exception e) {
                // Không throw lỗi email để không ảnh hưởng đến việc đặt lịch
                logger.warn("Không thể gửi email xác nhận đặt lịch: " + e.getMessage());
            }
            return ResponseEntity.ok().body("Đặt lịch thành công!");
        } catch (Exception e) {
            logger.error("Error while creating booking: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Đặt lịch thất bại: " + e.getMessage());
        }
    }

    @GetMapping("/appointments")
    public ResponseEntity<?> getAppointmentsByUserIdV2(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String date,
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
                pageable
            );

            List<Map<String, Object>> appointmentsList = new ArrayList<>();
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            for (Booking booking : bookingPage.getContent()) {
                Map<String, Object> appointmentDetails = new HashMap<>();
                appointmentDetails.put("id", booking.getId());
                appointmentDetails.put("customerName", booking.getFullName());
                appointmentDetails.put("method", booking.getIsCenterCollected() ? "Tại trung tâm" : "Tại nhà");
                appointmentDetails.put("bookingDate", booking.getBookingDate().format(dateTimeFormatter));
                // Thêm testDate cho filter (sử dụng bookingDate làm testDate)
                appointmentDetails.put("testDate", booking.getBookingDate().format(dateTimeFormatter));
                appointmentDetails.put("status", booking.getStatus());
                appointmentDetails.put("statusDisplay", mapStatusForCustomer(booking.getStatus()));
                
                // Thêm thông tin ADNService
                String ADNService = booking.getIsAdministrative() ? "Hành chính" : "Dân sự";
                appointmentDetails.put("ADNService", ADNService);
                
                // Thêm thông tin services cho feedback
                List<BookingService> bookingServices = bookingServiceRepository.findByBookingId(booking.getId());
                List<Map<String, Object>> services = new ArrayList<>();
                for (BookingService bs : bookingServices) {
                    Service service = bs.getService();
                    Map<String, Object> serviceDetail = new HashMap<>();
                    serviceDetail.put("service", service.getName());
                    serviceDetail.put("serviceId", service.getId());
                    serviceDetail.put("price", service.getPrice());
                    serviceDetail.put("sampleQuantity", bs.getSampleQuantity());
                    services.add(serviceDetail);
                }
                appointmentDetails.put("services", services);
                
                // Thêm thông tin staff nếu có
                if (booking.getStaff() != null) {
                    appointmentDetails.put("staff_id", booking.getStaff().getId());
                    appointmentDetails.put("staff_name", booking.getStaff().getFullName());
                }
                
                // --- Bổ sung tính tổng tiền, đã thanh toán, còn lại ---
                java.math.BigDecimal totalServiceAmount = java.math.BigDecimal.ZERO;
                for (BookingService bs : bookingServices) {
                    Service service = bs.getService();
                    java.math.BigDecimal extraSampleFee = service.getExtraPerSampleFee() != null ? service.getExtraPerSampleFee() : java.math.BigDecimal.ZERO;
                    int extraSamples = Math.max(0, bs.getSampleQuantity() - 2);
                    java.math.BigDecimal extraSampleCost = extraSampleFee.multiply(java.math.BigDecimal.valueOf(extraSamples));
                    java.math.BigDecimal serviceTotal = service.getPrice().add(extraSampleCost);
                    totalServiceAmount = totalServiceAmount.add(serviceTotal);
                }
                java.math.BigDecimal totalSurchargeAmount = java.math.BigDecimal.ZERO;
                List<Surcharge> activeSurcharges = surchargeService.getActiveSurcharges();
                for (Surcharge surcharge : activeSurcharges) {
                    if (isSurchargeApplicableForBooking(surcharge, booking)) {
                        java.math.BigDecimal surchargeAmount = calculateSurchargeAmount(surcharge, booking);
                        if (surchargeAmount.compareTo(java.math.BigDecimal.ZERO) > 0) {
                            totalSurchargeAmount = totalSurchargeAmount.add(surchargeAmount);
                        }
                    }
                }
                java.math.BigDecimal totalAmount = totalServiceAmount.add(totalSurchargeAmount);
                java.math.BigDecimal paidAmount = paymentRepository.findByBooking_Id(booking.getId())
                    .stream()
                    .map(Payment::getAmount)
                    .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
                java.math.BigDecimal remainingAmount = totalAmount.subtract(paidAmount);
                appointmentDetails.put("totalAmount", totalAmount);
                appointmentDetails.put("paidAmount", paidAmount);
                appointmentDetails.put("remainingAmount", remainingAmount);
                // --- End bổ sung ---
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
            logger.error("Error while fetching appointments: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi lấy danh sách lịch hẹn: " + e.getMessage());
        }
    }

    @GetMapping("/history")
    public ResponseEntity<?> getTestHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String date,
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
            // Chỉ lấy các booking có status "Đã trả kết quả"
            Page<Booking> bookingPage = bookingRepository.findByUserIdAndFilters(
                userId,
                "Đã trả kết quả",
                bookingDate,
                pageable
            );

            List<Map<String, Object>> appointmentsList = new ArrayList<>();
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            for (Booking booking : bookingPage.getContent()) {
                Map<String, Object> appointmentDetails = new HashMap<>();
                appointmentDetails.put("id", booking.getId());
                appointmentDetails.put("customerName", booking.getFullName());
                appointmentDetails.put("method", booking.getIsCenterCollected() ? "Tại trung tâm" : "Tại nhà");
                appointmentDetails.put("bookingDate", booking.getBookingDate().format(dateTimeFormatter));
                appointmentDetails.put("testDate", booking.getBookingDate().format(dateTimeFormatter));
                appointmentDetails.put("status", booking.getStatus());
                appointmentDetails.put("statusDisplay", mapStatusForCustomer(booking.getStatus()));
                
                // Thêm thông tin ADNService
                String ADNService = booking.getIsAdministrative() ? "Hành chính" : "Dân sự";
                appointmentDetails.put("ADNService", ADNService);
                
                // Thêm thông tin services cho feedback
                List<BookingService> bookingServices = bookingServiceRepository.findByBookingId(booking.getId());
                List<Map<String, Object>> services = new ArrayList<>();
                for (BookingService bs : bookingServices) {
                    Service service = bs.getService();
                    Map<String, Object> serviceDetail = new HashMap<>();
                    serviceDetail.put("service", service.getName());
                    serviceDetail.put("serviceId", service.getId());
                    serviceDetail.put("price", service.getPrice());
                    serviceDetail.put("sampleQuantity", bs.getSampleQuantity());
                    services.add(serviceDetail);
                }
                appointmentDetails.put("services", services);
                
                // Thêm thông tin staff nếu có
                if (booking.getStaff() != null) {
                    appointmentDetails.put("staff_id", booking.getStaff().getId());
                    appointmentDetails.put("staff_name", booking.getStaff().getFullName());
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
            logger.error("Error while fetching test history: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi lấy lịch sử xét nghiệm: " + e.getMessage());
        }
    }

    @PostMapping("/cancel")
    public ResponseEntity<?> cancelAppointment(
            @RequestParam Integer bookingId,
            @RequestParam String reason,
            @SessionAttribute(name = "user", required = false) User sessionUser,
            @SessionAttribute(name = "staff", required = false) User sessionStaff) {
        try {
            Booking booking = bookingRepository.findById(bookingId).orElse(null);

            if (booking == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy lịch hẹn");
            }

            // Nếu là staff, cho phép hủy bất kỳ lịch hẹn nào, bất kỳ trạng thái nào
            if (sessionStaff != null) {
                booking.setStatus("Đã hủy");
                String oldNote = booking.getNote();
                String newNote = (oldNote == null || oldNote.isBlank() ? "" : oldNote + "\n\n") + "Lý do hủy (staff): " + reason;
                booking.setNote(newNote);
                bookingRepository.save(booking);
                return ResponseEntity.ok("Hủy lịch hẹn thành công (staff)");
            }

            // Nếu là user, chỉ cho phép hủy lịch của mình và trạng thái hợp lệ
            if (sessionUser != null && booking.getCustomer() != null && booking.getCustomer().getId().equals(sessionUser.getId())) {
                if (!booking.getStatus().equals("Đã đặt") && !booking.getStatus().equals("Chưa thanh toán")) {
                    return ResponseEntity.badRequest().body("Không thể hủy lịch hẹn trong trạng thái hiện tại");
                }
                booking.setStatus("Đã hủy");
                String oldNote = booking.getNote();
                String newNote = (oldNote == null || oldNote.isBlank() ? "" : oldNote + "\n\n") + "Lý do hủy: " + reason;
                booking.setNote(newNote);
                bookingRepository.save(booking);
                return ResponseEntity.ok("Hủy lịch hẹn thành công");
            }

            // Không có quyền
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Bạn không có quyền hủy lịch hẹn này");
        } catch (Exception e) {
            logger.error("Error while canceling appointment: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi hủy lịch hẹn: " + e.getMessage());
        }
    }

    // Staff endpoints
    @GetMapping("/staff/appointments")
    public ResponseEntity<?> getAllAppointmentsForStaff(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String statuses, // truyền dạng: "Đã nhận mẫu,Đã hoàn thành"
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
            java.util.List<String> statusList = null;
            if (statuses != null && !statuses.isBlank()) {
                statusList = java.util.Arrays.asList(statuses.split(","));
            }
            Page<Booking> bookingPage = bookingRepository.findAllWithFilters(
                statusList,
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
                appointmentDetails.put("numParticipants", booking.getNumParticipants());
                appointmentDetails.put("paymentMethod", booking.getPaymentMethod());
                if (!booking.getIsCenterCollected()) {
                    appointmentDetails.put("kitProgressStatus", booking.getStatus());
                }
                
                // Lấy services để hiển thị chi tiết và tính tổng tiền
                List<BookingService> bookingServices = bookingServiceRepository.findByBookingId(booking.getId());
                List<Map<String, Object>> services = new ArrayList<>();
                BigDecimal totalServiceAmount = BigDecimal.ZERO;
                
                for (BookingService bs : bookingServices) {
                    Service service = bs.getService();
                    Map<String, Object> serviceDetail = new HashMap<>();
                    serviceDetail.put("service", service.getName());
                    serviceDetail.put("serviceId", service.getId());
                    serviceDetail.put("price", service.getPrice());
                    serviceDetail.put("sampleQuantity", bs.getSampleQuantity());
                    serviceDetail.put("extraPerSampleFee", service.getExtraPerSampleFee());
                    
                    // Tính phí mẫu thêm
                    BigDecimal extraSampleFee = service.getExtraPerSampleFee() != null ? service.getExtraPerSampleFee() : BigDecimal.ZERO;
                    int extraSamples = Math.max(0, bs.getSampleQuantity() - 2);
                    BigDecimal extraSampleCost = extraSampleFee.multiply(BigDecimal.valueOf(extraSamples));
                    serviceDetail.put("extraSampleCost", extraSampleCost);
                    serviceDetail.put("extraSamples", extraSamples);
                    
                    // Tổng phí cho service này
                    BigDecimal serviceTotal = service.getPrice().add(extraSampleCost);
                    serviceDetail.put("serviceTotal", serviceTotal);
                    totalServiceAmount = totalServiceAmount.add(serviceTotal);

                    services.add(serviceDetail);
                }
                appointmentDetails.put("services", services);
                
                // Tính toán phụ phí
                List<Map<String, Object>> surcharges = new ArrayList<>();
                BigDecimal totalSurchargeAmount = BigDecimal.ZERO;
                
                List<Surcharge> activeSurcharges = surchargeService.getActiveSurcharges();
                for (Surcharge surcharge : activeSurcharges) {
                    // Kiểm tra xem phụ phí có áp dụng cho booking này không
                    if (isSurchargeApplicableForBooking(surcharge, booking)) {
                        BigDecimal surchargeAmount = calculateSurchargeAmount(surcharge, booking);
                        if (surchargeAmount.compareTo(BigDecimal.ZERO) > 0) {
                            Map<String, Object> surchargeDetail = new HashMap<>();
                            surchargeDetail.put("name", surcharge.getName());
                            surchargeDetail.put("description", surcharge.getDescription());
                            surchargeDetail.put("feePerUnit", surcharge.getFeePerUnit());
                            surchargeDetail.put("unit", surcharge.getUnit());
                            surchargeDetail.put("totalAmount", surchargeAmount);
                            surcharges.add(surchargeDetail);
                            totalSurchargeAmount = totalSurchargeAmount.add(surchargeAmount);
                        }
                    }
                }
                
                appointmentDetails.put("surcharges", surcharges);
                appointmentDetails.put("totalServiceAmount", totalServiceAmount);
                appointmentDetails.put("totalSurchargeAmount", totalSurchargeAmount);
                
                // Tổng tiền cuối cùng
                BigDecimal totalAmount = totalServiceAmount.add(totalSurchargeAmount);

                // Lấy tổng số tiền đã thanh toán cho booking này
                BigDecimal paidAmount = paymentRepository.findByBooking_Id(booking.getId())
                    .stream()
                    .map(Payment::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

                // Chỉ tính toán thông tin thanh toán nếu không phải trạng thái "Đã đặt"
                if (!"Đã đặt".equals(booking.getStatus())) {
                    appointmentDetails.put("totalAmount", totalAmount);
                    appointmentDetails.put("paidAmount", paidAmount);
                    appointmentDetails.put("remainingAmount", totalAmount.subtract(paidAmount));
                } else {
                    appointmentDetails.put("totalAmount", null);
                    appointmentDetails.put("paidAmount", null);
                    appointmentDetails.put("remainingAmount", null);
                }
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
            logger.error("Error while fetching staff appointments: ", e);
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
        appointmentDetails.put("statusDisplay", mapStatusForCustomer(booking.getStatus()));
        appointmentDetails.put("numParticipants", booking.getNumParticipants());
        appointmentDetails.put("paymentMethod", booking.getPaymentMethod());
        if (!booking.getIsCenterCollected()) {
            appointmentDetails.put("kitStatus", booking.getKitStatus());
            appointmentDetails.put("kitStatusDisplay", mapKitStatusForCustomer(booking.getKitStatus()));
        }
        // Dịch vụ
        List<BookingService> bookingServices = bookingServiceRepository.findByBookingId(booking.getId());
        List<Map<String, Object>> services = new ArrayList<>();
        for (BookingService bs : bookingServices) {
            Service service = bs.getService();
            Map<String, Object> serviceDetail = new HashMap<>();
            serviceDetail.put("service", service.getName());
            serviceDetail.put("serviceId", service.getId());
            serviceDetail.put("price", service.getPrice());
            serviceDetail.put("sampleQuantity", bs.getSampleQuantity());
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
        String newStatus = (String) payload.get("status");
        String oldStatus = booking.getStatus();
        // Check kitStatus condition for at-home bookings (chỉ cho phép chuyển sang "Đã lấy mẫu" khi kit đã "Đã nhận mẫu")
        if (!booking.getIsCenterCollected() && "Đã lấy mẫu".equals(newStatus)) {
            if (!"Đã nhận mẫu".equals(booking.getKitStatus())) {
                return ResponseEntity.badRequest().body("Chỉ được chuyển sang Đã lấy mẫu khi kit đã ở trạng thái Đã nhận mẫu.");
            }
        }
        // Thêm kiểm tra cho tại trung tâm: chỉ cho phép lấy mẫu nếu ngày hiện tại >= ngày hẹn
        if (booking.getIsCenterCollected() && "Đã lấy mẫu".equals(newStatus)) {
            if (booking.getCenterSampleDate() == null) {
                return ResponseEntity.badRequest().body("Không có ngày hẹn lấy mẫu để xác nhận lấy mẫu.");
            }
            java.time.LocalDate today = java.time.LocalDate.now();
            if (today.isBefore(booking.getCenterSampleDate())) {
                return ResponseEntity.badRequest().body("Chỉ được lấy mẫu vào ngày hẹn hoặc sau đó.");
            }
        }
        if (newStatus != null && !newStatus.isBlank() && !newStatus.equals(oldStatus)) {
            if (!isValidStatusTransition(oldStatus, newStatus, booking.getIsCenterCollected())) {
                return ResponseEntity.badRequest().body("Chỉ được chuyển trạng thái theo thứ tự hợp lệ hoặc Đã hủy.");
            }
            booking.setStatus(newStatus);
        }
        // Update kitStatus if provided
        if (payload.containsKey("kitStatus")) {
            String newKitStatus = (String) payload.get("kitStatus");
            booking.setKitStatus(newKitStatus);
        }
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
        if ("Đã hủy".equals(newStatus) && payload.get("cancelReason") != null && !payload.get("cancelReason").toString().isBlank()) {
            String oldNote = booking.getNote();
            String newNote = (oldNote == null || oldNote.isBlank() ? "" : oldNote + "\n\n") + "Lý do hủy: " + payload.get("cancelReason");
            booking.setNote(newNote);
        }
        // Cập nhật số lượng người tham gia
        if (payload.containsKey("numParticipants")) {
            Object numParticipantsObj = payload.get("numParticipants");
            if (numParticipantsObj != null) {
                if (numParticipantsObj instanceof Integer) {
                    booking.setNumParticipants((Integer) numParticipantsObj);
                } else if (numParticipantsObj instanceof String) {
                    try {
                        booking.setNumParticipants(Integer.parseInt((String) numParticipantsObj));
                    } catch (NumberFormatException e) {
                        // Ignore invalid number format
                    }
                }
            }
        }

        // Cập nhật dịch vụ
        List<Map<String, Object>> services = (List<Map<String, Object>>) payload.get("services");
        if (services != null) {
            bookingServiceRepository.deleteAll(bookingServiceRepository.findByBookingId(booking.getId()));
            for (Map<String, Object> serviceObj : services) {
                Integer serviceId = (Integer) serviceObj.get("serviceId");
                Integer sampleQuantity = serviceObj.get("sampleQuantity") != null ? ((Number)serviceObj.get("sampleQuantity")).intValue() : 2;
                Service service = serviceRepository.findById(serviceId).orElse(null);
                if (service != null) {
                    BookingService bs = new BookingService();
                    bs.setBooking(booking);
                    bs.setService(service);
                    bs.setSampleQuantity(sampleQuantity);
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
        String oldStatus = booking.getStatus();
        
        // Tự động set kitStatus thành "Chưa chọn kit" khi staff xác nhận booking tại nhà
        if (!booking.getIsCenterCollected() && "Chưa lấy mẫu".equals(newStatus) && booking.getKitStatus() == null) {
            booking.setKitStatus("Chưa chọn kit");
        }
        
        
        // Check kitStatus condition for at-home bookings (chỉ cho phép chuyển sang "Đã lấy mẫu" khi kit đã "Đã nhận mẫu")
        if (!booking.getIsCenterCollected() && "Đã lấy mẫu".equals(newStatus)) {
            if (!"Đã nhận mẫu".equals(booking.getKitStatus())) {
                return ResponseEntity.badRequest().body("Chỉ được chuyển sang Đã lấy mẫu khi kit đã ở trạng thái Đã nhận mẫu.");
            }
        }
        // Thêm kiểm tra cho tại trung tâm: chỉ cho phép lấy mẫu nếu ngày hiện tại >= ngày hẹn
        if (booking.getIsCenterCollected() && "Đã lấy mẫu".equals(newStatus)) {
            if (booking.getCenterSampleDate() == null) {
                return ResponseEntity.badRequest().body("Không có ngày hẹn lấy mẫu để xác nhận lấy mẫu.");
            }
            java.time.LocalDate today = java.time.LocalDate.now();
            if (today.isBefore(booking.getCenterSampleDate())) {
                return ResponseEntity.badRequest().body("Chỉ được lấy mẫu vào ngày hẹn hoặc sau đó.");
            }
        }
        
        // Kiểm tra thanh toán: chỉ cho phép chuyển sang "Đã lấy mẫu" khi booking đã hoàn thành thanh toán
        if ("Đã lấy mẫu".equals(newStatus)) {
            // Tính tổng tiền dịch vụ (bao gồm phụ phí)
            BigDecimal serviceAmount = bookingServiceRepository.getTotalBookingAmountWithExtraFees(booking.getId());
            BigDecimal surchargeAmount = surchargeService.calculateSurchargeForBooking(booking);
            BigDecimal totalAmount = serviceAmount.add(surchargeAmount);
            
            // Tính tổng tiền đã thanh toán
            List<Payment> payments = paymentRepository.findByBooking_Id(booking.getId());
            BigDecimal paidAmount = payments.stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            // Kiểm tra nếu chưa thanh toán đủ
            if (paidAmount.compareTo(totalAmount) < 0) {
                BigDecimal shortfall = totalAmount.subtract(paidAmount);
                String errorMessage = String.format(
                    "Không thể chuyển sang 'Đã lấy mẫu' vì chưa thanh toán đủ. " +
                    "Tổng tiền: %s VNĐ, Đã thanh toán: %s VNĐ, Còn thiếu: %s VNĐ. " +
                    "Vui lòng hoàn thành thanh toán trước khi lấy mẫu.",
                    totalAmount.toString(), paidAmount.toString(), shortfall.toString()
                );
                return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body(errorMessage);
            }
        }
        
        if (!newStatus.equals(oldStatus)) {
            if (!isValidStatusTransition(oldStatus, newStatus, booking.getIsCenterCollected())) {
                return ResponseEntity.badRequest().body("Chỉ được chuyển trạng thái theo thứ tự hợp lệ hoặc Đã hủy.");
            }
            booking.setStatus(newStatus);
            // Nếu chuyển sang 'Đã trả kết quả', gửi email thông báo
            if ("Đã trả kết quả".equals(newStatus)) {
                try {
                    if (booking.getEmail() != null && !booking.getEmail().isBlank()) {
                        String subject = "Thông báo: Kết quả xét nghiệm ADN đã sẵn sàng";
                        StringBuilder text = new StringBuilder();
                        text.append("Chào ").append(booking.getFullName()).append(",\n\n");
                        text.append("Kết quả xét nghiệm ADN cho lịch hẹn (Mã: ").append(booking.getId()).append(") đã được trả trên hệ thống.\n");
                        text.append("Vui lòng đăng nhập để xem và tải về kết quả xét nghiệm của bạn.\n");
                        text.append("Bạn có thể xem kết quả tại: http://localhost:8080/historytest\n");
                        text.append("Nếu có thắc mắc, vui lòng liên hệ trung tâm để được hỗ trợ.\n");
                        text.append("Trân trọng!\nTrung tâm xét nghiệm ADN Genx");
                        emailService.sendSimpleEmail(booking.getEmail(), subject, text.toString());
                    }
                } catch (Exception e) {
                    // Không throw lỗi gửi mail, chỉ log
                    System.err.println("[BookingController] Lỗi gửi email thông báo trả kết quả: " + e.getMessage());
                }
            }
        }
        // Update kitStatus if provided
        if (payload.containsKey("kitStatus")) {
            String newKitStatus = (String) payload.get("kitStatus");
            booking.setKitStatus(newKitStatus);
        }
        bookingRepository.save(booking);
        return ResponseEntity.ok("Cập nhật trạng thái thành công");
    }

    @PutMapping("/appointments/{id}/payment-method")
    public ResponseEntity<?> updatePaymentMethod(@PathVariable Integer id, @RequestBody Map<String, Object> payload, @SessionAttribute(name = "user", required = false) User sessionUser) {
        try {
            if (sessionUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Vui lòng đăng nhập");
            }
            Booking booking = bookingRepository.findById(id).orElse(null);
            if (booking == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy lịch hẹn");
            }
            if (booking.getCustomer() == null || !booking.getCustomer().getId().equals(sessionUser.getId())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Bạn không có quyền cập nhật lịch hẹn này");
            }
            if (booking.getPaymentMethod() != null) {
                return ResponseEntity.badRequest().body("Phương thức thanh toán đã được chọn và không thể thay đổi");
            }
            String paymentMethod = (String) payload.get("paymentMethod");
            if (paymentMethod == null) {
                return ResponseEntity.badRequest().body("Vui lòng chọn phương thức thanh toán");
            }
            boolean isCenter = Boolean.TRUE.equals(booking.getIsCenterCollected());
            if (isCenter) {
                if (!paymentMethod.equals("CASH") && !paymentMethod.equals("BANK_TRANSFER")) {
                    return ResponseEntity.badRequest().body("Chỉ chấp nhận Tiền mặt hoặc Chuyển khoản cho lấy mẫu tại cơ sở");
                }
            } else {
                if (!paymentMethod.equals("COD") && !paymentMethod.equals("BANK_TRANSFER")) {
                    return ResponseEntity.badRequest().body("Chỉ chấp nhận COD hoặc Chuyển khoản cho lấy mẫu tại nhà");
                }
            }
            booking.setPaymentMethod(paymentMethod);
            // Nếu trạng thái hiện tại là 'Chưa thanh toán', chuyển sang 'Chưa lấy mẫu'
            if ("Chưa thanh toán".equals(booking.getStatus())) {
                booking.setStatus("Chưa lấy mẫu");
            }
            bookingRepository.save(booking);
            return ResponseEntity.ok().body("Cập nhật phương thức thanh toán thành công");
        } catch (Exception e) {
            logger.error("Error while updating payment method: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi cập nhật phương thức thanh toán: " + e.getMessage());
        }
    }

    @GetMapping("/staff/appointments-with-results")
    public ResponseEntity<?> getAppointmentsWithResults(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String statuses, // truyền dạng: "Đã nhận mẫu,Đã hoàn thành"
            @RequestParam(required = false) String date,
            @RequestParam(required = false) Integer serviceId,
            @RequestParam(required = false) String searchQuery,
            @SessionAttribute(name = "staff", required = false) User sessionStaff) {
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
            java.util.List<String> statusList = null;
            if (statuses != null && !statuses.isBlank()) {
                statusList = java.util.Arrays.asList(statuses.split(","));
            }
            Page<Booking> bookingPage = bookingRepository.findAllWithFilters(
                statusList,
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
                appointmentDetails.put("numParticipants", booking.getNumParticipants());
                appointmentDetails.put("paymentMethod", booking.getPaymentMethod());
                if (!booking.getIsCenterCollected()) {
                    appointmentDetails.put("kitProgressStatus", booking.getStatus());
                }
                
                // Tính toán progress và isMultiService ở backend
                List<BookingService> bookingServices = bookingServiceRepository.findByBookingId(booking.getId());
                int totalServices = bookingServices.size();
                int completedServices = 0;
                
                for (BookingService bs : bookingServices) {
                    Service service = bs.getService();
                    // Kiểm tra có TestResult cho booking + service này không
                    Optional<TestResult> testResultOpt = testResultRepository.findByBookingIdAndServiceId(booking.getId(), service.getId());
                    if (testResultOpt.isPresent() && testResultOpt.get().getResultFile() != null && !testResultOpt.get().getResultFile().isBlank()) {
                        completedServices++;
                    }
                }
                
                // Tính progress
                int progress = totalServices > 0 ? Math.round((completedServices * 100.0f) / totalServices) : 0;
                appointmentDetails.put("progress", progress);
                
                // Tính isMultiService
                boolean isMultiService = totalServices > 1;
                appointmentDetails.put("isMultiService", isMultiService);
                
                // KHÔNG trả về services, result, results để giảm dữ liệu truyền
                
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
            logger.error("Error while fetching appointments with results: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi lấy danh sách lịch hẹn: " + e.getMessage());
        }
    }

    // API để lấy chi tiết services và results cho một booking (lazy loading)
    @GetMapping("/staff/appointments/{id}/details")
    public ResponseEntity<?> getAppointmentDetails(@PathVariable Integer id, @SessionAttribute(name = "staff", required = false) User sessionStaff) {
        if (sessionStaff == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("ACCESS_DENIED: Bạn không có quyền truy cập trang này.");
        }
        
        Booking booking = bookingRepository.findById(id).orElse(null);
        if (booking == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy lịch hẹn");
        }
        
        try {
            List<BookingService> bookingServices = bookingServiceRepository.findByBookingId(booking.getId());
            List<Map<String, Object>> services = new ArrayList<>();
            
            for (BookingService bs : bookingServices) {
                Service service = bs.getService();
                Map<String, Object> serviceDetail = new HashMap<>();
                serviceDetail.put("service", service.getName());
                serviceDetail.put("serviceId", service.getId());
                serviceDetail.put("price", service.getPrice());
                
                // Lấy TestResult cho booking + service này
                Optional<TestResult> testResultOpt = testResultRepository.findByBookingIdAndServiceId(booking.getId(), service.getId());
                if (testResultOpt.isPresent() && testResultOpt.get().getResultFile() != null && !testResultOpt.get().getResultFile().isBlank()) {
                    serviceDetail.put("resultFile", testResultOpt.get().getResultFile());
                    serviceDetail.put("resultId", testResultOpt.get().getId());
                } else {
                    serviceDetail.put("resultFile", null);
                    serviceDetail.put("resultId", null);
                }
                services.add(serviceDetail);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("services", services);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error while fetching appointment details: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi lấy chi tiết lịch hẹn: " + e.getMessage());
        }
    }

    @GetMapping("/appointments/{id}/details")
    public ResponseEntity<?> getAppointmentDetailsForCustomer(@PathVariable Integer id, @SessionAttribute(name = "user", required = false) User sessionUser) {
        if (sessionUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("SESSION_EXPIRED: Phiên đăng nhập đã hết hạn, vui lòng đăng nhập lại.");
        }
        
        Booking booking = bookingRepository.findById(id).orElse(null);
        if (booking == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy lịch hẹn");
        }
        
        // Kiểm tra quyền truy cập
        if (booking.getCustomer() == null || !booking.getCustomer().getId().equals(sessionUser.getId())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Bạn không có quyền xem lịch hẹn này");
        }
        
        try {
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
            appointmentDetails.put("bookingDate", booking.getBookingDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

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
            appointmentDetails.put("statusDisplay", mapStatusForCustomer(booking.getStatus()));
            appointmentDetails.put("numParticipants", booking.getNumParticipants());
            appointmentDetails.put("paymentMethod", booking.getPaymentMethod());
            
            // Lấy services để hiển thị chi tiết và tính tổng tiền
            List<BookingService> bookingServices = bookingServiceRepository.findByBookingId(booking.getId());
            List<Map<String, Object>> services = new ArrayList<>();
            BigDecimal totalServiceAmount = BigDecimal.ZERO;
            
            for (BookingService bs : bookingServices) {
                Service service = bs.getService();
                Map<String, Object> serviceDetail = new HashMap<>();
                serviceDetail.put("service", service.getName());
                serviceDetail.put("serviceId", service.getId());
                serviceDetail.put("price", service.getPrice());
                serviceDetail.put("sampleQuantity", bs.getSampleQuantity());
                serviceDetail.put("extraPerSampleFee", service.getExtraPerSampleFee());
                
                // Tính phí mẫu thêm
                BigDecimal extraSampleFee = service.getExtraPerSampleFee() != null ? service.getExtraPerSampleFee() : BigDecimal.ZERO;
                int extraSamples = Math.max(0, bs.getSampleQuantity() - 2);
                BigDecimal extraSampleCost = extraSampleFee.multiply(BigDecimal.valueOf(extraSamples));
                serviceDetail.put("extraSampleCost", extraSampleCost);
                serviceDetail.put("extraSamples", extraSamples);
                
                // Tổng phí cho service này
                BigDecimal serviceTotal = service.getPrice().add(extraSampleCost);
                serviceDetail.put("serviceTotal", serviceTotal);
                totalServiceAmount = totalServiceAmount.add(serviceTotal);
                
                services.add(serviceDetail);
            }
            appointmentDetails.put("services", services);
            
            // Tính toán phụ phí
            List<Map<String, Object>> surcharges = new ArrayList<>();
            BigDecimal totalSurchargeAmount = BigDecimal.ZERO;
            
            List<Surcharge> activeSurcharges = surchargeService.getActiveSurcharges();
            for (Surcharge surcharge : activeSurcharges) {
                // Kiểm tra xem phụ phí có áp dụng cho booking này không
                if (isSurchargeApplicableForBooking(surcharge, booking)) {
                    BigDecimal surchargeAmount = calculateSurchargeAmount(surcharge, booking);
                    if (surchargeAmount.compareTo(BigDecimal.ZERO) > 0) {
                        Map<String, Object> surchargeDetail = new HashMap<>();
                        surchargeDetail.put("name", surcharge.getName());
                        surchargeDetail.put("description", surcharge.getDescription());
                        surchargeDetail.put("feePerUnit", surcharge.getFeePerUnit());
                        surchargeDetail.put("unit", surcharge.getUnit());
                        surchargeDetail.put("totalAmount", surchargeAmount);
                        surcharges.add(surchargeDetail);
                        totalSurchargeAmount = totalSurchargeAmount.add(surchargeAmount);
                    }
                }
            }
            
            appointmentDetails.put("surcharges", surcharges);
            appointmentDetails.put("totalServiceAmount", totalServiceAmount);
            appointmentDetails.put("totalSurchargeAmount", totalSurchargeAmount);
            
            // Tổng tiền cuối cùng
            BigDecimal totalAmount = totalServiceAmount.add(totalSurchargeAmount);

            // Lấy tổng số tiền đã thanh toán cho booking này
            BigDecimal paidAmount = paymentRepository.findByBooking_Id(booking.getId())
                .stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            // Chỉ tính toán thông tin thanh toán nếu không phải trạng thái "Đã đặt"
            if (!"Đã đặt".equals(booking.getStatus())) {
                appointmentDetails.put("totalAmount", totalAmount);
                appointmentDetails.put("paidAmount", paidAmount);
                appointmentDetails.put("remainingAmount", totalAmount.subtract(paidAmount));
            } else {
                appointmentDetails.put("totalAmount", null);
                appointmentDetails.put("paidAmount", null);
                appointmentDetails.put("remainingAmount", null);
            }

            return ResponseEntity.ok(appointmentDetails);
        } catch (Exception e) {
            logger.error("Error while fetching appointment details for customer: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi lấy chi tiết lịch hẹn: " + e.getMessage());
        }
    }

    @GetMapping("/{bookingId}/sample-collection-status")
    public ResponseEntity<?> getSampleCollectionStatus(@PathVariable Integer bookingId) {
        // Lấy booking
        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        if (bookingOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy booking");
        }
        Booking booking = bookingOpt.get();
        // Số người tham gia yêu cầu
        int numParticipantsRequired = booking.getNumParticipants() != null ? booking.getNumParticipants() : 0;
        // Số người tham gia thực tế
        int numParticipantsActual = participantRepository.findByBookingId(bookingId).size();
        // Lấy các dịch vụ của booking
        List<BookingService> bookingServices = bookingServiceRepository.findByBookingId(bookingId);
        List<Map<String, Object>> services = new java.util.ArrayList<>();
        for (BookingService bs : bookingServices) {
            int sampleQuantityRequired = bs.getSampleQuantity();
            int sampleQuantityActual = testSampleRepository.findByServiceId(bs.getService().getId()).size();
            Map<String, Object> serviceStatus = new java.util.HashMap<>();
            serviceStatus.put("serviceId", bs.getService().getId());
            serviceStatus.put("sampleQuantityRequired", sampleQuantityRequired);
            serviceStatus.put("sampleQuantityActual", sampleQuantityActual);
            services.add(serviceStatus);
        }
        Map<String, Object> result = new java.util.HashMap<>();
        result.put("numParticipantsRequired", numParticipantsRequired);
        result.put("numParticipantsActual", numParticipantsActual);
        result.put("services", services);
        return ResponseEntity.ok(result);
    }

    // Mapping status for customer
    private String mapStatusForCustomer(String status) {
        return switch (status) {
            case "Đã đặt" -> "Đã đặt";
            case "Chưa thanh toán" -> "Chưa thanh toán";
            case "Chưa lấy mẫu" -> "Chưa lấy mẫu";
            case "Đã lấy mẫu" -> "Đã cấp mẫu";
            case "Đã trả kết quả" -> "Đã nhận kết quả";
            case "Đã hủy" -> "Đã hủy";
            default -> status;
        };
    }
    // Mapping kitStatus for customer
    private String mapKitStatusForCustomer(String kitStatus) {
        return switch (kitStatus) {
            case "Đã gửi" -> "Chưa nhận kit";
            case "Đã giao thành công" -> "Đã nhận kit";
            case "Chưa nhận mẫu" -> "Đã trả kit";
            case "Đã nhận mẫu" -> "Hoàn thành lấy mẫu";
            case "Lỗi mẫu" -> "Lỗi mẫu";
            default -> "";
        };
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
    private BigDecimal calculateSurchargeAmount(Surcharge surcharge, Booking booking) {
        switch (surcharge.getUnit()) {
            case "participant":
                // Tính theo số người tham gia
                return surcharge.getFeePerUnit().multiply(BigDecimal.valueOf(booking.getNumParticipants()));

            case "booking":
                // Tính theo booking (1 lần)
                return surcharge.getFeePerUnit();
                
            case "sample":
                // Tính theo tổng số mẫu (cần tính từ BookingService)
                // Sẽ được tính riêng trong BookingServiceRepository
                return BigDecimal.ZERO;
                
            default:
                return BigDecimal.ZERO;
        }
    }
    
    /**
     * Lấy thông tin ngân hàng để hiển thị trong modal thanh toán
     */
    @GetMapping("/bank-info")
    public ResponseEntity<?> getBankInfo() {
        try {
            var bankInfoOpt = bankInfoService.getFirstActiveBankInfo();
            if (bankInfoOpt.isPresent()) {
                var bankInfo = bankInfoOpt.get();
                Map<String, Object> response = new HashMap<>();
                response.put("bankName", bankInfo.getBankName());
                response.put("accountNumber", bankInfo.getAccountNumber());
                response.put("accountHolder", bankInfo.getAccountHolder());
                response.put("swiftCode", bankInfo.getSwiftCode());
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy thông tin ngân hàng");
            }
        } catch (Exception e) {
            logger.error("Error while fetching bank info: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi lấy thông tin ngân hàng: " + e.getMessage());
        }
    }
}
