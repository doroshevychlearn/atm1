package com.atm.api.service.impl;

import com.atm.api.entity.User;
import com.atm.api.models.request.AuthModel;
import com.atm.api.models.response.NumberResponse;
import com.atm.api.models.response.UserDataResponse;
import com.atm.api.repository.UserRepository;
import com.atm.api.service.AbstactService;
import com.atm.api.service.UserService;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UserServiceImpl extends AbstactService implements UserService {

    private UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User save(User user) {
        return this.userRepository.save(user);
    }



    @Override
    public void delete(User user) {
        this.userRepository.delete(user);
    }

    @Override
    public User findById(Long id) {
        return userRepository.getOne(id);
    }

    @Override
    public User findByEmail(String email) {
        return this.userRepository.findByEmail(email);
    }

    @Override
    public User findByNumber(Long number) {
        return this.userRepository.findByNumber(number);
    }

    @Override
    public NumberResponse signUp(AuthModel authModel) {
        authModel = isCorrectAuthModel(authModel);
        String email = authModel.getEmail();
        User user = this.findByEmail(email);
        if(Objects.nonNull(user)){
            throw new IllegalArgumentException("This email address is already registered!");
        }else{
            user = new User();
            user.setEmail(email);
            user.setNumber(this.generateUniqueNumber());
            user.setBalance(0d);
            user = this.save(user);
        }
        return new NumberResponse(user.getId());
    }

    @Override
    public NumberResponse signIn(AuthModel authModel) {
        authModel = isCorrectAuthModel(authModel);
        String email = authModel.getEmail();
        User user = this.findByEmail(email);
        if(Objects.nonNull(user)){
            return new NumberResponse(user.getId());
        }
        throw new IllegalArgumentException("User does not exist!");
    }

    @Override
    public UserDataResponse getUserData(User user) {
        user = userIsNotNull(user);
        UserDataResponse userDataResponse = new UserDataResponse();
        BeanUtils.copyProperties(user,userDataResponse);
        return userDataResponse;
    }

    private Long generateUniqueNumber(){
        Long number = RandomUtils.nextLong(100000, 99999999);
        User user = this.findByNumber(number);
        if(Objects.nonNull(user)){
            return this.generateUniqueNumber();
        }
        return number;
    }

}
