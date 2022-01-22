package com.pairlearning.expensetracker.repositories;

import com.pairlearning.expensetracker.domain.Transaction;
import com.pairlearning.expensetracker.exceptions.EtBadRequestException;
import com.pairlearning.expensetracker.exceptions.EtResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;

@Repository
public class TransactionRepositoryImpl implements TransactionRepository{
    private static final String SQL_CREATE = "INSERT INTO ET_TRANSACTIONS (CATEGORY_ID, USER_ID, AMOUNT, NOTE, TRANSACTION_DATE) VALUES (?,?,?,?,?)";
    private static final String SQL_FIND_BY_ID =
            "SELECT TRANSACTION_ID, CATEGORY_ID, USER_ID, AMOUNT, NOTE, TRANSACTION_DATE" +
            " FROM ET_TRANSACTIONS" +
            " WHERE TRANSACTION_ID =  ?" +
            " AND CATEGORY_ID = ?" +
            " AND USER_ID = ?";
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public int create(Integer userId, Integer categoryId, Transaction transaction) {
        try{
//            jdbcTemplate.update(SQL_CREATE, new Object[]{categoryId, userId, transaction.getAmount(), transaction.getNote(), transaction.getTransactionDate()},);
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(con->{
                PreparedStatement ps = con.prepareStatement(SQL_CREATE, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1,categoryId);
                ps.setInt(2, userId);
                ps.setDouble(3, transaction.getAmount());
                ps.setString(4, transaction.getNote());
                ps.setLong(5, transaction.getTransactionDate());
                return ps;
            }, keyHolder);
            return keyHolder.getKey().intValue();
        }catch(Exception e){
            e.printStackTrace();
            throw new EtBadRequestException("Invalid request");
        }
    }

    @Override
    public Transaction findById(int userId, int categoryId, int transactionId) throws EtResourceNotFoundException {
        try{
            return jdbcTemplate.queryForObject(SQL_FIND_BY_ID, new Object[]{transactionId, categoryId, userId}, transactionRowMapper);
        }catch (Exception e){
            throw new EtResourceNotFoundException("transaction not found");
        }
    }

    private RowMapper<Transaction> transactionRowMapper = ((rs,rowNum)->{
       return new Transaction(rs.getInt("TRANSACTION_ID"),
               rs.getInt("CATEGORY_ID"),
               rs.getInt("USER_ID"),
               rs.getDouble("AMOUNT"),
               rs.getString("NOTE"),
               rs.getLong("TRANSACTION_DATE")
       ) ;
    });
}