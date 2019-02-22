package com.atm.api.controller;

import com.atm.api.entity.User;
import com.atm.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;

public abstract class AbstractController {

    protected UserService userService;

    @Autowired
    public AbstractController(UserService userService) {
        this.userService = userService;
    }

    protected User getAuthUser(String id){
        Long userId = Long.parseLong(id);
        User user = this.userService.findById(userId);
        if(Objects.isNull(user)){
            throw new IllegalArgumentException("User does not exist!");
        }
        return user;
    }

    protected Boolean notNull(Object o){
        if(Objects.nonNull(o)){
            return true;
        }
        throw new IllegalArgumentException("Object is null!");
    }
}
