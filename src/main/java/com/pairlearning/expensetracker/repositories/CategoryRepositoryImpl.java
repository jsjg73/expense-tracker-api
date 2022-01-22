package com.pairlearning.expensetracker.repositories;

import com.pairlearning.expensetracker.domain.Category;
import com.pairlearning.expensetracker.exceptions.EtBadRequestException;
import com.pairlearning.expensetracker.exceptions.EtResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class CategoryRepositoryImpl implements CategoryRepository{
    private static final String SQL_CREATE = "INSERT INTO ET_CATEGORIES (USER_ID, TITLE, DESCRIPTION) VALUES(?,?,?)";
    private static final String SQL_FIND_BY_ID ="SELECT C.CATEGORY_ID, C.USER_ID, C.TITLE, C.TITLE, C.DESCRIPTION, " +
            "IFNULL(SUM(T.AMOUNT),0) TOTAL_EXPENSE " +
            "FROM ET_TRANSACTIONS T RIGHT OUTER JOIN ET_CATEGORIES C ON C.CATEGORY_ID = T.CATEGORY_ID " +
            "WHERE C.USER_ID = ? AND C.CATEGORY_ID = ? GROUP BY C.CATEGORY_ID";
    private  static final String SQL_FIND_ALL = "SELECT USER_ID, CATEGORY_ID, TITLE, DESCRIPTION FROM ET_CATEGORIES WHERE USER_ID = ? ORDER BY CATEGORY_ID ";
    @Autowired
    JdbcTemplate jdbcTemplate;
    private RowMapper<Category> categoryRowMapper =(((rs, rowNum) -> {
        return new Category(rs.getInt("CATEGORY_ID"),
                rs.getInt("USER_ID"),
                rs.getString("TITLE"),
                rs.getString("DESCRIPTION"),
                rs.getDouble("TOTAL_EXPENSE"));
    })) ;
    private RowMapper<Category> categoryAllRowMapper =(((rs, rowNum) -> {
        return new Category(rs.getInt("CATEGORY_ID"),
                rs.getInt("USER_ID"),
                rs.getString("TITLE"),
                rs.getString("DESCRIPTION"),
                0.0);
    })) ;
    @Override
    public List<Category> findAll(Integer userId) throws EtResourceNotFoundException {
        try{
            return jdbcTemplate.query(SQL_FIND_ALL, new Object[]{userId},categoryAllRowMapper);
        }catch (Exception e){
            e.printStackTrace();
            throw new EtResourceNotFoundException("Category not found");
        }
    }

    @Override
    public Category findById(Integer userId, Integer categoryId) throws EtResourceNotFoundException {
        try{
            return jdbcTemplate.queryForObject(SQL_FIND_BY_ID, new Object[]{userId, categoryId}, categoryRowMapper);
        }catch(Exception e){
            throw new EtResourceNotFoundException("Category not found");
        }
    }

    @Override
    public Integer create(Integer userId, String title, String description) throws EtBadRequestException {
        try{
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(con -> {
                PreparedStatement ps = con.prepareStatement(SQL_CREATE, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, userId);
                ps.setString(2, title);
                ps.setString(3, description);
                return ps;
            }, keyHolder);
            return keyHolder.getKey().intValue();
        }catch (Exception e){
            throw new EtBadRequestException("Invalid request");
        }
    }

    @Override
    public void update(Integer userId, Integer categoryId, Category category) throws EtBadRequestException {

    }

    @Override
    public void removeById(Integer userId, Integer categoryId) {

    }
}