package com.genx.adnmanagement.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bank_info")
public class BankInfo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "bank_name", nullable = false)
    private String bankName;
    
    @Column(name = "account_number", nullable = false)
    private String accountNumber;
    
    @Column(name = "account_holder", nullable = false)
    private String accountHolder;
    
    @Column(name = "swift_code")
    private String swiftCode;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructors
    public BankInfo() {}
    
    public BankInfo(String bankName, String accountNumber, String accountHolder, String swiftCode) {
        this.bankName = bankName;
        this.accountNumber = accountNumber;
        this.accountHolder = accountHolder;
        this.swiftCode = swiftCode;
    }
    
    // Getters and Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getBankName() {
        return bankName;
    }
    
    public void setBankName(String bankName) {
        this.bankName = bankName;
    }
    
    public String getAccountNumber() {
        return accountNumber;
    }
    
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
    
    public String getAccountHolder() {
        return accountHolder;
    }
    
    public void setAccountHolder(String accountHolder) {
        this.accountHolder = accountHolder;
    }
    
    public String getSwiftCode() {
        return swiftCode;
    }
    
    public void setSwiftCode(String swiftCode) {
        this.swiftCode = swiftCode;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
} 