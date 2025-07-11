package com.genx.adnmanagement.repository;

import com.genx.adnmanagement.entity.ServiceKitType;
import com.genx.adnmanagement.entity.ServiceKitTypeId;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ServiceKitTypeRepository extends JpaRepository<ServiceKitType, ServiceKitTypeId> {
    List<ServiceKitType> findByService_Id(Integer serviceId);
} 