package com.alexvait.accountingapi.accounting.entity;

import com.alexvait.accountingapi.usermanagement.entity.UserEntity;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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
    @Column(nullable = false, length = 50)
    @Size(min = 5, max = 50)
    @NotNull(message = "Label is mandatory")
    private String label;
    @Column(nullable = false)
    private long customerId;
    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    @ToString.Exclude
    private UserEntity user;
    @ManyToOne
    @JoinColumn(name = "invoiceId")
    private InvoiceEntity invoice;

    public enum Payment {
        CASH,
        CREDIT_CARD,
        PAYPAL,
        VOUCHER
    }
}
