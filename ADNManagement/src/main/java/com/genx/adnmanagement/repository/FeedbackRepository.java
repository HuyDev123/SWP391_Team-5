package com.genx.adnmanagement.repository;

import com.genx.adnmanagement.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Integer> {
    
    // Tìm feedback theo booking_id và service_id
    @Query("SELECT f FROM Feedback f WHERE f.booking.id = :bookingId AND f.service.id = :serviceId")
    Optional<Feedback> findByBookingIdAndServiceId(@Param("bookingId") Integer bookingId, @Param("serviceId") Integer serviceId);
    
    // Tìm tất cả feedback theo booking_id
    @Query("SELECT f FROM Feedback f WHERE f.booking.id = :bookingId")
    List<Feedback> findByBookingId(@Param("bookingId") Integer bookingId);
    
    // Tìm tất cả feedback theo user_id
    @Query("SELECT f FROM Feedback f WHERE f.user.id = :userId")
    List<Feedback> findByUserId(@Param("userId") Integer userId);
    
    // Tìm tất cả feedback theo staff_id
    @Query("SELECT f FROM Feedback f WHERE f.staff.id = :staffId")
    List<Feedback> findByStaffId(@Param("staffId") Integer staffId);
    
    // Kiểm tra xem đã có feedback cho booking_id và service_id chưa
    @Query("SELECT COUNT(f) > 0 FROM Feedback f WHERE f.booking.id = :bookingId AND f.service.id = :serviceId")
    boolean existsByBookingIdAndServiceId(@Param("bookingId") Integer bookingId, @Param("serviceId") Integer serviceId);
    
    // Tìm feedback theo booking_id, service_id và user_id
    @Query("SELECT f FROM Feedback f WHERE f.booking.id = :bookingId AND f.service.id = :serviceId AND f.user.id = :userId")
    Optional<Feedback> findByBookingIdAndServiceIdAndUserId(
        @Param("bookingId") Integer bookingId, 
        @Param("serviceId") Integer serviceId, 
        @Param("userId") Integer userId
    );
    
    // Tính điểm trung bình rating theo staff_id
    @Query("SELECT AVG(f.rating) FROM Feedback f WHERE f.staff.id = :staffId")
    Double getAverageRatingByStaffId(@Param("staffId") Integer staffId);
    
    // Tính điểm trung bình rating theo service_id
    @Query("SELECT AVG(f.rating) FROM Feedback f WHERE f.service.id = :serviceId")
    Double getAverageRatingByServiceId(@Param("serviceId") Integer serviceId);
} 