package com.genx.adnmanagement.repository;

import com.genx.adnmanagement.entity.BookingService;
import com.genx.adnmanagement.entity.BookingServiceId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingServiceRepository extends JpaRepository<BookingService, BookingServiceId> {
    List<BookingService> findByBooking_Id(Integer bookingId);
}
