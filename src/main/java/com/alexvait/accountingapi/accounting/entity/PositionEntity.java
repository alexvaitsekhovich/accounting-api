package com.alexvait.accountingapi.accounting.entity;

import com.alexvait.accountingapi.accounting.entity.enums.Payment;
import com.alexvait.accountingapi.usermanagement.entity.UserEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "accounting_positions")
@Data
public class PositionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private long id;

    @Column(nullable = false)
    private long amount;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Payment payment;

    @Column(nullable = false)
    private long customerId;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "invoiceId")
    private InvoiceEntity invoice;
}
