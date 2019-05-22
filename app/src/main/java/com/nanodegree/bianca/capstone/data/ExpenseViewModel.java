package com.nanodegree.bianca.capstone.data;

import android.app.Application;

import java.util.List;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public class ExpenseViewModel extends AndroidViewModel {
    private ExpenseRepository mRepository;
    private LiveData<List<Expense>> mAllExpenses;

    public ExpenseViewModel (Application application) {
        super(application);
        mRepository = new ExpenseRepository(application);
        mAllExpenses = mRepository.getAllExpenses();
    }

    public LiveData<List<Expense>> getAllExpenses() { return mAllExpenses; }

    public LiveData<Expense> getLatest() { return mRepository.getLatest(); }

    public LiveData<List<Expense>> getSinceLastExpire(long lastExpire) {
        return mRepository.getSinceLastExpire(lastExpire);
    }
}
