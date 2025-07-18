package com.genx.adnmanagement.repository;

import com.genx.adnmanagement.entity.Participant;
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
public interface ParticipantRepository extends JpaRepository<Participant, Integer> {
    
    List<Participant> findByFullNameContainingIgnoreCaseOrBooking_Id(
            String fullName, Integer bookingId);
    
    List<Participant> findByBooking_Id(Integer bookingId);

    @Query("SELECT DISTINCT p FROM Participant p " +
           "JOIN p.booking b " +
           "LEFT JOIN b.customer c " +
           "WHERE LOWER(p.fullName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(b.fullName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR (c IS NOT NULL AND LOWER(c.fullName) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<Participant> searchParticipantsByText(@Param("searchTerm") String searchTerm);
    
    // Tìm theo booking ID chính xác
    @Query("SELECT p FROM Participant p " +
           "JOIN p.booking b " +
           "WHERE b.id = :bookingId")
    List<Participant> findByBookingId(@Param("bookingId") Integer bookingId);
    
    // Tìm theo customer ID chính xác
    @Query("SELECT p FROM Participant p " +
           "JOIN p.booking b " +
           "JOIN b.customer c " +
           "WHERE c.id = :customerId")
    List<Participant> findByCustomerId(@Param("customerId") Integer customerId);
    
    // Method với EntityGraph để eager load booking
    @EntityGraph(attributePaths = {"booking", "booking.customer"})
    List<Participant> findAll();
    
    @EntityGraph(attributePaths = {"booking", "booking.customer"})
    Optional<Participant> findById(Integer id);
    
    // Pagination methods
    @EntityGraph(attributePaths = {"booking", "booking.customer"})
    @Query("SELECT p FROM Participant p")
    Page<Participant> findAllWithPagination(Pageable pageable);
    
    @Query("SELECT DISTINCT p FROM Participant p " +
           "JOIN p.booking b " +
           "LEFT JOIN b.customer c " +
           "WHERE LOWER(p.fullName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(b.fullName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR (c IS NOT NULL AND LOWER(c.fullName) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Participant> searchParticipantsByTextWithPagination(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    @Query("SELECT p FROM Participant p " +
           "JOIN p.booking b " +
           "WHERE b.id = :bookingId")
    Page<Participant> findByBookingIdWithPagination(@Param("bookingId") Integer bookingId, Pageable pageable);
    
    @Query("SELECT p FROM Participant p " +
           "JOIN p.booking b " +
           "JOIN b.customer c " +
           "WHERE c.id = :customerId")
    Page<Participant> findByCustomerIdWithPagination(@Param("customerId") Integer customerId, Pageable pageable);
}
