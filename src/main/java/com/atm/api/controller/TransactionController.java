package com.atm.api.controller;

import com.atm.api.entity.User;
import com.atm.api.models.request.AmountRequest;
import com.atm.api.models.request.RemitRequest;
import com.atm.api.service.TransactionService;
import com.atm.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.Objects;

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

    @PostMapping("/remit")
    public ResponseEntity createRemit(@RequestBody RemitRequest remitRequest,
                                         @RequestHeader("Customer") String id){
        User sender = getAuthUser(id);
        notNull(remitRequest);
        if(Objects.isNull(remitRequest.getAmount()) || Objects.isNull(remitRequest.getNumber()) || remitRequest.getAmount() < 1 || remitRequest.getNumber() < 1){
            throw new IllegalArgumentException("Incorrect Data!");
        }
        return ResponseEntity.ok(this.transactionService.create(sender, remitRequest.getNumber(), remitRequest.getAmount()));
    }

    @GetMapping("/statement/monthly")
    public ResponseEntity getMonthlyStatement(@RequestHeader("Customer") String id){
        User user = getAuthUser(id);
        LocalDate firstDate = LocalDate.now().minusMonths(1);
        LocalDate secondDate = LocalDate.now();
        return ResponseEntity.ok(this.transactionService.getMonthlyStatement(user, firstDate, secondDate));
    }

    @GetMapping("/statement/yearly")
    public ResponseEntity getYearlyStatement(@RequestHeader("Customer") String id){
        User user = getAuthUser(id);
        LocalDate firstDate = LocalDate.now().minusYears(1);
        LocalDate secondDate = LocalDate.now();
        return ResponseEntity.ok(this.transactionService.getMonthlyStatement(user, firstDate, secondDate));
    }



}
