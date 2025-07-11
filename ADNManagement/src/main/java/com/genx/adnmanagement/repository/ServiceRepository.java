package com.genx.adnmanagement.repository;

import com.genx.adnmanagement.entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceRepository extends JpaRepository<Service, Integer> {
    List<Service> findByIsActiveTrue();
}

