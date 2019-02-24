package com.atm.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "date")
    private Date date;

    @OneToOne
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @JoinColumn(name = "sender")
    private User sender;

    @OneToOne
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @JoinColumn(name = "receiver")
    private User receiver;

    @NotNull
    @Column(name = "amount")
    private Double amount;

    @NotNull
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    @NotNull
    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private TransactionType type;
}
