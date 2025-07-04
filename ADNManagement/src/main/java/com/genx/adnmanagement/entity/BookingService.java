package com.genx.adnmanagement.entity;

import jakarta.persistence.*;
import java.util.List;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kit_type_id")
    private KitType kitType;

    // Getters and setters
    public Booking getBooking() { return booking; }
    public void setBooking(Booking booking) { this.booking = booking; }
    public Service getService() { return service; }
    public void setService(Service service) { this.service = service; }
    public KitType getKitType() { return kitType; }
    public void setKitType(KitType kitType) { this.kitType = kitType; }
}
