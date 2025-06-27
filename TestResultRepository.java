package com.genx.adnmanagement.repository;

import com.genx.adnmanagement.entity.TestResult;
import com.genx.adnmanagement.entity.Booking;
import com.genx.adnmanagement.entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface TestResultRepository extends JpaRepository<TestResult, Integer> {
    
    // Find by booking
    List<TestResult> findByBooking(Booking booking);
    List<TestResult> findByBookingId(Integer bookingId);
    
    // Find by service
    List<TestResult> findByService(Service service);
    List<TestResult> findByServiceId(Integer serviceId);
    
    // Find by status
    List<TestResult> findByStatus(String status);
    List<TestResult> findByStatusIn(List<String> statuses);
    
    // Find by result code
    Optional<TestResult> findByResultCode(String resultCode);
    
    // Find by booking and service
    Optional<TestResult> findByBookingAndService(Booking booking, Service service);
    Optional<TestResult> findByBookingIdAndServiceId(Integer bookingId, Integer serviceId);
    
    // Custom queries
    @Query("SELECT tr FROM TestResult tr WHERE tr.booking.fullName LIKE %:customerName%")
    List<TestResult> findByCustomerNameContaining(@Param("customerName") String customerName);
    
    @Query("SELECT tr FROM TestResult tr WHERE tr.labTechnician = :technician")
    List<TestResult> findByLabTechnician(@Param("technician") String technician);
    
    @Query("SELECT tr FROM TestResult tr WHERE tr.reviewedBy = :reviewer")
    List<TestResult> findByReviewedBy(@Param("reviewer") String reviewer);
    
    @Query("SELECT tr FROM TestResult tr WHERE tr.status = 'PENDING' ORDER BY tr.createdAt ASC")
    List<TestResult> findPendingResults();
    
    @Query("SELECT tr FROM TestResult tr WHERE tr.status = 'COMPLETED' AND tr.reviewedBy IS NULL ORDER BY tr.resultDate ASC")
    List<TestResult> findResultsNeedingReview();
    
    @Query("SELECT tr FROM TestResult tr WHERE tr.booking.id = :bookingId ORDER BY tr.createdAt DESC")
    List<TestResult> findByBookingIdOrderByCreatedAtDesc(@Param("bookingId") Integer bookingId);
    
    // Statistics queries
    @Query("SELECT COUNT(tr) FROM TestResult tr WHERE tr.status = :status")
    Long countByStatus(@Param("status") String status);
    
    @Query("SELECT tr.status as status, COUNT(tr) as count FROM TestResult tr GROUP BY tr.status")
    List<Object[]> getStatusStatistics();
} 