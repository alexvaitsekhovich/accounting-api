package com.alexvait.accountingapi.accounting.repository;

import com.alexvait.accountingapi.accounting.entity.InvoiceEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvoiceRepository extends JpaRepository<InvoiceEntity, Long> {

    Page<InvoiceEntity> findAllByUserId(long userId, Pageable pageable);

    InvoiceEntity findByNumber(String number);
}
