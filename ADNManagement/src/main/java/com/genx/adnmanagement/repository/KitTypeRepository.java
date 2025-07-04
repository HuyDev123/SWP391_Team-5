package com.genx.adnmanagement.repository;

import com.genx.adnmanagement.entity.KitType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface KitTypeRepository extends JpaRepository<KitType, Integer> {
    @Query("SELECT DISTINCT kt FROM KitType kt " +
           "JOIN kt.serviceKitTypes skt " +
           "WHERE skt.service.id = :serviceId")
    List<KitType> findByServiceId(@Param("serviceId") Integer serviceId);
} 