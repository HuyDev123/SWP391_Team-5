package com.genx.adnmanagement.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "Surcharge")
public class Surcharge {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    @Column(name = "description", length = 255)
    private String description;
    
    @Column(name = "fee_per_unit", nullable = false, precision = 18, scale = 2)
    private BigDecimal feePerUnit;
    
    @Column(name = "unit", nullable = false, length = 50)
    private String unit;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;
    
    // Constructors
    public Surcharge() {}
    
    public Surcharge(String name, String description, BigDecimal feePerUnit, String unit, Boolean isActive) {
        this.name = name;
        this.description = description;
        this.feePerUnit = feePerUnit;
        this.unit = unit;
        this.isActive = isActive;
    }
    
    // Getters and Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public BigDecimal getFeePerUnit() {
        return feePerUnit;
    }
    
    public void setFeePerUnit(BigDecimal feePerUnit) {
        this.feePerUnit = feePerUnit;
    }
    
    public String getUnit() {
        return unit;
    }
    
    public void setUnit(String unit) {
        this.unit = unit;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    @Override
    public String toString() {
        return "Surcharge{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", feePerUnit=" + feePerUnit +
                ", unit='" + unit + '\'' +
                ", isActive=" + isActive +
                '}';
    }
} 