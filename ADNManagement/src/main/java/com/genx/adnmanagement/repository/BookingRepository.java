package com.genx.adnmanagement.repository;

import com.genx.adnmanagement.entity.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    Page<Booking> findByUserId(Integer userId, Pageable pageable);

    @Query("SELECT DISTINCT b FROM Booking b " +
           "LEFT JOIN b.bookingServices bs " +
           "LEFT JOIN bs.service s " +
           "WHERE b.user.id = :userId " +
           "AND (:status IS NULL OR b.status = :status) " +
           "AND (:bookingDate IS NULL OR CAST(b.bookingDate AS date) = :bookingDate)")
    Page<Booking> findByUserIdAndFilters(
        @Param("userId") Integer userId,
        @Param("status") String status,
        @Param("bookingDate") LocalDate bookingDate,
        Pageable pageable
    );

    @Query("SELECT DISTINCT b FROM Booking b " +
           "JOIN b.bookingServices bs " +
           "JOIN bs.service s " +
           "WHERE b.user.id = :userId " +
           "AND (:status IS NULL OR b.status = :status) " +
           "AND (:bookingDate IS NULL OR CAST(b.bookingDate AS date) = :bookingDate) " +
           "AND s.id = :serviceId")
    Page<Booking> findByUserIdAndServiceId(
        @Param("userId") Integer userId,
        @Param("status") String status,
        @Param("bookingDate") LocalDate bookingDate,
        @Param("serviceId") Integer serviceId,
        Pageable pageable
    );
}
