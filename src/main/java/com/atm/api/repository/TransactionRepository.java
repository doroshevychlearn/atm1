package com.atm.api.repository;

import com.atm.api.entity.Transaction;
import com.atm.api.entity.TransactionStatus;
import com.atm.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Date;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Transaction findAllByStatus(TransactionStatus transactionStatus);

    List<Transaction> findAllByReceiver(User user);

    List<Transaction> findAllBySender(User user);

    //    @Query("select c from Country c where lower(c.name) = lower(:countryName)")
//    List<Transaction> findAllBySenderOrReceiverAndDateBetween(User user, Date date, Date date1);
//    List<Transaction> findAllBySenderOrReceiverAndDateBetween(User user, );
    @Query("SELECT t FROM Transaction t WHERE t.date BETWEEN :fromm AND :to AND t.receiver = :user OR t.sender = :user")
    Collection<Transaction> findOperationsReceiver(@Param("fromm") Date startDay,
                                           @Param("to") Date endDay,
                                           @Param("user") User user);

    @Query("SELECT t FROM Transaction t WHERE t.date BETWEEN :fromm AND :to AND t.sender = :user OR t.receiver = :user")
    Collection<Transaction> findOperationsSender(@Param("fromm") Date startDay,
                                           @Param("to") Date endDay,
                                           @Param("user") User user);
}
