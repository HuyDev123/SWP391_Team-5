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
                List<BookingService> bookingServices = bookingServiceRepository.findByBooking_Id(booking.getId());
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
            List<BookingService> bookingServices = bookingServiceRepository.findByBooking_Id(booking.getId());
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

            // Update status to "Chưa lấy mẫu" (tương ứng với "Đã nhận kit" trong mapping)
            booking.setKitStatus("Chưa lấy mẫu");
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

            // Check if current status is "Chưa lấy mẫu" (tương ứng với "Đã nhận kit" trong mapping)
            if (!"Chưa lấy mẫu".equals(booking.getKitStatus())) {
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

    // Test endpoint to debug booking data
    @GetMapping("/debug")
    public ResponseEntity<?> debugBookings(@SessionAttribute(name = "staff", required = false) User sessionStaff) {
        if (sessionStaff == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("ACCESS_DENIED: Bạn không có quyền truy cập trang này.");
        }

        try {
            List<Booking> allBookings = bookingRepository.findAll();
            List<Map<String, Object>> debugData = new ArrayList<>();
            
            for (Booking booking : allBookings) {
                Map<String, Object> bookingInfo = new HashMap<>();
                bookingInfo.put("id", booking.getId());
                bookingInfo.put("status", booking.getStatus());
                bookingInfo.put("kitStatus", booking.getKitStatus());
                bookingInfo.put("isCenterCollected", booking.getIsCenterCollected());
                bookingInfo.put("fullName", booking.getFullName());
                bookingInfo.put("bookingDate", booking.getBookingDate());
                debugData.add(bookingInfo);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("totalBookings", allBookings.size());
            response.put("bookings", debugData);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi debug: " + e.getMessage());
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
            
            System.out.println("Số lượng phần tử trên trang hiện tại: " + bookingPage.getContent().size());

            List<Map<String, Object>> kitsList = new ArrayList<>();
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            for (Booking booking : bookingPage.getContent()) {
                // Xử lý trường hợp kitStatus null (chưa được xác nhận)
                String kitStatus = booking.getKitStatus();
                if (kitStatus == null && "Đã đặt".equals(booking.getStatus())) {
                    kitStatus = "Chưa xác nhận";
                }

                // Lấy thông tin dịch vụ
                List<BookingService> bookingServices = bookingServiceRepository.findByBooking_Id(booking.getId());

                for (BookingService bs : bookingServices) {
                    Service service = bs.getService();
                    Map<String, Object> kitDetails = new HashMap<>();
                    kitDetails.put("appointmentId", booking.getId());
                    kitDetails.put("customerName", booking.getFullName());
                    kitDetails.put("address", booking.getAddress());
                    kitDetails.put("status", kitStatus);
                    kitDetails.put("serviceId", service.getId());
                    kitDetails.put("serviceName", service.getName());
                    kitDetails.put("kitType", bs.getKitType() != null ? bs.getKitType().getName() : null);
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

            List<BookingService> bookingServices = bookingServiceRepository.findByBooking_Id(appointmentId);
            List<Map<String, Object>> services = new ArrayList<>();

            for (BookingService bs : bookingServices) {
                Service service = bs.getService();
                Map<String, Object> serviceDetail = new HashMap<>();
                serviceDetail.put("serviceId", service.getId());
                serviceDetail.put("serviceName", service.getName());
                serviceDetail.put("customerName", booking.getFullName());
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
            for (Map<String, Object> selection : kitSelections) {
                Integer appointmentId = (Integer) selection.get("appointmentId");
                Integer serviceId = (Integer) selection.get("serviceId");
                String kitCode = (String) selection.get("kitCode");
                Integer kitTypeId = (Integer) selection.get("kitTypeId");
                String note = (String) selection.get("note");

                Booking booking = bookingRepository.findById(appointmentId).orElse(null);
                if (booking == null) continue;

                BookingService bookingService = bookingServiceRepository.findByBooking_IdAndService_Id(appointmentId, serviceId);
                if (bookingService == null) continue;

                KitType kitType = kitTypeRepository.findById(kitTypeId).orElse(null);
                if (kitType != null) {
                    bookingService.setKitType(kitType);
                }

                // bookingService.setKitCode(kitCode); // Tạm thời comment lại
                // booking.setKitNote(note); // Tạm thời comment lại
                booking.setKitStatus("Chưa gửi");

                bookingServiceRepository.save(bookingService);
                bookingRepository.save(booking);
            }

            return ResponseEntity.ok("Đã chọn kit thành công");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi chọn kit: " + e.getMessage());
        }
    }

    @PutMapping("/appointment/{appointmentId}/send")
    public ResponseEntity<?> sendKits(@PathVariable Integer appointmentId, @RequestBody Map<String, Object> payload) {
        try {
            Booking booking = bookingRepository.findById(appointmentId).orElse(null);
            if (booking == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy cuộc hẹn");
            }

            // Kiểm tra tất cả dịch vụ trong booking đã có kitType chưa
            List<BookingService> bookingServices = bookingServiceRepository.findByBooking_Id(appointmentId);
            for (BookingService bs : bookingServices) {
                if (bs.getKitType() == null) {
                    return ResponseEntity.badRequest().body("Vui lòng chọn kit cho tất cả dịch vụ trước khi gửi");
                }
            }

            // Kiểm tra trạng thái hiện tại có phải "Chưa gửi" không
            if (!"Chưa gửi".equals(booking.getKitStatus())) {
                return ResponseEntity.badRequest().body("Chỉ có thể gửi kit khi trạng thái là 'Chưa gửi'");
            }

            // String sendDate = (String) payload.get("sendDate");
            // if (sendDate != null) {
            //     booking.setKitSendDate(LocalDate.parse(sendDate));
            // }
            booking.setKitStatus("Đã gửi");
            bookingRepository.save(booking);

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

    // Mapping kitStatus for customer (same as BookingController)
    private String mapKitStatusForCustomer(String kitStatus) {
        return switch (kitStatus) {
            case "Đã gửi" -> "Chưa nhận kit";
            case "Chưa lấy mẫu" -> "Đã nhận kit";
            case "Chưa nhận mẫu" -> "Đã trả kit";
            case "Đã nhận mẫu" -> "Hoàn thành lấy mẫu";
            case "Lỗi mẫu" -> "Lỗi mẫu";
            default -> kitStatus;
        };
    }

    // Convert mapped status back to original status for filtering
    private String convertMappedStatusToOriginal(String mappedStatus) {
        return switch (mappedStatus) {
            case "Chưa nhận kit" -> "Đã gửi";
            case "Đã nhận kit" -> "Chưa lấy mẫu";
            case "Đã trả kit" -> "Chưa nhận mẫu";
            case "Hoàn thành lấy mẫu" -> "Đã nhận mẫu";
            case "Lỗi mẫu" -> "Lỗi mẫu";
            default -> mappedStatus;
        };
    }
}