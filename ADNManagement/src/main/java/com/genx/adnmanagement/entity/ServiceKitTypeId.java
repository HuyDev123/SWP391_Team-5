package com.genx.adnmanagement.entity;

import java.io.Serializable;
import java.util.Objects;

public class ServiceKitTypeId implements Serializable {
    private Integer service;
    private Integer kitType;

    public ServiceKitTypeId() {}
    public ServiceKitTypeId(Integer service, Integer kitType) {
        this.service = service;
        this.kitType = kitType;
    }
    public Integer getService() { return service; }
    public void setService(Integer service) { this.service = service; }
    public Integer getKitType() { return kitType; }
    public void setKitType(Integer kitType) { this.kitType = kitType; }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServiceKitTypeId that = (ServiceKitTypeId) o;
        return Objects.equals(service, that.service) && Objects.equals(kitType, that.kitType);
    }
    @Override
    public int hashCode() {
        return Objects.hash(service, kitType);
    }
} 