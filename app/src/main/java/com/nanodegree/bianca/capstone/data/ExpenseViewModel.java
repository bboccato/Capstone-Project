package com.nanodegree.bianca.capstone.data;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import java.util.List;

public class ExpenseViewModel extends AndroidViewModel {
    private ExpenseRepository mRepository;

    private LiveData<List<Expense>> mAllExpenses;

    public ExpenseViewModel (Application application) {
        super(application);
        mRepository = new ExpenseRepository(application);
        mAllExpenses = mRepository.getAllExpenses();
    }

    LiveData<List<Expense>> getAllExpenses() { return mAllExpenses; }

    public void insert(Expense expense) { mRepository.insert(expense); }
}
