package com.genx.adnmanagement.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class BookingRequest {
    private String fullName;
    private String email;
    private String phone;
    private List<Integer> serviceIds;
    private Boolean isAdministrative;
    private Boolean isCenterCollected;
    private String address;
    private String note;
    private LocalDate centerSampleDate;
    private LocalTime centerSampleTime;
    private Integer customerId; // Thêm trường userId để nhận từ client nếu có
    // getters and setters
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public List<Integer> getServiceIds() { return serviceIds; }
    public void setServiceIds(List<Integer> serviceIds) { this.serviceIds = serviceIds; }
    public Boolean getIsAdministrative() { return isAdministrative; }
    public void setIsAdministrative(Boolean isAdministrative) { this.isAdministrative = isAdministrative; }
    public Boolean getIsCenterCollected() { return isCenterCollected; }
    public void setIsCenterCollected(Boolean isCenterCollected) { this.isCenterCollected = isCenterCollected; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public LocalDate getCenterSampleDate() { return centerSampleDate; }
    public void setCenterSampleDate(LocalDate centerSampleDate) { this.centerSampleDate = centerSampleDate; }
    public LocalTime getCenterSampleTime() { return centerSampleTime; }
    public void setCenterSampleTime(LocalTime centerSampleTime) { this.centerSampleTime = centerSampleTime; }
    public Integer getCustomerId() { return customerId; }
    public void setCustomerId(Integer customerId) { this.customerId = customerId; }
}
