package com.genx.adnmanagement.repository;

import com.genx.adnmanagement.entity.Surcharge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SurchargeRepository extends JpaRepository<Surcharge, Integer> {
    
    // Lấy tất cả surcharge đang active
    List<Surcharge> findByIsActiveTrue();
    
    // Lấy surcharge theo unit và active
    List<Surcharge> findByUnitAndIsActiveTrue(String unit);
    
    // Lấy surcharge theo tên
    Surcharge findByName(String name);
    
    // Lấy surcharge theo tên và active
    Surcharge findByNameAndIsActiveTrue(String name);
} 