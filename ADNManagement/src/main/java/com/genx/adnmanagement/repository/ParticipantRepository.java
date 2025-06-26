package com.genx.adnmanagement.repository;

import com.genx.adnmanagement.entity.Participant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipantRepository extends JpaRepository<Participant, Integer> {
} 