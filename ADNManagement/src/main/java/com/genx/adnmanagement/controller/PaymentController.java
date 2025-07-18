package com.genx.adnmanagement.controller;

import com.genx.adnmanagement.entity.Booking;
import com.genx.adnmanagement.entity.Payment;
import com.genx.adnmanagement.entity.User;
import com.genx.adnmanagement.repository.BookingRepository;
import com.genx.adnmanagement.repository.PaymentRepository;
import com.genx.adnmanagement.repository.BookingServiceRepository;
import com.genx.adnmanagement.service.SurchargeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private BookingServiceRepository bookingServiceRepository;
    
    @Autowired
    private SurchargeService surchargeService;

    // Thư mục lưu ảnh biên lai
    private static final String UPLOAD_DIR = "uploads/receipts/";

    @PostMapping("/confirm")
    public ResponseEntity<?> confirmPayment(@RequestBody Map<String, Object> request,
                                          @SessionAttribute(name = "user", required = false) User sessionUser,
                                          @SessionAttribute(name = "staff", required = false) User sessionStaff) {
        try {
            Integer bookingId = (Integer) request.get("bookingId");
            String paymentMethod = (String) request.get("paymentMethod");
            String receiptImage = (String) request.get("receiptImage"); // Đường dẫn ảnh biên lai

            if (bookingId == null) {
                return ResponseEntity.badRequest().body("Thông tin thanh toán không hợp lệ");
            }

            // Kiểm tra booking tồn tại
            Booking booking = bookingRepository.findById(bookingId).orElse(null);
            if (booking == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy lịch hẹn");
            }

            // Kiểm tra quyền thanh toán
            if (sessionUser != null) {
                // User chỉ có thể thanh toán cho booking của mình
                if (booking.getCustomer() == null || !booking.getCustomer().getId().equals(sessionUser.getId())) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Bạn không có quyền thanh toán cho lịch hẹn này");
                }
            } else if (sessionStaff != null) {
                // Staff có thể thanh toán cho bất kỳ booking nào
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Vui lòng đăng nhập để thực hiện thanh toán");
            }

            // Tạo payment record (số tiền sẽ được nhân viên nhập sau)
            Payment payment = new Payment();
            payment.setBooking(booking);
            payment.setAmount(BigDecimal.ZERO); // Tạm thời set 0, nhân viên sẽ cập nhật sau
            payment.setPaymentMethod(paymentMethod);
            payment.setCreatedAt(LocalDateTime.now());
            payment.setReceiptImage(receiptImage);

            // Set staff nếu là staff thực hiện thanh toán
            if (sessionStaff != null) {
                payment.setStaff(sessionStaff);
            }

            String note = (String) request.get("note");
            payment.setNote(note);

            // Lưu payment
            Payment savedPayment = paymentRepository.save(payment);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Đã gửi yêu cầu thanh toán thành công");
            response.put("paymentId", savedPayment.getId());
            response.put("paymentMethod", savedPayment.getPaymentMethod());
            response.put("createdAt", savedPayment.getCreatedAt());
            response.put("note", "Nhân viên sẽ xác nhận số tiền và hoàn tất thanh toán");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi xử lý thanh toán: " + e.getMessage());
        }
    }

    @PostMapping("/upload-receipt")
    public ResponseEntity<?> uploadReceipt(@RequestParam("receipt") MultipartFile file,
                                         @RequestParam("bookingId") Integer bookingId,
                                         @SessionAttribute(name = "user", required = false) User sessionUser,
                                         @SessionAttribute(name = "staff", required = false) User sessionStaff) {
        try {
            // Kiểm tra quyền
            if (sessionUser == null && sessionStaff == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Vui lòng đăng nhập");
            }

            // Kiểm tra file
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("File không được để trống");
            }

            // Kiểm tra định dạng file
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest().body("Chỉ chấp nhận file ảnh");
            }

            // Tạo thư mục nếu chưa tồn tại
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Tạo tên file unique
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename != null ? 
                originalFilename.substring(originalFilename.lastIndexOf(".")) : ".jpg";
            String filename = "receipt_" + bookingId + "_" + UUID.randomUUID().toString() + fileExtension;

            // Lưu file
            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Upload ảnh biên lai thành công");
            response.put("imagePath", UPLOAD_DIR + filename);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi upload ảnh: " + e.getMessage());
        }
    }

    @GetMapping("/status/{bookingId}")
    public ResponseEntity<?> getPaymentStatus(@PathVariable Integer bookingId,
                                            @SessionAttribute(name = "user", required = false) User sessionUser,
                                            @SessionAttribute(name = "staff", required = false) User sessionStaff) {
        try {
            // Kiểm tra booking tồn tại
            Booking booking = bookingRepository.findById(bookingId).orElse(null);
            if (booking == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy lịch hẹn");
            }

            // Kiểm tra quyền xem thông tin thanh toán
            if (sessionUser != null) {
                if (booking.getCustomer() == null || !booking.getCustomer().getId().equals(sessionUser.getId())) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Bạn không có quyền xem thông tin thanh toán này");
                }
            } else if (sessionStaff == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Vui lòng đăng nhập để xem thông tin thanh toán");
            }

            // Lấy danh sách thanh toán
            List<Payment> payments = paymentRepository.findByBooking_Id(bookingId);
            
            double totalPaid = payments.stream()
                    .mapToDouble(payment -> payment.getAmount().doubleValue())
                    .sum();

            Map<String, Object> response = new HashMap<>();
            response.put("totalPaid", totalPaid);
            response.put("payments", payments);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi lấy thông tin thanh toán: " + e.getMessage());
        }
    }

    @GetMapping("/check-deposit/{bookingId}")
    public ResponseEntity<?> checkDepositPayment(@PathVariable Integer bookingId,
                                               @SessionAttribute(name = "user", required = false) User sessionUser,
                                               @SessionAttribute(name = "staff", required = false) User sessionStaff) {
        try {
            // Kiểm tra booking tồn tại
            Booking booking = bookingRepository.findById(bookingId).orElse(null);
            if (booking == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy lịch hẹn");
            }

            // Kiểm tra quyền
            if (sessionUser != null) {
                if (booking.getCustomer() == null || !booking.getCustomer().getId().equals(sessionUser.getId())) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Bạn không có quyền xem thông tin thanh toán này");
                }
            } else if (sessionStaff == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Vui lòng đăng nhập để xem thông tin thanh toán");
            }

            // Tính tổng tiền đã thanh toán
            double totalPaid = paymentRepository.findByBooking_Id(bookingId)
                    .stream()
                    .mapToDouble(payment -> payment.getAmount().doubleValue())
                    .sum();

            // Tính tổng tiền booking từ BookingService
            double totalAmount = bookingServiceRepository.getTotalBookingAmount(bookingId).doubleValue();

            // Kiểm tra đã thanh toán đặt cọc (ví dụ: 30% tổng tiền)
            double depositRequired = totalAmount * 0.3;
            boolean hasDeposit = totalPaid >= depositRequired;

            Map<String, Object> response = new HashMap<>();
            response.put("hasDeposit", hasDeposit);
            response.put("totalPaid", totalPaid);
            response.put("depositRequired", depositRequired);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi kiểm tra thanh toán đặt cọc: " + e.getMessage());
        }
    }

    @GetMapping("/check-full-payment/{bookingId}")
    public ResponseEntity<?> checkFullPayment(@PathVariable Integer bookingId,
                                            @SessionAttribute(name = "user", required = false) User sessionUser,
                                            @SessionAttribute(name = "staff", required = false) User sessionStaff) {
        try {
            // Kiểm tra booking tồn tại
            Booking booking = bookingRepository.findById(bookingId).orElse(null);
            if (booking == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy lịch hẹn");
            }

            // Kiểm tra quyền
            if (sessionUser != null) {
                if (booking.getCustomer() == null || !booking.getCustomer().getId().equals(sessionUser.getId())) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Bạn không có quyền xem thông tin thanh toán này");
                }
            } else if (sessionStaff == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Vui lòng đăng nhập để xem thông tin thanh toán");
            }

            // Tính tổng tiền đã thanh toán
            double totalPaid = paymentRepository.findByBooking_Id(bookingId)
                    .stream()
                    .mapToDouble(payment -> payment.getAmount().doubleValue())
                    .sum();

            // Tính tổng tiền booking từ BookingService (bao gồm phí mẫu thêm và phụ phí)
            BigDecimal serviceAmount = bookingServiceRepository.getTotalBookingAmountWithExtraFees(bookingId);
            BigDecimal surchargeAmount = surchargeService.calculateSurchargeForBooking(booking);
            double totalAmount = serviceAmount.add(surchargeAmount).doubleValue();

            // Kiểm tra đã thanh toán đầy đủ
            boolean isFullyPaid = totalPaid >= totalAmount;

            Map<String, Object> response = new HashMap<>();
            response.put("isFullyPaid", isFullyPaid);
            response.put("totalPaid", totalPaid);
            response.put("totalAmount", totalAmount);
            response.put("remainingAmount", Math.max(0, totalAmount - totalPaid));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi kiểm tra thanh toán đầy đủ: " + e.getMessage());
        }
    }

    @PostMapping("/update-amount")
    public ResponseEntity<?> updatePaymentAmount(@RequestBody Map<String, Object> request,
                                               @SessionAttribute(name = "staff", required = false) User sessionStaff) {
        try {
            // Chỉ staff mới có quyền cập nhật số tiền thanh toán
            if (sessionStaff == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Chỉ nhân viên mới có quyền cập nhật số tiền thanh toán");
            }

            Integer paymentId = (Integer) request.get("paymentId");
            Object amountObj = request.get("amount");
            Double amount = null;
            if (amountObj instanceof Integer) {
                amount = ((Integer) amountObj).doubleValue();
            } else if (amountObj instanceof Double) {
                amount = (Double) amountObj;
            } else if (amountObj instanceof String) {
                try {
                    amount = Double.parseDouble((String) amountObj);
                } catch (NumberFormatException e) {
                    amount = null;
                }
            }

            if (paymentId == null || amount == null || amount <= 0) {
                return ResponseEntity.badRequest().body("Thông tin cập nhật không hợp lệ");
            }

            // Tìm payment record
            Payment payment = paymentRepository.findById(paymentId).orElse(null);
            if (payment == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy bản ghi thanh toán");
            }

            // Tính tổng tiền booking từ BookingService (bao gồm phí mẫu thêm và phụ phí)
            Booking booking = payment.getBooking();
            BigDecimal serviceAmount = bookingServiceRepository.getTotalBookingAmountWithExtraFees(booking.getId());
            BigDecimal surchargeAmount = surchargeService.calculateSurchargeForBooking(booking);
            double totalAmount = serviceAmount.add(surchargeAmount).doubleValue();

            // Tính tổng tiền đã thanh toán (trừ payment hiện tại)
            double totalPaid = paymentRepository.findByBooking_Id(payment.getBooking().getId())
                    .stream()
                    .filter(p -> !p.getId().equals(paymentId))
                    .mapToDouble(p -> p.getAmount().doubleValue())
                    .sum();

            double remainingAmount = totalAmount - totalPaid;

            if (amount > remainingAmount) {
                return ResponseEntity.badRequest().body("Số tiền thanh toán vượt quá số tiền còn lại");
            }

            // Cập nhật số tiền
            payment.setAmount(BigDecimal.valueOf(amount));
            payment.setStaff(sessionStaff); // Cập nhật staff xác nhận
            String note = (String) request.get("note");
            payment.setNote(note);
            Payment savedPayment = paymentRepository.save(payment);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Cập nhật số tiền thanh toán thành công");
            response.put("paymentId", savedPayment.getId());
            response.put("amount", savedPayment.getAmount());
            response.put("paymentMethod", savedPayment.getPaymentMethod());
            response.put("updatedAt", savedPayment.getCreatedAt());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi cập nhật số tiền thanh toán: " + e.getMessage());
        }
    }

    @GetMapping("/user")
    public ResponseEntity<?> getPaymentsByUser(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @SessionAttribute(name = "user", required = false) User sessionUser) {
        if (sessionUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Vui lòng đăng nhập để xem lịch sử thanh toán");
        }
        
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Payment> paymentPage = paymentRepository.findByUserIdWithPagination(sessionUser.getId(), pageable);
            
            List<Map<String, Object>> result = new java.util.ArrayList<>();
            for (Payment p : paymentPage.getContent()) {
                Map<String, Object> map = new java.util.HashMap<>();
                map.put("id", p.getId());
                map.put("createdAt", p.getCreatedAt());
                map.put("amount", p.getAmount());
                map.put("paymentMethod", p.getPaymentMethod());
                map.put("receiptImage", p.getReceiptImage());
                map.put("note", p.getNote());
                result.add(map);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("content", result);
            response.put("totalPages", paymentPage.getTotalPages());
            response.put("totalElements", paymentPage.getTotalElements());
            response.put("page", paymentPage.getNumber());
            response.put("size", paymentPage.getSize());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi lấy lịch sử thanh toán: " + e.getMessage());
        }
    }

    // Staff endpoints for payment management
    @GetMapping("/staff/pending")
    public ResponseEntity<?> getPendingPayments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @SessionAttribute(name = "staff", required = false) User sessionStaff) {
        if (sessionStaff == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Chỉ nhân viên mới có quyền truy cập");
        }
        
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Payment> paymentPage;
            
            if (search != null && !search.trim().isEmpty()) {
                paymentPage = paymentRepository.findPendingPaymentsWithSearch(search.trim(), pageable);
            } else {
                paymentPage = paymentRepository.findPendingPayments(pageable);
            }
            
            List<Map<String, Object>> result = new java.util.ArrayList<>();
            for (Payment p : paymentPage.getContent()) {
                Map<String, Object> map = new java.util.HashMap<>();
                map.put("id", p.getId());
                map.put("bookingId", p.getBooking().getId());
                map.put("customerName", p.getBooking().getFullName());
                map.put("requestDate", p.getCreatedAt());
                map.put("amount", p.getAmount());
                map.put("paymentMethod", p.getPaymentMethod());
                map.put("receiptImage", p.getReceiptImage());
                map.put("note", p.getNote());
                map.put("status", "PENDING");
                result.add(map);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("content", result);
            response.put("totalPages", paymentPage.getTotalPages());
            response.put("totalElements", paymentPage.getTotalElements());
            response.put("page", paymentPage.getNumber());
            response.put("size", paymentPage.getSize());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi lấy danh sách thanh toán chờ xử lý: " + e.getMessage());
        }
    }

    @GetMapping("/staff/history")
    public ResponseEntity<?> getPaymentHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String method,
            @SessionAttribute(name = "staff", required = false) User sessionStaff) {
        if (sessionStaff == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Chỉ nhân viên mới có quyền truy cập");
        }
        
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Payment> paymentPage;
            
            if (search != null && !search.trim().isEmpty()) {
                paymentPage = paymentRepository.findConfirmedPaymentsWithSearch(search.trim(), date, method, pageable);
            } else {
                paymentPage = paymentRepository.findConfirmedPayments(date, method, pageable);
            }
            
            List<Map<String, Object>> result = new java.util.ArrayList<>();
            for (Payment p : paymentPage.getContent()) {
                Map<String, Object> map = new java.util.HashMap<>();
                map.put("id", p.getId());
                map.put("bookingId", p.getBooking().getId());
                map.put("customerName", p.getBooking().getFullName());
                map.put("paymentDate", p.getCreatedAt());
                map.put("amount", p.getAmount());
                map.put("paymentMethod", p.getPaymentMethod());
                map.put("receiptImage", p.getReceiptImage());
                map.put("note", p.getNote());
                map.put("status", "CONFIRMED");
                map.put("staffName", p.getStaff() != null ? p.getStaff().getFullName() : "N/A");
                result.add(map);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("content", result);
            response.put("totalPages", paymentPage.getTotalPages());
            response.put("totalElements", paymentPage.getTotalElements());
            response.put("page", paymentPage.getNumber());
            response.put("size", paymentPage.getSize());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi lấy lịch sử thanh toán: " + e.getMessage());
        }
    }

    @PostMapping("/staff/confirm")
    public ResponseEntity<?> confirmPaymentByStaff(@RequestBody Map<String, Object> request,
                                                 @SessionAttribute(name = "staff", required = false) User sessionStaff) {
        if (sessionStaff == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Chỉ nhân viên mới có quyền xác nhận thanh toán");
        }
        
        try {
            Integer paymentId = (Integer) request.get("paymentId");
            Object actualAmountObj = request.get("actualAmount");
            Double actualAmount = null;
            if (actualAmountObj instanceof Integer) {
                actualAmount = ((Integer) actualAmountObj).doubleValue();
            } else if (actualAmountObj instanceof Double) {
                actualAmount = (Double) actualAmountObj;
            } else if (actualAmountObj instanceof String) {
                try {
                    actualAmount = Double.parseDouble((String) actualAmountObj);
                } catch (NumberFormatException e) {
                    actualAmount = null;
                }
            }
            String notes = (String) request.get("notes");
            
            if (paymentId == null || actualAmount == null || actualAmount <= 0) {
                return ResponseEntity.badRequest().body("Thông tin xác nhận không hợp lệ");
            }
            
            Payment payment = paymentRepository.findById(paymentId).orElse(null);
            if (payment == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy thanh toán");
            }
            
            // Cập nhật thông tin thanh toán
            payment.setAmount(BigDecimal.valueOf(actualAmount));
            payment.setStaff(sessionStaff);
            payment.setNote(notes);
            
            Payment savedPayment = paymentRepository.save(payment);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Xác nhận thanh toán thành công");
            response.put("paymentId", savedPayment.getId());
            response.put("amount", savedPayment.getAmount());
            response.put("paymentMethod", savedPayment.getPaymentMethod());
            response.put("confirmedAt", savedPayment.getCreatedAt());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi xác nhận thanh toán: " + e.getMessage());
        }
    }

    @PostMapping("/staff/create")
    public ResponseEntity<?> createPaymentByStaff(@RequestBody Map<String, Object> request,
                                                @SessionAttribute(name = "staff", required = false) User sessionStaff) {
        if (sessionStaff == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Chỉ nhân viên mới có quyền tạo thanh toán");
        }
        
        try {
            Integer bookingId = (Integer) request.get("bookingId");
            Object amountObj = request.get("amount");
            Double amount = null;
            if (amountObj instanceof Integer) {
                amount = ((Integer) amountObj).doubleValue();
            } else if (amountObj instanceof Double) {
                amount = (Double) amountObj;
            } else if (amountObj instanceof String) {
                try {
                    amount = Double.parseDouble((String) amountObj);
                } catch (NumberFormatException e) {
                    amount = null;
                }
            }
            String paymentMethod = (String) request.get("paymentMethod");
            String notes = (String) request.get("notes");
            
            if (bookingId == null || amount == null || amount <= 0 || paymentMethod == null) {
                return ResponseEntity.badRequest().body("Thông tin thanh toán không hợp lệ");
            }
            
            Booking booking = bookingRepository.findById(bookingId).orElse(null);
            if (booking == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy lịch hẹn");
            }
            
            // Tạo thanh toán mới
            Payment payment = new Payment();
            payment.setBooking(booking);
            payment.setAmount(BigDecimal.valueOf(amount));
            payment.setPaymentMethod(paymentMethod);
            payment.setStaff(sessionStaff);
            payment.setNote(notes);
            payment.setCreatedAt(LocalDateTime.now());
            
            Payment savedPayment = paymentRepository.save(payment);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Tạo thanh toán thành công");
            response.put("paymentId", savedPayment.getId());
            response.put("amount", savedPayment.getAmount());
            response.put("paymentMethod", savedPayment.getPaymentMethod());
            response.put("createdAt", savedPayment.getCreatedAt());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi tạo thanh toán: " + e.getMessage());
        }
    }

    @GetMapping("/staff/unpaid-bookings")
    public ResponseEntity<?> getUnpaidBookings(@SessionAttribute(name = "staff", required = false) User sessionStaff) {
        if (sessionStaff == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Chỉ nhân viên mới có quyền truy cập");
        }
        
        try {
            List<Map<String, Object>> result = new java.util.ArrayList<>();
            
            // Lấy các booking chưa có payment hoặc chưa thanh toán đủ
            List<Booking> bookings = bookingRepository.findBookingsWithoutFullPayment();
            
            for (Booking booking : bookings) {
                // Tính tổng tiền đã thanh toán
                double totalPaid = paymentRepository.findByBooking_Id(booking.getId())
                        .stream()
                        .mapToDouble(payment -> payment.getAmount().doubleValue())
                        .sum();
                
                // Tính tổng tiền booking (bao gồm phí mẫu thêm và phụ phí)
                BigDecimal serviceAmount = bookingServiceRepository.getTotalBookingAmountWithExtraFees(booking.getId());
                BigDecimal surchargeAmount = surchargeService.calculateSurchargeForBooking(booking);
                double totalAmount = serviceAmount.add(surchargeAmount).doubleValue();
                
                // Chỉ hiển thị những booking chưa thanh toán đủ
                if (totalPaid < totalAmount) {
                    Map<String, Object> map = new java.util.HashMap<>();
                    map.put("id", booking.getId());
                    map.put("customerName", booking.getFullName());
                    map.put("totalAmount", totalAmount);
                    map.put("totalPaid", totalPaid);
                    map.put("remainingAmount", totalAmount - totalPaid);
                    map.put("paymentMethod", booking.getPaymentMethod());
                    // Thêm trường method (phương thức lấy mẫu)
                    map.put("method", booking.getIsCenterCollected() ? "Tại trung tâm" : "Tại nhà");
                    result.add(map);
                }
            }
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi lấy danh sách booking chưa thanh toán: " + e.getMessage());
        }
    }
} 