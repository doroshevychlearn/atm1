package com.atm.api.models.response;

import lombok.Data;

@Data
public class UserDataResponse {
    private Long id;
    private String email;
    private Long number;
    private Double balance;
}
