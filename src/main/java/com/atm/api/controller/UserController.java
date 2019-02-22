package com.atm.api.controller;

import com.atm.api.models.request.AuthModel;

import com.atm.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController extends AbstractController{

    @Autowired
    public UserController(UserService userService) {
        super(userService);
    }

    @PostMapping("/signUp")
    public ResponseEntity signUp(@RequestBody AuthModel model) {
        return ResponseEntity.ok(this.userService.signUp(model));
    }


    @PostMapping("/signIn")
    public ResponseEntity signIn(@RequestBody AuthModel model) {
        return ResponseEntity.ok(this.userService.signIn(model));
    }

    @GetMapping("/")
    public ResponseEntity getUserData(@RequestHeader("Customer") String id){
        return ResponseEntity.ok(this.userService.getUserData(getAuthUser(id)));
    }

}
