package com.pairlearning.expensetracker.services;

import com.pairlearning.expensetracker.domain.Transaction;
import com.pairlearning.expensetracker.exceptions.EtResourceNotFoundException;

import java.util.List;

public interface TransactionService {
    Transaction addTransaction(Integer userId, Integer categoryId, Transaction transaction);

    List<Transaction> fetchAllTransactions(int userId, int categoryId) throws EtResourceNotFoundException;
}
