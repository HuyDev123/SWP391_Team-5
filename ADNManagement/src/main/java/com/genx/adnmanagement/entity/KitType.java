package com.genx.adnmanagement.entity;

import jakarta.persistence.*;
import java.util.List;
import com.genx.adnmanagement.entity.ServiceKitType;

@Entity
@Table(name = "KitType")
public class KitType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 255, nullable = false)
    private String name;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String description;

    @OneToMany(mappedBy = "kitType", cascade = CascadeType.ALL)
    private List<ServiceKitType> serviceKitTypes;

    // Getters and setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public List<ServiceKitType> getServiceKitTypes() { return serviceKitTypes; }
    public void setServiceKitTypes(List<ServiceKitType> serviceKitTypes) { this.serviceKitTypes = serviceKitTypes; }
} 