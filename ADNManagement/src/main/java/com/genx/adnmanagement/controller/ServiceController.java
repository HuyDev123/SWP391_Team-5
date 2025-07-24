package com.genx.adnmanagement.controller;

import com.genx.adnmanagement.entity.Service;
import com.genx.adnmanagement.repository.ServiceRepository;
import com.genx.adnmanagement.repository.SurchargeRepository;
import com.genx.adnmanagement.entity.Surcharge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/service")
public class ServiceController {
    @Autowired
    private ServiceRepository serviceRepository;
    @Autowired
    private SurchargeRepository surchargeRepository;

    // API để lấy danh sách services cho booking form
    @GetMapping("/list")
    public ResponseEntity<?> getServicesForBooking() {
        try {
            List<Service> services = serviceRepository.findByIsActiveTrue();
            List<Map<String, Object>> serviceList = new ArrayList<>();
            
            for (Service service : services) {
                Map<String, Object> serviceMap = new HashMap<>();
                serviceMap.put("id", service.getId());
                serviceMap.put("name", service.getName());
                serviceMap.put("description", service.getDescription());
                serviceMap.put("price", service.getPrice());
                // Thêm trường extra_per_sample để frontend lấy giá phụ phí
                serviceMap.put("extra_per_sample", service.getExtraPerSampleFee());
                // Map tên service với frontend values
                String frontendValue = mapServiceNameToFrontendValue(service.getName());
                serviceMap.put("frontendValue", frontendValue);
                serviceList.add(serviceMap);
            }
            
            return ResponseEntity.ok(serviceList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi lấy danh sách dịch vụ: " + e.getMessage());
        }
    }

    // API lấy phí hành chính theo participant
    @GetMapping("/surcharge/administrative")
    public ResponseEntity<?> getAdministrativeSurcharge() {
        // Lấy surcharge có unit = 'participant', is_active = true, name chứa 'hành chính'
        List<Surcharge> surcharges = surchargeRepository.findByUnitAndIsActiveTrue("participant");
        for (Surcharge surcharge : surcharges) {
            if (surcharge.getName() != null && surcharge.getName().toLowerCase().contains("hành chính")) {
                Map<String, Object> result = new HashMap<>();
                result.put("fee_per_unit", surcharge.getFeePerUnit());
                return ResponseEntity.ok(result);
            }
        }
        // Không tìm thấy thì trả về 0
        return ResponseEntity.ok(Map.of("fee_per_unit", 0));
    }

    // Map service name to frontend value
    private String mapServiceNameToFrontendValue(String serviceName) {
        if (serviceName == null) return "";
        
        if (serviceName.contains("Cha - Con") || serviceName.contains("Cha-Con")) {
            return "father-child";
        } else if (serviceName.contains("Mẹ - Con") || serviceName.contains("Mẹ-Con")) {
            return "mother-child";
        } else if (serviceName.contains("Anh/Chị - Em") || serviceName.contains("Anh/Chị-Em")) {
            return "siblings";
        } else if (serviceName.contains("Ông/Bà - Cháu") || serviceName.contains("Ông/Bà-Cháu")) {
            return "grandparent";
        }
        
        return serviceName.toLowerCase().replaceAll("[^a-z0-9]", "-");
    }
} 