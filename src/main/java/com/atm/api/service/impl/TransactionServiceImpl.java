package com.atm.api.service.impl;

import com.atm.api.entity.Transaction;
import com.atm.api.entity.TransactionStatus;
import com.atm.api.entity.User;
import com.atm.api.models.response.AmountResponse;
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
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;

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
        return null;
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
        if(Objects.isNull(amount) || amount < 1){
            transaction.setStatus(TransactionStatus.FAILED);
            throw new IllegalArgumentException("Bad amount!");
        }
        BigDecimal balance = new BigDecimal(user.getBalance()).add(new BigDecimal(amount));
        user.setBalance(balance.doubleValue());
        try{
            user = this.userService.save(user);
        }catch (Exception e){
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
        if(user.getBalance() > amount){
            BigDecimal balance = new BigDecimal(user.getBalance()).subtract(new BigDecimal(amount));
            user.setBalance(balance.doubleValue());
        }else{
            throw new IllegalArgumentException("There is not enough balance on the account");
        }
        try{
            user = this.userService.save(user);
        }catch (Exception e){
            transaction.setStatus(TransactionStatus.FAILED);
        }
        transaction.setStatus(TransactionStatus.SUCCEED);
        transaction = this.save(transaction);
        return transaction;
    }

    @Override
    public AmountResponse getMonthlyStatement(User user) {
        if(Objects.isNull(user)){
            throw new  IllegalArgumentException("User must be not null!");
        }
        LocalDate firstDate = YearMonth.now(ZoneId.of("Pacific/Auckland")).atDay(1).plusMonths(1);
        ZoneId z = ZoneId.of( "Europe/Kiev" );
        LocalDate secondDate = LocalDate.now(z);

//        this.transactionRepository.findAllBySenderOrReceiverAndDateBetween(user,
//                Date.from(firstDate.atStartOfDay(ZoneId.systemDefault()).toInstant()),
//                Date.from(secondDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        Collection<Transaction> operations = this.transactionRepository.findOperations(Date.from(firstDate.atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Date.from(secondDate.atStartOfDay(ZoneId.systemDefault()).toInstant()), user);
        return null;
    }
}
