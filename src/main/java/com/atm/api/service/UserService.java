package com.atm.api.service;

import com.atm.api.entity.User;
import com.atm.api.models.request.AuthModel;
import com.atm.api.models.response.NumberResponse;
import com.atm.api.models.response.UserDataResponse;

public interface UserService {

    User save(User user);

    void delete(User user);

    User findById(Long id);

    User findByEmail(String email);

    User findByNumber(Long number);

    NumberResponse signUp(AuthModel authModel);

    NumberResponse signIn(AuthModel authModel);

    UserDataResponse getUserData(User user);
}
