package com.alexvait.accountingapi.accounting.repository;

import com.alexvait.accountingapi.accounting.entity.PositionEntity;
import com.alexvait.accountingapi.usermanagement.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PositionRepository extends JpaRepository<PositionEntity, Long> {
}
