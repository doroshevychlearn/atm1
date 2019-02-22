package com.atm.api.controller;

import com.atm.api.entity.User;
import com.atm.api.models.request.AmountRequest;
import com.atm.api.service.TransactionService;
import com.atm.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transaction")
public class TransactionController extends  AbstractController {

    private TransactionService transactionService;

    @Autowired
    public TransactionController(UserService userService, TransactionService transactionService) {
        super(userService);
        this.transactionService = transactionService;
    }

    @PostMapping("/replenish")
    public ResponseEntity createReplenish(@RequestBody AmountRequest amountRequest,
                                          @RequestHeader("Customer") String id){
        User user = getAuthUser(id);
        notNull(amountRequest);
        return ResponseEntity.ok(this.transactionService.createReplenish(user, amountRequest.getAmount()));
    }

    @PostMapping("/withdraw")
    public ResponseEntity createWithdraw(@RequestBody AmountRequest amountRequest,
                                          @RequestHeader("Customer") String id){
        User user = getAuthUser(id);
        notNull(amountRequest);
        return ResponseEntity.ok(this.transactionService.createWithdraw(user, amountRequest.getAmount()));
    }

    @GetMapping("/statement/monthly")
    public ResponseEntity getMonthlyStatement(@RequestHeader("Customer") String id){
        User user = getAuthUser(id);
        return ResponseEntity.ok(this.transactionService.getMonthlyStatement(user));
    }



}
