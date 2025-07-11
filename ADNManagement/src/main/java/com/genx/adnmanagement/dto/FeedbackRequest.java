package com.genx.adnmanagement.dto;

import jakarta.validation.constraints.*;

public class FeedbackRequest {
    
    @NotNull(message = "Booking ID không được để trống")
    private Integer bookingId;
    
    @NotNull(message = "Service ID không được để trống")
    private Integer serviceId;
    
    private Integer userId;
    
    private Integer staffId;
    
    @NotNull(message = "Rating không được để trống")
    @Min(value = 1, message = "Rating phải từ 1-5")
    @Max(value = 5, message = "Rating phải từ 1-5")
    private Integer rating;
    
    @Size(max = 1000, message = "Comment không được quá 1000 ký tự")
    private String comment;
    
    // Constructor
    public FeedbackRequest() {
    }
    
    public FeedbackRequest(Integer bookingId, Integer serviceId, Integer userId, Integer staffId, Integer rating, String comment) {
        this.bookingId = bookingId;
        this.serviceId = serviceId;
        this.userId = userId;
        this.staffId = staffId;
        this.rating = rating;
        this.comment = comment;
    }
    
    // Getters and setters
    public Integer getBookingId() {
        return bookingId;
    }
    
    public void setBookingId(Integer bookingId) {
        this.bookingId = bookingId;
    }
    
    public Integer getServiceId() {
        return serviceId;
    }
    
    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }
    
    public Integer getUserId() {
        return userId;
    }
    
    public void setUserId(Integer userId) {
        this.userId = userId;
    }
    
    public Integer getStaffId() {
        return staffId;
    }
    
    public void setStaffId(Integer staffId) {
        this.staffId = staffId;
    }
    
    public Integer getRating() {
        return rating;
    }
    
    public void setRating(Integer rating) {
        this.rating = rating;
    }
    
    public String getComment() {
        return comment;
    }
    
    public void setComment(String comment) {
        this.comment = comment;
    }
} 