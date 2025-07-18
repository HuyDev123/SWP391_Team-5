package com.genx.adnmanagement.repository;

import com.genx.adnmanagement.entity.TestResult;
import com.genx.adnmanagement.entity.Booking;
import com.genx.adnmanagement.entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface TestResultRepository extends JpaRepository<TestResult, Integer> {
    // Find by booking
    List<TestResult> findByBooking(Booking booking);
    List<TestResult> findByBookingId(Integer bookingId);

    // Find by service
    List<TestResult> findByService(Service service);
    List<TestResult> findByServiceId(Integer serviceId);

    // Find by booking and service
    Optional<TestResult> findByBookingAndService(Booking booking, Service service);
    Optional<TestResult> findByBookingIdAndServiceId(Integer bookingId, Integer serviceId);
} 