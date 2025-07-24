package com.genx.adnmanagement.repository;

import com.genx.adnmanagement.entity.TestSample;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TestSampleRepository extends JpaRepository<TestSample, Integer> {
    
    // Tìm theo participant ID
    @Query("SELECT ts FROM TestSample ts " +
           "JOIN ts.participant p " +
           "WHERE p.id = :participantId")
    List<TestSample> findByParticipantId(@Param("participantId") Integer participantId);
    
    // Tìm theo participant ID với phân trang
    @Query("SELECT ts FROM TestSample ts " +
           "JOIN ts.participant p " +
           "WHERE p.id = :participantId")
    Page<TestSample> findByParticipantIdWithPagination(@Param("participantId") Integer participantId, Pageable pageable);
    
    // Tìm theo service ID
    @Query("SELECT ts FROM TestSample ts " +
           "JOIN ts.service s " +
           "WHERE s.id = :serviceId")
    List<TestSample> findByServiceId(@Param("serviceId") Integer serviceId);
    
    // Tìm kiếm theo text (sample code, participant name, service name)
    @Query("SELECT DISTINCT ts FROM TestSample ts " +
           "JOIN ts.participant p " +
           "JOIN ts.service s " +
           "WHERE LOWER(ts.sampleCode) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(p.fullName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(s.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<TestSample> searchTestSamplesByText(@Param("searchTerm") String searchTerm);
    
    // Tìm kiếm theo text với phân trang
    @Query("SELECT DISTINCT ts FROM TestSample ts " +
           "JOIN ts.participant p " +
           "JOIN ts.service s " +
           "WHERE LOWER(ts.sampleCode) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(p.fullName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(s.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<TestSample> searchTestSamplesByTextWithPagination(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    // Tìm theo sample code chính xác
    @Query("SELECT ts FROM TestSample ts " +
           "WHERE ts.sampleCode = :sampleCode")
    Optional<TestSample> findBySampleCode(@Param("sampleCode") String sampleCode);
    
    // Method với EntityGraph để eager load
    @EntityGraph(attributePaths = {"service", "participant", "participant.booking", "collectedBy"})
    List<TestSample> findAll();
    
    // Method với EntityGraph để eager load và phân trang
    @EntityGraph(attributePaths = {"service", "participant", "participant.booking", "collectedBy"})
    @Query("SELECT ts FROM TestSample ts")
    Page<TestSample> findAllWithPagination(Pageable pageable);
    
    // Đếm số mẫu theo bookingId và serviceId
    @Query("SELECT COUNT(ts) FROM TestSample ts WHERE ts.participant.booking.id = :bookingId AND ts.service.id = :serviceId")
    int countByBookingIdAndServiceId(@Param("bookingId") Integer bookingId, @Param("serviceId") Integer serviceId);
}
