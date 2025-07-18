package com.genx.adnmanagement.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "[User]")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "full_name")
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "role_id", nullable = false)
    private Integer roleId;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "customer")
    @JsonIgnore
    private java.util.List<Booking> bookingsAsCustomer;

    @OneToMany(mappedBy = "staff")
    @JsonIgnore
    private java.util.List<Booking> bookingsAsStaff;

    public User() {
        // Required by JPA
    }

    public User(String fullName, String email) {
        this.fullName = fullName;
        this.email = email;
        this.roleId = 4;
        this.isActive = true;
    }

    // Getters and setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Integer getRoleId() { return roleId; }
    public void setRoleId(Integer roleId) { this.roleId = roleId; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public java.util.List<Booking> getBookingsAsCustomer() { return bookingsAsCustomer; }
    public void setBookingsAsCustomer(java.util.List<Booking> bookingsAsCustomer) { this.bookingsAsCustomer = bookingsAsCustomer; }
    public java.util.List<Booking> getBookingsAsStaff() { return bookingsAsStaff; }
    public void setBookingsAsStaff(java.util.List<Booking> bookingsAsStaff) { this.bookingsAsStaff = bookingsAsStaff; }
}
