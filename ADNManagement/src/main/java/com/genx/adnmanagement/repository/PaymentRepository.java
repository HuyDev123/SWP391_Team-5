package com.genx.adnmanagement.repository;

import com.genx.adnmanagement.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    
    // Lấy tất cả payment của một booking
    List<Payment> findByBooking_IdOrderByCreatedAtDesc(Integer bookingId);

    // Tính tổng tiền đã thanh toán của một booking
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.booking.id = :bookingId")
    BigDecimal getTotalPaidByBookingId(@Param("bookingId") Integer bookingId);
    
    // Kiểm tra xem booking đã thanh toán đủ deposit (30%) chưa
    @Query("SELECT CASE WHEN COALESCE(SUM(p.amount), 0) >= :depositAmount THEN true ELSE false END " +
           "FROM Payment p WHERE p.booking.id = :bookingId")
    boolean isDepositPaid(@Param("bookingId") Integer bookingId, @Param("depositAmount") BigDecimal depositAmount);
    
    // Kiểm tra xem booking đã thanh toán đủ toàn bộ chưa
    @Query("SELECT CASE WHEN COALESCE(SUM(p.amount), 0) >= :totalAmount THEN true ELSE false END " +
           "FROM Payment p WHERE p.booking.id = :bookingId")
    boolean isFullyPaid(@Param("bookingId") Integer bookingId, @Param("totalAmount") BigDecimal totalAmount);
    
    // Lấy payment theo phương thức thanh toán
    List<Payment> findByBooking_IdAndPaymentMethodOrderByCreatedAtDesc(Integer bookingId, String paymentMethod);

    // Lấy payment theo staff xác nhận
    List<Payment> findByStaff_IdOrderByCreatedAtDesc(Integer staffId);

    // Kiểm tra xem có payment nào cho booking này không
    boolean existsByBooking_Id(Integer bookingId);
    
    // Lấy tất cả payment của một booking (alias cho findByBooking_IdOrderByCreatedAtDesc)
    List<Payment> findByBooking_Id(Integer bookingId);

    // Lấy tất cả payment của một user (customer)
    @Query("SELECT p FROM Payment p WHERE p.booking.customer.id = :userId ORDER BY p.createdAt DESC")
    List<Payment> findByUserId(@Param("userId") Integer userId);
    
    // Lấy payment của một user với pagination
    @Query("SELECT p FROM Payment p WHERE p.booking.customer.id = :userId ORDER BY p.createdAt DESC")
    Page<Payment> findByUserIdWithPagination(@Param("userId") Integer userId, Pageable pageable);

    // Staff payment management methods
    
    // Lấy thanh toán chờ xử lý (chưa có staff xác nhận)
    @Query("SELECT p FROM Payment p WHERE p.staff IS NULL ORDER BY p.createdAt DESC")
    Page<Payment> findPendingPayments(Pageable pageable);
    
    // Lấy thanh toán chờ xử lý với tìm kiếm
    @Query("SELECT p FROM Payment p WHERE p.staff IS NULL AND " +
           "(p.booking.fullName LIKE %:search% OR CAST(p.booking.id AS string) LIKE %:search% OR CAST(p.id AS string) LIKE %:search%) " +
           "ORDER BY p.createdAt DESC")
    Page<Payment> findPendingPaymentsWithSearch(@Param("search") String search, Pageable pageable);
    
    // Lấy thanh toán đã xác nhận (có staff xác nhận)
    @Query("SELECT p FROM Payment p WHERE p.staff IS NOT NULL ORDER BY p.createdAt DESC")
    Page<Payment> findConfirmedPayments(Pageable pageable);
    
    // Lấy thanh toán đã xác nhận với filter - sửa đổi để tương thích với JPQL
    @Query(value = "SELECT p.* FROM Payment p WHERE p.staff_id IS NOT NULL " +
           "AND (:date IS NULL OR CONVERT(VARCHAR(10), p.created_at, 120) = :date) " +
           "AND (:method IS NULL OR p.payment_method = :method) " +
           "ORDER BY p.created_at DESC",
           countQuery = "SELECT COUNT(*) FROM Payment p WHERE p.staff_id IS NOT NULL " +
           "AND (:date IS NULL OR CONVERT(VARCHAR(10), p.created_at, 120) = :date) " +
           "AND (:method IS NULL OR p.payment_method = :method)",
           nativeQuery = true)
    Page<Payment> findConfirmedPayments(@Param("date") String date, @Param("method") String method, Pageable pageable);
    
    // Lấy thanh toán đã xác nhận với tìm kiếm và filter - sửa đổi để tương thích với JPQL
    @Query(value = "SELECT p.* FROM Payment p " +
           "LEFT JOIN Booking b ON p.booking_id = b.id " +
           "WHERE p.staff_id IS NOT NULL " +
           "AND (:search IS NULL OR b.full_name LIKE %:search% OR CAST(b.id AS VARCHAR) LIKE %:search% OR CAST(p.id AS VARCHAR) LIKE %:search%) " +
           "AND (:date IS NULL OR CONVERT(VARCHAR(10), p.created_at, 120) = :date) " +
           "AND (:method IS NULL OR p.payment_method = :method) " +
           "ORDER BY p.created_at DESC",
           countQuery = "SELECT COUNT(*) FROM Payment p " +
           "LEFT JOIN Booking b ON p.booking_id = b.id " +
           "WHERE p.staff_id IS NOT NULL " +
           "AND (:search IS NULL OR b.full_name LIKE %:search% OR CAST(b.id AS VARCHAR) LIKE %:search% OR CAST(p.id AS VARCHAR) LIKE %:search%) " +
           "AND (:date IS NULL OR CONVERT(VARCHAR(10), p.created_at, 120) = :date) " +
           "AND (:method IS NULL OR p.payment_method = :method)",
           nativeQuery = true)
    Page<Payment> findConfirmedPaymentsWithSearch(@Param("search") String search, @Param("date") String date,
                                                 @Param("method") String method, Pageable pageable);
}
