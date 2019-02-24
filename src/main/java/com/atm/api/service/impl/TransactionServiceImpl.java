package com.atm.api.service.impl;

import com.atm.api.entity.Transaction;
import com.atm.api.entity.TransactionStatus;
import com.atm.api.entity.TransactionType;
import com.atm.api.entity.User;
import com.atm.api.models.response.AmountResponse;
import com.atm.api.models.response.StatementResponse;
import com.atm.api.repository.TransactionRepository;
import com.atm.api.service.AbstactService;
import com.atm.api.service.TransactionService;
import com.atm.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TransactionServiceImpl extends AbstactService implements TransactionService {

    private TransactionRepository transactionRepository;
    private UserService userService;

    @Autowired
    public TransactionServiceImpl(TransactionRepository transactionRepository,
                                  UserService userService) {
        this.transactionRepository = transactionRepository;
        this.userService = userService;
    }

    @Override
    public Transaction save(Transaction transaction) {
        return this.transactionRepository.save(transaction);
    }

    @Override
    public void delete(Transaction transaction) {
        this.transactionRepository.delete(transaction);
    }

    @Override
    public Transaction create(User sender, Long numberOfReceiver, Double amount) {
        User receiver = this.userService.findByNumber(numberOfReceiver);
        if(Objects.isNull(sender) || Objects.isNull(receiver) || Objects.isNull(amount) || amount < 1){
            throw new IllegalArgumentException("Incorrect Data!");
        }
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setDate(new Date());
        transaction.setReceiver(receiver);
        transaction.setSender(sender);
        transaction.setType(TransactionType.TRANSFER);
        if (sender.getBalance() > amount) {
            BigDecimal balance = new BigDecimal(sender.getBalance()).subtract(new BigDecimal(amount));
            receiver.setBalance(new BigDecimal(receiver.getBalance()).add(new BigDecimal(amount)).doubleValue());
            sender.setBalance(balance.doubleValue());
        } else {
            throw new IllegalArgumentException("There is not enough balance on the account");
        }
        try {
            sender = this.userService.save(sender);
            receiver = this.userService.save(receiver);
        } catch (Exception e) {
            transaction.setStatus(TransactionStatus.FAILED);
        }
        transaction.setStatus(TransactionStatus.SUCCEED);
        transaction = this.save(transaction);
        return transaction;
    }

    @Override
    public List<Transaction> findAllByUser(User user) {
        return null;
    }

    @Override
    public List<Transaction> findAllWhereUserIsReceiver(User user) {
        return null;
    }

    @Override
    public List<Transaction> findAllWhereUserIsSender(User user) {
        return null;
    }

    @Override
    public Transaction createReplenish(User user, Double amount) {
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setDate(new Date());
        transaction.setReceiver(user);
        transaction.setType(TransactionType.REFILL);
        if (Objects.isNull(amount) || amount < 1) {
            transaction.setStatus(TransactionStatus.FAILED);
            throw new IllegalArgumentException("Bad amount!");
        }
        BigDecimal balance = new BigDecimal(user.getBalance()).add(new BigDecimal(amount));
        user.setBalance(balance.doubleValue());
        try {
            user = this.userService.save(user);
        } catch (Exception e) {
            transaction.setStatus(TransactionStatus.FAILED);
        }
        transaction.setStatus(TransactionStatus.SUCCEED);
        transaction = this.save(transaction);
        return transaction;
    }

    @Override
    public Transaction createWithdraw(User user, Double amount) {
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setDate(new Date());
        transaction.setReceiver(user);
        transaction.setType(TransactionType.WITHDRAWAL);
        if (user.getBalance() > amount) {
            BigDecimal balance = new BigDecimal(user.getBalance()).subtract(new BigDecimal(amount));
            user.setBalance(balance.doubleValue());
        } else {
            throw new IllegalArgumentException("There is not enough balance on the account");
        }
        try {
            user = this.userService.save(user);
        } catch (Exception e) {
            transaction.setStatus(TransactionStatus.FAILED);
        }
        transaction.setStatus(TransactionStatus.SUCCEED);
        transaction = this.save(transaction);
        return transaction;
    }

    @Override
    public StatementResponse getMonthlyStatement(User user, LocalDate firstDate, LocalDate secondDate) {
        if (Objects.isNull(user)) {
            throw new IllegalArgumentException("User must be not null!");
        }

        Date firstD = Date.from(firstDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date secondD = Date.from(secondDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        Collection<Transaction> operations = this.transactionRepository.findOperationsReceiver(firstD, secondD, user);
        operations.addAll(this.transactionRepository.findOperationsSender(firstD, secondD, user));
        double spent = this.getSpentStatement(user, operations);
        double obtained = this.getObtainedStatement(user, operations);
        return new StatementResponse(spent, obtained);
    }

    private double getSpentStatement(User user, Collection<Transaction> operations){
        double result = 0;
        if(!Objects.isNull(operations) && !operations.isEmpty()){
            result = operations
                    .stream()
                    .filter(t ->
                            (t.getStatus().equals(TransactionStatus.SUCCEED) && t.getType().equals(TransactionType.TRANSFER) && t.getSender().equals(user)) || (t.getStatus().equals(TransactionStatus.SUCCEED) && t.getType().equals(TransactionType.WITHDRAWAL) && t.getReceiver().equals(user))
                    )
                    .collect(Collectors.toSet())
                    .stream()
                    .mapToDouble(transaction -> transaction.getAmount()).sum();

        }
        return result;
    }

    private double getObtainedStatement(User user, Collection<Transaction> operations){
        double result = 0;
        if(!Objects.isNull(operations) && !operations.isEmpty()){
            result = operations
                    .stream()
                    .filter(t ->
                            (t.getStatus().equals(TransactionStatus.SUCCEED) && t.getType().equals(TransactionType.TRANSFER) && t.getReceiver().equals(user))
                                    || (t.getStatus().equals(TransactionStatus.SUCCEED) && t.getType().equals(TransactionType.REFILL) && t.getReceiver().equals(user))
                    )
                    .collect(Collectors.toSet())
                    .stream()
                    .mapToDouble(transaction -> transaction.getAmount()).sum();

        }
        return result;
    }
}
