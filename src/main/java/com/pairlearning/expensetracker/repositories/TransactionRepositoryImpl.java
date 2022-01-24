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
import java.util.List;

@Repository
public class TransactionRepositoryImpl implements TransactionRepository{
    private static final String SQL_CREATE = "INSERT INTO ET_TRANSACTIONS (CATEGORY_ID, USER_ID, AMOUNT, NOTE, TRANSACTION_DATE) VALUES (?,?,?,?,?)";
    private static final String SQL_FIND_BY_ID =
            "SELECT TRANSACTION_ID, CATEGORY_ID, USER_ID, AMOUNT, NOTE, TRANSACTION_DATE" +
            " FROM ET_TRANSACTIONS" +
            " WHERE TRANSACTION_ID =  ?" +
            " AND CATEGORY_ID = ?" +
            " AND USER_ID = ?";
    private static final String SQL_FIND_ALL=
            "SELECT TRANSACTION_ID, CATEGORY_ID, USER_ID, AMOUNT, NOTE, TRANSACTION_DATE" +
            " FROM ET_TRANSACTIONS" +
            " WHERE CATEGORY_ID = ?" +
            " AND USER_ID = ?";
    private static final String SQL_UPDATE = "UPDATE ET_TRANSACTIONS" +
            " SET AMOUNT =?," +
            " NOTE = ?," +
            " TRANSACTION_DATE = ?" +
            " WHERE TRANSACTION_ID = ?" +
            " AND CATEGORY_ID = ?" +
            " AND USER_ID = ?";
    private static final String SQL_DELETE = "DELETE FROM ET_TRANSACTIONS WHERE USER_ID = ? AND CATEGORY_ID = ? AND TRANSACTION_ID = ?";
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public int create(Integer userId, Integer categoryId, Transaction transaction) {
        try{
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
            throw new EtResourceNotFoundException("Transaction not found");
        }
    }

    @Override
    public List<Transaction> findAll(int userId, int categoryId) throws EtResourceNotFoundException {
        try{
            return jdbcTemplate.query(SQL_FIND_ALL, new Object[]{ categoryId, userId}, transactionRowMapper);
        }catch(Exception e){
            throw new EtResourceNotFoundException("Transaction not found");
        }
    }

    @Override
    public void update(int userId, int categoryId, int transactionId, Transaction transaction) throws EtBadRequestException {
        try{
            int affected = jdbcTemplate.update(SQL_UPDATE,
                    new Object[]{transaction.getAmount(),
                            transaction.getNote(),
                            transaction.getTransactionDate(),
                            transactionId,
                            categoryId,
                            userId});
            if(affected==0)
                throw new EtBadRequestException("this transaction doesn't exist");
        }catch (Exception e){
            throw new EtBadRequestException("Invalid request");
        }
    }

    @Override
    public void delete(Integer userId, Integer categoryId, Integer transactionId) throws EtResourceNotFoundException {
        int count = jdbcTemplate.update(SQL_DELETE, new Object[]{userId, categoryId, transactionId});
        if(count==0)
            throw new EtResourceNotFoundException("Transaction not found");
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
