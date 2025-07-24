package com.genx.adnmanagement.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "Payment")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;
    
    @Column(name = "amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;
    
    @Column(name = "payment_method", nullable = false, length = 20)
    private String paymentMethod; // 'CASH', 'COD', 'BANK_TRANSFER'
    
    @Column(name = "receipt_image", length = 255)
    private String receiptImage; // Đường dẫn ảnh biên lai (cho BANK_TRANSFER)
    
    @Column(name = "note", length = 255)
    private String note;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id")
    private User staff; // Nhân viên xác nhận thanh toán
    
    // Constructors
    public Payment() {}
    
    public Payment(Booking booking, BigDecimal amount, String paymentMethod, User staff) {
        this.booking = booking;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.staff = staff;
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public Booking getBooking() {
        return booking;
    }
    
    public void setBooking(Booking booking) {
        this.booking = booking;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public String getPaymentMethod() {
        return paymentMethod;
    }
    
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    public String getReceiptImage() {
        return receiptImage;
    }
    
    public void setReceiptImage(String receiptImage) {
        this.receiptImage = receiptImage;
    }
    
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public User getStaff() {
        return staff;
    }
    
    public void setStaff(User staff) {
        this.staff = staff;
    }
    
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
} 