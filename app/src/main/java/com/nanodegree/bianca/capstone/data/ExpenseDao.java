package com.nanodegree.bianca.capstone.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface ExpenseDao {
    @Query("SELECT * FROM expense_table ORDER BY value ASC")
    LiveData<List<Expense>> getAll();


    @Query("SELECT * FROM expense_table ORDER BY value ASC")
    List<Expense> getAllB();

    @Query("SELECT * FROM expense_table WHERE uid IN (:expenseIds)")
    List<Expense> loadAllByIds(int[] expenseIds);

    @Query("SELECT * FROM expense_table WHERE summary LIKE :summary AND " +
            "value LIKE :value LIMIT 1")
    Expense findBySummary(String summary, String value);

    @Query("SELECT * FROM expense_table ORDER BY date DESC LIMIT 1")
    Expense getLatest();

    @Query("SELECT * FROM expense_table WHERE date > :lastExpireDate ORDER BY date")
    List<Expense> getSinceLastExpire(long lastExpireDate);

    @Insert
    void insert(Expense expense);
}