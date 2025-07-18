package com.genx.adnmanagement.controller;

import com.genx.adnmanagement.entity.Service;
import com.genx.adnmanagement.repository.ServiceRepository;
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