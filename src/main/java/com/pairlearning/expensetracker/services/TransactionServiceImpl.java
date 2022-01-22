package com.pairlearning.expensetracker.services;

import com.pairlearning.expensetracker.domain.Transaction;
import com.pairlearning.expensetracker.exceptions.EtResourceNotFoundException;
import com.pairlearning.expensetracker.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService{
    @Autowired
    TransactionRepository transactionRepository;
    @Override
    public Transaction addTransaction(Integer userId, Integer categoryId, Transaction transaction) {
        int transactionId = transactionRepository.create(userId,categoryId,transaction);
        return transactionRepository.findById(userId, categoryId, transactionId);
    }

    @Override
    public List<Transaction> fetchAllTransactions(int userId, int categoryId) throws EtResourceNotFoundException {
        return transactionRepository.findAll(userId, categoryId);
    }

    @Override
    public Transaction fetchTransactionById(int userId, int categoryId, int transactionId) throws EtResourceNotFoundException {
        return transactionRepository.findById(userId, categoryId, transactionId);
    }


}
