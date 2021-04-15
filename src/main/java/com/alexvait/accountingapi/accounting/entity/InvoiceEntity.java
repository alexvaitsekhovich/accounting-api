package com.alexvait.accountingapi.accounting.entity;

import com.alexvait.accountingapi.usermanagement.entity.UserEntity;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "invoices")
@Data
public class InvoiceEntity {

    public enum InvoiceState {
        OPEN,
        PAID,
        CANCELLED,
        PARTLY_PAID
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private long id;

    @Column(nullable = false, length = 20)
    private String number;

    @Column(nullable = false)
    private long amount;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private InvoiceState state = InvoiceState.OPEN;

    @Column(nullable = false)
    private long customerId;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    @ToString.Exclude
    private UserEntity user;

    @OneToMany(mappedBy = "invoice")
    @ToString.Exclude
    private List<PositionEntity> positions;
}
