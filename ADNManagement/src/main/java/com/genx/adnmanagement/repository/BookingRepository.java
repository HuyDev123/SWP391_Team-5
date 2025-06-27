package com.genx.adnmanagement.repository;

import com.genx.adnmanagement.entity.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    Page<Booking> findByCustomerId(Integer userId, Pageable pageable);

    @Query("SELECT DISTINCT b FROM Booking b " +
           "LEFT JOIN b.bookingServices bs " +
           "LEFT JOIN bs.service s " +
           "WHERE b.customer.id = :userId " +
           "AND (:status IS NULL OR b.status = :status) " +
           "AND (:bookingDate IS NULL OR CAST(b.bookingDate AS date) = :bookingDate) " +
           "AND (:serviceId IS NULL OR s.id = :serviceId) " +
           "ORDER BY b.bookingDate DESC")
    Page<Booking> findByUserIdAndFilters(
        @Param("userId") Integer userId,
        @Param("status") String status,
        @Param("bookingDate") LocalDate bookingDate,
        @Param("serviceId") Integer serviceId,
        Pageable pageable
    );

    // Method for staff to get all appointments with filters
    @Query("SELECT DISTINCT b FROM Booking b " +
           "LEFT JOIN b.bookingServices bs " +
           "LEFT JOIN bs.service s " +
           "WHERE (:status IS NULL OR b.status = :status) " +
           "AND (:bookingDate IS NULL OR CAST(b.bookingDate AS date) = :bookingDate) " +
           "AND (:serviceId IS NULL OR s.id = :serviceId) " +
           "AND (:searchQuery IS NULL OR " +
           "LOWER(b.fullName) LIKE LOWER(CONCAT('%', :searchQuery, '%')) OR " +
           "CAST(b.id AS string) = :searchQuery OR " +
           "CAST(b.customer.id AS string) = :searchQuery) " +
           "ORDER BY b.bookingDate DESC")
    Page<Booking> findAllWithFilters(
        @Param("status") String status,
        @Param("bookingDate") LocalDate bookingDate,
        @Param("serviceId") Integer serviceId,
        @Param("searchQuery") String searchQuery,
        Pageable pageable
    );
}
