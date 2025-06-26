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

    @OneToMany(mappedBy = "bookingService", cascade = CascadeType.ALL)
    private List<KitItem> kitItems;

    // Getters and setters
    public Booking getBooking() { return booking; }
    public void setBooking(Booking booking) { this.booking = booking; }
    public Service getService() { return service; }
    public void setService(Service service) { this.service = service; }
    public List<KitItem> getKitItems() { return kitItems; }
    public void setKitItems(List<KitItem> kitItems) { this.kitItems = kitItems; }
}
