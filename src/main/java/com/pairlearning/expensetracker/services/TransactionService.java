package com.pairlearning.expensetracker.services;

import com.pairlearning.expensetracker.domain.Transaction;

public interface TransactionService {
    Transaction addTransaction(Integer userId, Integer categoryId, Transaction transaction);
}
