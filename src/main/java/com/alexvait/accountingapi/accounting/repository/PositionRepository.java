package com.alexvait.accountingapi.accounting.repository;

import com.alexvait.accountingapi.accounting.entity.PositionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PositionRepository extends JpaRepository<PositionEntity, Long> {

    List<PositionEntity> findAllByUserIdAndInvoiceIdIsNull(long userId);
}
