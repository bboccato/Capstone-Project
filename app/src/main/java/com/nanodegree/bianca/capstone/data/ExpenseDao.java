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

    @Query("SELECT * FROM expense_table WHERE uid IN (:expenseIds)")
    List<Expense> loadAllByIds(int[] expenseIds);

    @Query("SELECT * FROM expense_table WHERE summary LIKE :summary AND " +
            "value LIKE :value LIMIT 1")
    Expense findBySummary(String summary, String value);

    @Insert
    void insertAll(Expense... expenses);

    @Insert
    void insert(Expense expense);

    @Delete
    void delete(Expense expense);
}