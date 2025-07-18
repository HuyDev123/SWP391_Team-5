package com.genx.adnmanagement.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "Booking")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "booking_date")
    private LocalDateTime bookingDate;

    @Column(length = 100)
    private String status;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String note;

    @Column(name = "full_name", length = 255)
    private String fullName;

    @Column(length = 255)
    private String email;

    @Column(length = 20)
    private String phone;

    @Column(name = "is_administrative")
    private Boolean isAdministrative;

    @Column(name = "is_center_collected")
    private Boolean isCenterCollected;

    @Column(length = 500)
    private String address;

    @Column(name = "center_sample_date")
    private LocalDate centerSampleDate;

    @Column(name = "center_sample_time")
    private LocalTime centerSampleTime;

    @Column(name = "kit_status")
    private String kitStatus;

    @Column(name = "payment_method", length = 20)
    private String paymentMethod;

    @Column(name = "num_participants")
    private Integer numParticipants;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User staff;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<BookingService> bookingServices;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Participant> participants;

    // Getters and setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public LocalDateTime getBookingDate() { return bookingDate; }
    public void setBookingDate(LocalDateTime bookingDate) { this.bookingDate = bookingDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public Boolean getIsAdministrative() { return isAdministrative; }
    public void setIsAdministrative(Boolean isAdministrative) { this.isAdministrative = isAdministrative; }
    public Boolean getIsCenterCollected() { return isCenterCollected; }
    public void setIsCenterCollected(Boolean isCenterCollected) { this.isCenterCollected = isCenterCollected; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public LocalDate getCenterSampleDate() { return centerSampleDate; }
    public void setCenterSampleDate(LocalDate centerSampleDate) { this.centerSampleDate = centerSampleDate; }
    public LocalTime getCenterSampleTime() { return centerSampleTime; }
    public void setCenterSampleTime(LocalTime centerSampleTime) { this.centerSampleTime = centerSampleTime; }
    public User getCustomer() { return customer; }
    public void setCustomer(User customer) { this.customer = customer; }
    public User getStaff() { return staff; }
    public void setStaff(User staff) { this.staff = staff; }
    public List<BookingService> getBookingServices() { return bookingServices; }
    public void setBookingServices(List<BookingService> bookingServices) { this.bookingServices = bookingServices; }
    public List<Participant> getParticipants() { return participants; }
    public void setParticipants(List<Participant> participants) { this.participants = participants; }
    public String getKitStatus() { return kitStatus; }
    public void setKitStatus(String kitStatus) { this.kitStatus = kitStatus; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public Integer getNumParticipants() { return numParticipants; }
    public void setNumParticipants(Integer numParticipants) { this.numParticipants = numParticipants; }

    // Helper methods
    public String getCustomerName() {
        return this.fullName;
    }

    public String getServiceType() {
        if (this.bookingServices != null && !this.bookingServices.isEmpty()) {
            return this.bookingServices.get(0).getService().getName();
        }
        return "N/A";
    }

    public String getPurpose() {
        return this.isAdministrative != null && this.isAdministrative ? "Hành chính" : "Dân sự";
    }
}

