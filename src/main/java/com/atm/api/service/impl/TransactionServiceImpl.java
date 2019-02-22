package com.atm.api.service.impl;

import com.atm.api.entity.Transaction;
import com.atm.api.entity.TransactionStatus;
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
        transaction.setAmount(-amount);
        transaction.setDate(new Date());
        transaction.setReceiver(user);
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

        Collection<Transaction> operations = this.transactionRepository.findOperations(Date.from(firstDate.atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Date.from(secondDate.atStartOfDay(ZoneId.systemDefault()).toInstant()), user);
        List<Transaction> byUserIsSender = operations
                .stream()
                .filter(item -> Objects.nonNull(item.getSender())
                        && item.getSender().equals(user)
                        && item.getStatus().equals(TransactionStatus.SUCCEED))
                .collect(Collectors.toList());
        byUserIsSender.forEach(transaction -> {
            transaction.setAmount(-transaction.getAmount());
        });
        byUserIsSender.addAll(operations.
                stream().
                filter(item -> Objects.isNull(item.getSender())
                        && item.getReceiver().equals(user)
                        && item.getAmount() < 1
                        && item.getStatus().equals(TransactionStatus.SUCCEED))
                .collect(Collectors.toList()));
        double spent = byUserIsSender.stream().mapToDouble(item -> item.getAmount()).sum();
        List<Transaction> positiveNumbers = operations
                .stream()
                .filter(item -> Objects.isNull(item.getSender())
                        && Objects.nonNull(item.getReceiver())
                        && item.getReceiver().equals(user)
                        && item.getAmount() > 0
                        && item.getStatus().equals(TransactionStatus.SUCCEED))
                .collect(Collectors.toList());
        double obtained = positiveNumbers.stream().mapToDouble(item -> item.getAmount()).sum();
        return new StatementResponse(spent, obtained);
    }
}
