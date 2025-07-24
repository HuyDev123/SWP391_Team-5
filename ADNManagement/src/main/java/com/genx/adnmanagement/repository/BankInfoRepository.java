package com.genx.adnmanagement.repository;

import com.genx.adnmanagement.entity.BankInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BankInfoRepository extends JpaRepository<BankInfo, Integer> {
    
    /**
     * Lấy thông tin ngân hàng đang active
     */
    List<BankInfo> findByIsActiveTrue();
    
    /**
     * Lấy thông tin ngân hàng đầu tiên đang active
     */
    Optional<BankInfo> findFirstByIsActiveTrue();
    
    /**
     * Lấy thông tin ngân hàng theo tên ngân hàng
     */
    List<BankInfo> findByBankNameContainingIgnoreCaseAndIsActiveTrue(String bankName);
} 