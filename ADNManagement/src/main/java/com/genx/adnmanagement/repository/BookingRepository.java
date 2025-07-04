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

    // Method for customer kit management - get only their at-home bookings with kit status >= "Đã gửi"
    @Query(
        value = "SELECT DISTINCT b.* FROM Booking b " +
                "WHERE b.customer_id = :userId " +
                "AND b.is_center_collected = 0 " +
                "AND b.kit_status IN (N'Đã gửi', N'Chưa lấy mẫu', N'Chưa nhận mẫu', N'Đã nhận mẫu', N'Lỗi mẫu') " +
                "AND (:appointmentId IS NULL OR CAST(b.id AS VARCHAR) = :appointmentId) " +
                "AND (:date IS NULL OR CAST(b.booking_date AS DATE) = :date) " +
                "ORDER BY b.booking_date DESC", 
        countQuery = "SELECT COUNT(DISTINCT b.id) FROM Booking b " +
                "WHERE b.customer_id = :userId " +
                "AND b.is_center_collected = 0 " +
                "AND b.kit_status IN (N'Đã gửi', N'Chưa lấy mẫu', N'Chưa nhận mẫu', N'Đã nhận mẫu', N'Lỗi mẫu') " +
                "AND (:appointmentId IS NULL OR CAST(b.id AS VARCHAR) = :appointmentId) " +
                "AND (:date IS NULL OR CAST(b.booking_date AS DATE) = :date)",
        nativeQuery = true
    )
    Page<Booking> findByCustomerIdAndIsCenterCollectedFalseWithFilters(
        @Param("userId") Integer userId,
        @Param("appointmentId") String appointmentId,
        @Param("date") String date,
        @Param("kitStatus") String kitStatus,
        Pageable pageable
    );

    // Method for staff to get all appointments with filters
    @Query("SELECT DISTINCT b FROM Booking b " +
           "LEFT JOIN b.bookingServices bs " +
           "LEFT JOIN bs.service s " +
           "WHERE (:statuses IS NULL OR b.status IN :statuses) " +
           "AND (:bookingDate IS NULL OR CAST(b.bookingDate AS date) = :bookingDate) " +
           "AND (:serviceId IS NULL OR s.id = :serviceId) " +
           "AND (:searchQuery IS NULL OR " +
           "LOWER(b.fullName) LIKE LOWER(CONCAT('%', :searchQuery, '%')) OR " +
           "CAST(b.id AS string) = :searchQuery OR " +
           "CAST(b.customer.id AS string) = :searchQuery) " +
           "ORDER BY b.bookingDate DESC")
    Page<Booking> findAllWithFilters(
        @Param("statuses") java.util.List<String> statuses,
        @Param("bookingDate") LocalDate bookingDate,
        @Param("serviceId") Integer serviceId,
        @Param("searchQuery") String searchQuery,
        Pageable pageable
    );

    // Method for kit management - get only at-home bookings with status >= "Chưa lấy mẫu"
    @Query(
        value = "SELECT DISTINCT b.* FROM Booking b " +
                "LEFT JOIN Booking_Service bs ON b.id = bs.booking_id " +
                "LEFT JOIN Service s ON bs.service_id = s.id " +
                "WHERE b.is_center_collected = 0 " +
                "AND b.status IN (N'Chưa lấy mẫu', N'Đã lấy mẫu', N'Đã trả kết quả') " +
                "AND (:status IS NULL OR b.kit_status = :status) " +
                "AND (:search IS NULL OR " +
                "LOWER(b.full_name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
                "CAST(b.id AS VARCHAR) = :search) " +
                "ORDER BY b.booking_date DESC", 
        countQuery = "SELECT COUNT(DISTINCT b.id) FROM Booking b " +
                "LEFT JOIN Booking_Service bs ON b.id = bs.booking_id " +
                "LEFT JOIN Service s ON bs.service_id = s.id " +
                "WHERE b.is_center_collected = 0 " +
                "AND b.status IN (N'Chưa lấy mẫu', N'Đã lấy mẫu', N'Đã trả kết quả') " +
                "AND (:status IS NULL OR b.kit_status = :status) " +
                "AND (:search IS NULL OR " +
                "LOWER(b.full_name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
                "CAST(b.id AS VARCHAR) = :search)",
        nativeQuery = true
    )
    Page<Booking> findByIsCenterCollectedFalseWithFilters(
        @Param("search") String search,
        @Param("status") String status,
        Pageable pageable
    );
}
