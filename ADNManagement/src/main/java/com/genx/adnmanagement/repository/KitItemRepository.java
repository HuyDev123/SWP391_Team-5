package com.genx.adnmanagement.repository;

import com.genx.adnmanagement.entity.KitItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface KitItemRepository extends JpaRepository<KitItem, Integer> {
    Optional<KitItem> findByKitCode(String kitCode);
} 