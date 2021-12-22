package com.java.deposit.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Deposit {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long depositId;
    private BigDecimal amount;
    private Long billId;
    private String email;
    private OffsetDateTime creationDate;

    public Deposit(BigDecimal amount, Long billId, String email, OffsetDateTime creationDate) {
        this.amount = amount;
        this.billId = billId;
        this.email = email;
        this.creationDate = creationDate;
    }
}
