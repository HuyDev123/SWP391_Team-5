package com.genx.adnmanagement.controller;

import com.genx.adnmanagement.entity.*;
import com.genx.adnmanagement.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;

import java.util.*;

@RestController
@RequestMapping("/kit-item")
public class KitItemController {
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private BookingServiceRepository bookingServiceRepository;
    @Autowired
    private ServiceRepository serviceRepository;
    @Autowired
    private KitItemRepository kitItemRepository;
    @Autowired
    private KitTypeRepository kitTypeRepository;
    @Autowired
    private ServiceKitTypeRepository serviceKitTypeRepository;

    // 1. Check booking existence and return services for dropdown
    @GetMapping("/booking-services/{bookingId}")
    public ResponseEntity<?> getBookingServices(@PathVariable Integer bookingId) {
        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        if (bookingOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy cuộc hẹn");
        }
        Booking booking = bookingOpt.get();
        if (Boolean.TRUE.equals(booking.getIsCenterCollected())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Chỉ hỗ trợ thêm kit cho cuộc hẹn phương thức tại nhà");
        }
        if ("Đã hủy".equalsIgnoreCase(booking.getStatus())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cuộc hẹn đã bị hủy, không thể thêm kit");
        }
        List<BookingService> services = bookingServiceRepository.findByBooking_Id(bookingId);
        List<Map<String, Object>> servicesList = new ArrayList<>();
        for (BookingService bs : services) {
            Service service = bs.getService();
            Map<String, Object> map = new HashMap<>();
            map.put("id", service.getId());
            map.put("name", service.getName());
            servicesList.add(map);
        }
        return ResponseEntity.ok(servicesList);
    }

    // 2. Add new KitItem (after booking and service are selected)
    @PostMapping("/add")
    public ResponseEntity<?> addKitItem(@RequestBody Map<String, Object> payload) {
        try {
            Integer bookingId = (Integer) payload.get("bookingId");
            Integer serviceId = (Integer) payload.get("serviceId");
            String kitCode = (String) payload.get("kitCode");
            Integer kitTypeId = (Integer) payload.get("kitTypeId");
            String note = (String) payload.get("note");

            if (bookingId == null || serviceId == null || kitCode == null || kitTypeId == null) {
                return ResponseEntity.badRequest().body("Thiếu thông tin bắt buộc");
            }
            // Validate booking-service
            Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
            if (bookingOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy cuộc hẹn");
            }
            List<BookingService> bsList = bookingServiceRepository.findByBooking_Id(bookingId);
            BookingService bookingService = null;
            for (BookingService bs : bsList) {
                if (bs.getService().getId().equals(serviceId)) {
                    bookingService = bs;
                    break;
                }
            }
            if (bookingService == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Dịch vụ không thuộc cuộc hẹn này");
            }
            // Validate kit type
            Optional<KitType> kitTypeOpt = kitTypeRepository.findById(kitTypeId);
            if (kitTypeOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Loại kit không hợp lệ");
            }
            // Check kitCode uniqueness
            if (kitItemRepository.findByKitCode(kitCode).isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("KitCode đã tồn tại");
            }
            // Create KitItem
            KitItem kitItem = new KitItem();
            kitItem.setBookingService(bookingService);
            kitItem.setKitCode(kitCode);
            kitItem.setKitType(kitTypeOpt.get());
            kitItem.setNote(note);
            kitItem.setDeliveryStatus("Chờ gửi");
            kitItemRepository.save(kitItem);
            return ResponseEntity.ok("Thêm kit thành công");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi: " + e.getMessage());
        }
    }

    // 3. Lấy danh sách kit type phù hợp với service
    @GetMapping("/kit-types/{serviceId}")
    public ResponseEntity<?> getKitTypesByService(@PathVariable Integer serviceId) {
        List<ServiceKitType> sktList = serviceKitTypeRepository.findByService_Id(serviceId);
        List<Map<String, Object>> kitTypes = new ArrayList<>();
        for (ServiceKitType skt : sktList) {
            KitType kt = skt.getKitType();
            Map<String, Object> map = new HashMap<>();
            map.put("id", kt.getId());
            map.put("name", kt.getName());
            kitTypes.add(map);
        }
        return ResponseEntity.ok(kitTypes);
    }

    // API phân trang + filter cho list kit
    @GetMapping("/list")
    public ResponseEntity<?> getKitList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status
    ) {
        Pageable pageable = PageRequest.of(page, size);
        // Lấy toàn bộ kit, sau đó filter bằng stream (có thể tối ưu bằng query nếu cần)
        Page<KitItem> kitPage = kitItemRepository.findAll(pageable);
        List<Map<String, Object>> kitDtos = new ArrayList<>();
        for (KitItem kit : kitPage.getContent()) {
            BookingService bs = kit.getBookingService();
            String appointmentId = bs != null && bs.getBooking() != null ? String.valueOf(bs.getBooking().getId()) : null;
            String serviceName = bs != null && bs.getService() != null ? bs.getService().getName() : null;
            String kitTypeName = kit.getKitType() != null ? kit.getKitType().getName() : null;
            // Filter theo search
            boolean match = true;
            if (search != null && !search.isBlank()) {
                String s = search.trim();
                // Lọc chính xác theo ID hoặc code
                match = (appointmentId != null && appointmentId.equals(s))
                        || (kit.getKitCode() != null && kit.getKitCode().equals(s));
            }
            // Filter theo status
            if (match && status != null && !status.isBlank()) {
                match = status.equals(kit.getDeliveryStatus());
            }
            if (!match) continue;
            Map<String, Object> dto = new HashMap<>();
            dto.put("appointmentId", appointmentId);
            dto.put("kitCode", kit.getKitCode());
            dto.put("service", serviceName);
            dto.put("kitType", kitTypeName);
            dto.put("status", kit.getDeliveryStatus());
            dto.put("note", kit.getNote());
            dto.put("sendDate", kit.getSendDate());
            dto.put("receiveDate", kit.getReceiveDate());
            kitDtos.add(dto);
        }
        // Tạo page mới với dữ liệu đã filter
        int start = Math.min(page * size, kitDtos.size());
        int end = Math.min(start + size, kitDtos.size());
        List<Map<String, Object>> pageContent = kitDtos.subList(start, end);
        Page<Map<String, Object>> resultPage = new PageImpl<>(pageContent, pageable, kitDtos.size());
        Map<String, Object> response = new HashMap<>();
        response.put("content", resultPage.getContent());
        response.put("totalPages", resultPage.getTotalPages());
        response.put("page", resultPage.getNumber());
        response.put("size", resultPage.getSize());
        response.put("totalElements", resultPage.getTotalElements());
        return ResponseEntity.ok(response);
    }

    // Update KitItem status by kitCode
    @PutMapping("/status/{kitCode}")
    public ResponseEntity<?> updateKitStatus(@PathVariable String kitCode, @RequestBody Map<String, Object> payload) {
        String newStatus = (String) payload.get("status");
        if (newStatus == null || newStatus.isBlank()) {
            return ResponseEntity.badRequest().body("Thiếu trạng thái mới");
        }
        Optional<KitItem> kitOpt = kitItemRepository.findByKitCode(kitCode);
        if (kitOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy kit với mã: " + kitCode);
        }
        KitItem kit = kitOpt.get();
        kit.setDeliveryStatus(newStatus);
        kitItemRepository.save(kit);
        return ResponseEntity.ok("Cập nhật trạng thái kit thành công");
    }
} 