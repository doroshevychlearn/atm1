package com.atm.api.service;

import com.atm.api.entity.Transaction;
import com.atm.api.entity.User;
import com.atm.api.models.response.StatementResponse;

import java.time.LocalDate;
import java.util.List;

public interface TransactionService {

    Transaction save(Transaction transaction);

    void delete(Transaction transaction);

    Transaction create(User sender, Long numberOfReceiver, Double amount);

    List<Transaction> findAllByUser(User user);

    List<Transaction> findAllWhereUserIsReceiver(User user);

    List<Transaction> findAllWhereUserIsSender(User user);

    Transaction createReplenish(User user, Double amount);

    Transaction createWithdraw(User user, Double amount);

    StatementResponse getMonthlyStatement(User user, LocalDate firstDate, LocalDate secondDate);
}
