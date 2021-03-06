package com.pairlearning.expensetracker.repositories;

import com.pairlearning.expensetracker.domain.Transaction;
import com.pairlearning.expensetracker.exceptions.EtBadRequestException;
import com.pairlearning.expensetracker.exceptions.EtResourceNotFoundException;

import java.util.List;

public interface TransactionRepository {

    int create(Integer userId, Integer categoryId, Transaction transaction)throws EtBadRequestException;

    Transaction findById(int userId, int categoryId, int transactionId)throws EtResourceNotFoundException;

    List<Transaction> findAll(int userId, int categoryId)throws EtResourceNotFoundException;

    void update(int userId, int categoryId, int transactionId, Transaction transaction) throws EtBadRequestException;

    void delete(Integer userId, Integer categoryId, Integer transactionId)throws EtResourceNotFoundException;
}
