package com.genx.adnmanagement.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

public class BookingServiceId implements Serializable {
    private Booking booking;
    private Service service;

    public BookingServiceId() {}
    public BookingServiceId(Booking booking, Service service) {
        this.booking = booking;
        this.service = service;
    }
    public Booking getBooking() { return booking; }
    public void setBooking(Booking booking) { this.booking = booking; }
    public Service getService() { return service; }
    public void setService(Service service) { this.service = service; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookingServiceId that = (BookingServiceId) o;
        return Objects.equals(booking, that.booking) && Objects.equals(service, that.service);
    }
    @Override
    public int hashCode() {
        return Objects.hash(booking, service);
    }
}
