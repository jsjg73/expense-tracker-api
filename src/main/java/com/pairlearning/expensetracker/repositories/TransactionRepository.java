package com.pairlearning.expensetracker.repositories;

import com.pairlearning.expensetracker.domain.Transaction;
import com.pairlearning.expensetracker.exceptions.EtBadRequestException;
import com.pairlearning.expensetracker.exceptions.EtResourceNotFoundException;

public interface TransactionRepository {

    int create(Integer userId, Integer categoryId, Transaction transaction)throws EtBadRequestException;

    Transaction findById(int userId, int categoryId, int transactionId)throws EtResourceNotFoundException;
}
