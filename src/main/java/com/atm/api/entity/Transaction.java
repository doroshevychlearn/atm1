package com.atm.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "date")
    private Date date;

    @OneToOne
    @JoinColumn(name = "sender")
    private User sender;

    @OneToOne
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @JoinColumn(name = "receiver")
    private User receiver;

    @Column(name = "amount")
    private Double amount;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private TransactionStatus status;
}
