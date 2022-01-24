package com.pairlearning.expensetracker.services;

import com.pairlearning.expensetracker.domain.Transaction;
import com.pairlearning.expensetracker.exceptions.EtBadRequestException;
import com.pairlearning.expensetracker.exceptions.EtResourceNotFoundException;

import java.util.List;

public interface TransactionService {
    Transaction addTransaction(Integer userId, Integer categoryId, Transaction transaction);

    List<Transaction> fetchAllTransactions(int userId, int categoryId) throws EtResourceNotFoundException;

    Transaction fetchTransactionById(int userId, int categoryId, int transactionId) throws EtResourceNotFoundException;

    void update(int userId, int categoryId, int transactionId, Transaction transaction) throws EtBadRequestException;

    void delete(Integer userId, Integer categoryId, Integer transactionId)throws EtResourceNotFoundException;
}
