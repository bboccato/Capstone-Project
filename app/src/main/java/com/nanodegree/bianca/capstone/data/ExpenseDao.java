package com.nanodegree.bianca.capstone.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ExpenseDao {
    @Query("SELECT * FROM expense_table ORDER BY value ASC")
    LiveData<List<Expense>> getAll();

    @Query("SELECT * FROM expense_table ORDER BY date DESC LIMIT 1")
    LiveData<Expense> getLatest();

    @Query("SELECT * FROM expense_table WHERE date > :lastExpireDate ORDER BY date")
    LiveData<List<Expense>> getSinceLastExpire(long lastExpireDate);

    @Insert
    void insert(Expense expense);
}