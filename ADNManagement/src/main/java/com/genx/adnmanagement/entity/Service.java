package com.genx.adnmanagement.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.List;
import com.genx.adnmanagement.entity.ServiceKitType;

@Entity
@Table(name = "Service")
public class Service {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 255, nullable = false)
    private String name;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String description;

    @Column(precision = 18, scale = 2)
    private BigDecimal price;

    @Column(name = "extra_per_sample_fee", precision = 18, scale = 2)
    private BigDecimal extraPerSampleFee;

    @Column(name = "is_active")
    private Boolean isActive;

    @OneToMany(mappedBy = "service", cascade = CascadeType.ALL)
    private List<ServiceKitType> serviceKitTypes;

    // Getters and setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    public List<ServiceKitType> getServiceKitTypes() { return serviceKitTypes; }
    public void setServiceKitTypes(List<ServiceKitType> serviceKitTypes) { this.serviceKitTypes = serviceKitTypes; }
    public BigDecimal getExtraPerSampleFee() { return extraPerSampleFee; }
    public void setExtraPerSampleFee(BigDecimal extraPerSampleFee) { this.extraPerSampleFee = extraPerSampleFee; }
}

