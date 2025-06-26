package com.genx.adnmanagement.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "KitItem")
public class KitItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name = "booking_id", referencedColumnName = "booking_id"),
        @JoinColumn(name = "service_id", referencedColumnName = "service_id")
    })
    private BookingService bookingService;

    @Column(name = "kit_code", length = 255, unique = true)
    private String kitCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kit_type_id", nullable = false)
    private KitType kitType;

    @Column(name = "delivery_status", length = 100)
    private String deliveryStatus;

    @Column(name = "send_date")
    private LocalDate sendDate;

    @Column(name = "receive_date")
    private LocalDate receiveDate;

    @Column(name = "note", columnDefinition = "NVARCHAR(MAX)")
    private String note;

    // Getters and setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public BookingService getBookingService() { return bookingService; }
    public void setBookingService(BookingService bookingService) { this.bookingService = bookingService; }
    public String getKitCode() { return kitCode; }
    public void setKitCode(String kitCode) { this.kitCode = kitCode; }
    public KitType getKitType() { return kitType; }
    public void setKitType(KitType kitType) { this.kitType = kitType; }
    public String getDeliveryStatus() { return deliveryStatus; }
    public void setDeliveryStatus(String deliveryStatus) { this.deliveryStatus = deliveryStatus; }
    public LocalDate getSendDate() { return sendDate; }
    public void setSendDate(LocalDate sendDate) { this.sendDate = sendDate; }
    public LocalDate getReceiveDate() { return receiveDate; }
    public void setReceiveDate(LocalDate receiveDate) { this.receiveDate = receiveDate; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
} 