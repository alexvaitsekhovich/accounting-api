package com.alexvait.accountingapi.accounting.entity;

import com.alexvait.accountingapi.accounting.entity.enums.PositionPayment;
import com.alexvait.accountingapi.usermanagement.entity.UserEntity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.sql.Timestamp;

@Entity
@Table(name = "accounting_positions")
@Getter
@Setter
public class PositionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private long id;

    @Column(nullable = false)
    private long amount;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PositionPayment payment;

    @Column(nullable = false, length = 50)
    @Size(min = 5, max = 50)
    @NotNull(message = "Label is mandatory")
    private String label;

    @Column(nullable = false)
    private long customerId;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "invoiceId")
    private InvoiceEntity invoice;

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createdDate;

    @UpdateTimestamp
    private Timestamp lastModifiedDate;
}
