package com.genx.adnmanagement.service;

import com.genx.adnmanagement.entity.BankInfo;
import com.genx.adnmanagement.repository.BankInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BankInfoService {
    
    @Autowired
    private BankInfoRepository bankInfoRepository;
    
    /**
     * Lấy tất cả thông tin ngân hàng đang active
     */
    public List<BankInfo> getActiveBankInfo() {
        return bankInfoRepository.findByIsActiveTrue();
    }
    
    /**
     * Lấy thông tin ngân hàng đầu tiên đang active
     */
    public Optional<BankInfo> getFirstActiveBankInfo() {
        return bankInfoRepository.findFirstByIsActiveTrue();
    }
    
    /**
     * Lấy thông tin ngân hàng theo ID
     */
    public Optional<BankInfo> getBankInfoById(Integer id) {
        return bankInfoRepository.findById(id);
    }
    
    /**
     * Lưu thông tin ngân hàng
     */
    public BankInfo saveBankInfo(BankInfo bankInfo) {
        return bankInfoRepository.save(bankInfo);
    }
    
    /**
     * Cập nhật thông tin ngân hàng
     */
    public BankInfo updateBankInfo(BankInfo bankInfo) {
        return bankInfoRepository.save(bankInfo);
    }
    
    /**
     * Xóa thông tin ngân hàng (soft delete)
     */
    public void deactivateBankInfo(Integer id) {
        Optional<BankInfo> bankInfoOpt = bankInfoRepository.findById(id);
        if (bankInfoOpt.isPresent()) {
            BankInfo bankInfo = bankInfoOpt.get();
            bankInfo.setIsActive(false);
            bankInfoRepository.save(bankInfo);
        }
    }
} 