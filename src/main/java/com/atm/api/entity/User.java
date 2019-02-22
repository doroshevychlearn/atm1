package com.atm.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "number")
    private Long number;

    @Column(name = "emailSignIn")
    private String email;

    @Column(name = "balance")
    private Double balance;

    @JsonIgnore
    @Column(name = "password")
    private String password;
}
