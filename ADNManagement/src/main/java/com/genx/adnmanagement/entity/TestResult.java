package com.genx.adnmanagement.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "TestResult", uniqueConstraints = @UniqueConstraint(columnNames = {"booking_id", "service_id"}))
public class TestResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false)
    private Service service;

    @Column(name = "result_file", length = 255)
    private String resultFile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // Getters and setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Booking getBooking() { return booking; }
    public void setBooking(Booking booking) { this.booking = booking; }

    public Service getService() { return service; }
    public void setService(Service service) { this.service = service; }

    public String getResultFile() { return resultFile; }
    public void setResultFile(String resultFile) { this.resultFile = resultFile; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
} 