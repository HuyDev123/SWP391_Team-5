package com.genx.adnmanagement.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Entity
@Table(name = "TestResult")
public class TestResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Service service;

    @Column(name = "result_code", length = 100, unique = true)
    private String resultCode;

    @Column(name = "probability_percentage", precision = 5, scale = 2)
    private BigDecimal probabilityPercentage;

    @Column(name = "conclusion", columnDefinition = "NVARCHAR(MAX)")
    private String conclusion;

    @Column(name = "detailed_analysis", columnDefinition = "NVARCHAR(MAX)")
    private String detailedAnalysis;

    @Column(name = "test_method", length = 255)
    private String testMethod;

    @Column(name = "lab_technician", length = 255)
    private String labTechnician;

    @Column(name = "reviewed_by", length = 255)
    private String reviewedBy;

    @Column(name = "test_date")
    private LocalDateTime testDate;

    @Column(name = "result_date")
    private LocalDateTime resultDate;

    @Column(name = "status", length = 50)
    private String status; // PENDING, COMPLETED, REVIEWED, DELIVERED

    @Column(name = "pdf_file_path", length = 500)
    private String pdfFilePath;

    @Column(name = "notes", columnDefinition = "NVARCHAR(MAX)")
    private String notes;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public TestResult() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = "PENDING";
    }

    public TestResult(Booking booking, Service service, String resultCode) {
        this();
        this.booking = booking;
        this.service = service;
        this.resultCode = resultCode;
    }

    // Getters and setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Booking getBooking() { return booking; }
    public void setBooking(Booking booking) { this.booking = booking; }

    public Service getService() { return service; }
    public void setService(Service service) { this.service = service; }

    public String getResultCode() { return resultCode; }
    public void setResultCode(String resultCode) { this.resultCode = resultCode; }

    public BigDecimal getProbabilityPercentage() { return probabilityPercentage; }
    public void setProbabilityPercentage(BigDecimal probabilityPercentage) { this.probabilityPercentage = probabilityPercentage; }

    public String getConclusion() { return conclusion; }
    public void setConclusion(String conclusion) { this.conclusion = conclusion; }

    public String getDetailedAnalysis() { return detailedAnalysis; }
    public void setDetailedAnalysis(String detailedAnalysis) { this.detailedAnalysis = detailedAnalysis; }

    public String getTestMethod() { return testMethod; }
    public void setTestMethod(String testMethod) { this.testMethod = testMethod; }

    public String getLabTechnician() { return labTechnician; }
    public void setLabTechnician(String labTechnician) { this.labTechnician = labTechnician; }

    public String getReviewedBy() { return reviewedBy; }
    public void setReviewedBy(String reviewedBy) { this.reviewedBy = reviewedBy; }

    public LocalDateTime getTestDate() { return testDate; }
    public void setTestDate(LocalDateTime testDate) { this.testDate = testDate; }

    public LocalDateTime getResultDate() { return resultDate; }
    public void setResultDate(LocalDateTime resultDate) { this.resultDate = resultDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { 
        this.status = status; 
        this.updatedAt = LocalDateTime.now();
    }

    public String getPdfFilePath() { return pdfFilePath; }
    public void setPdfFilePath(String pdfFilePath) { this.pdfFilePath = pdfFilePath; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // Helper methods
    public String getCustomerName() {
        return this.booking != null ? this.booking.getFullName() : "N/A";
    }

    public String getServiceName() {
        return this.service != null ? this.service.getName() : "N/A";
    }

    public String getFormattedProbability() {
        if (probabilityPercentage != null) {
            return probabilityPercentage.toString() + "%";
        }
        return "Chưa có kết quả";
    }

    public String getStatusDisplay() {
        switch (status) {
            case "PENDING": return "Đang xử lý";
            case "COMPLETED": return "Hoàn thành";
            case "REVIEWED": return "Đã duyệt";
            case "DELIVERED": return "Đã giao";
            default: return status;
        }
    }
} 