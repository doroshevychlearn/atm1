package com.atm.api.service;

import com.atm.api.entity.User;
import com.atm.api.models.request.AuthModel;

import java.util.Objects;

public abstract class AbstactService {

    protected AuthModel isCorrectAuthModel(AuthModel authModel){
        if(Objects.isNull(authModel) || Objects.isNull(authModel.getEmail())
                || !authModel.getEmail().contains("@") || !authModel.getEmail().contains(".")){
            throw new IllegalArgumentException("Incorrectly entered data!");
        }
        return authModel;
    }

    protected User userIsNotNull(User user){
        if(Objects.isNull(user)){
            throw new IllegalArgumentException("User must be not null!");
        }
        return user;
    }
}
