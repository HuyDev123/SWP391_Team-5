package com.genx.adnmanagement.entity;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private User customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id")
    private User staff;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL)
    private List<BookingService> bookingServices;

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
}

