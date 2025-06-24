package com.genx.adnmanagement.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "Booking_Service")
@IdClass(BookingServiceId.class)
public class BookingService {
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id")
    private Service service;

    // Getters and setters
    public Booking getBooking() { return booking; }
    public void setBooking(Booking booking) { this.booking = booking; }
    public Service getService() { return service; }
    public void setService(Service service) { this.service = service; }
}
