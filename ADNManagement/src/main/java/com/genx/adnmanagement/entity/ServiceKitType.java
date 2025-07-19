package com.genx.adnmanagement.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "Service_KitType")
@IdClass(ServiceKitTypeId.class)
public class ServiceKitType {
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id")
    private Service service;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kit_type_id")
    private KitType kitType;

    // Getters and setters
    public Service getService() { return service; }
    public void setService(Service service) { this.service = service; }
    public KitType getKitType() { return kitType; }
    public void setKitType(KitType kitType) { this.kitType = kitType; }
} 