package com.genx.adnmanagement.repository;

import com.genx.adnmanagement.entity.BookingService;
import com.genx.adnmanagement.entity.BookingServiceId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingServiceRepository extends JpaRepository<BookingService, BookingServiceId> {
    
    // Lấy tất cả booking services của một booking
    List<BookingService> findByBookingId(Integer bookingId);
    
    // Tính tổng giá trị booking (từ các service)
    @Query("SELECT COALESCE(SUM(s.price), 0) FROM BookingService bs " +
           "JOIN bs.service s WHERE bs.booking.id = :bookingId")
    BigDecimal getTotalBookingAmount(@Param("bookingId") Integer bookingId);
    
    // Tính tổng giá trị booking bao gồm cả phí mẫu thêm (nếu có)
    // Công thức: Tổng tiền = Σ(Service.price) + Σ(extraPerSampleFee × (sampleQuantity - 2))
    @Query("SELECT COALESCE(SUM(s.price + COALESCE(s.extraPerSampleFee, 0) * GREATEST(bs.sampleQuantity - 2, 0)), 0) " +
           "FROM BookingService bs JOIN bs.service s WHERE bs.booking.id = :bookingId")
    BigDecimal getTotalBookingAmountWithExtraFees(@Param("bookingId") Integer bookingId);

    Optional<BookingService> findByBookingIdAndServiceId(Integer bookingId, Integer serviceId);
}
