package com.pairlearning.expensetracker.resources;

import com.pairlearning.expensetracker.domain.Transaction;
import com.pairlearning.expensetracker.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/categories/{categoryId}/transactions")
public class TransactionsResource {
    @Autowired
    TransactionService transactionService;
    @PostMapping
    public ResponseEntity<Transaction> createTransaction(HttpServletRequest request,
                                                         @PathVariable("categoryId") Integer categoryId,
                                                         @RequestBody Transaction transaction){
        int userId = (Integer) request
                .getAttribute("userId");

        Transaction trans = transactionService.addTransaction(userId,categoryId,transaction);
        return new ResponseEntity<>(trans, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<Transaction>> getAllTransactions(HttpServletRequest request,
                                                                @PathVariable("categoryId") Integer categoryId){
        int userId = (Integer) request.getAttribute("userId");

        return new ResponseEntity<>(transactionService.fetchAllTransactions(userId, categoryId), HttpStatus.OK);
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<Transaction> getTransactionById(HttpServletRequest request,
                                                            @PathVariable("categoryId") Integer categoryId,
                                                            @PathVariable("transactionId") Integer transactionId){
        int userId = (Integer) request.getAttribute("userId");
        return new ResponseEntity<>(
                transactionService.fetchTransactionById(userId,categoryId,transactionId),
                HttpStatus.OK);
    }

    @PutMapping("/{transactionId}")
    public ResponseEntity<Map<String, Boolean>> updateTransaction(HttpServletRequest request,
                                                                @PathVariable("categoryId") Integer categoryId,
                                                                @PathVariable("transactionId") Integer transactionId){
        //TODO
        return null;
    }
    @DeleteMapping("/{transactionId}")
    public ResponseEntity<Map<String, Boolean>> deleteTransaction(HttpServletRequest request,
                                                                  @PathVariable("categoryId") Integer categoryId,
                                                                  @PathVariable("transactionId") Integer transactionId){
        //TODO
        return null;
    }

}
