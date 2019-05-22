package com.nanodegree.bianca.capstone;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import com.nanodegree.bianca.capstone.data.Expense;
import com.nanodegree.bianca.capstone.data.ExpenseDao;
import com.nanodegree.bianca.capstone.data.ExpenseRoomDatabase;
import com.nanodegree.bianca.capstone.data.ExpenseViewModel;

import java.util.List;

public class ExpensesDetails extends AppCompatActivity {
    private static final String TAG = "ExpensesDetails";
    private static final String LIST_STATE_KEY = "list_state";
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private List<Expense> mExpenses;
    private long expire;
    private Parcelable mListState;
    private ExpenseViewModel mExpenseViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenses_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        expire = intent.getLongExtra("exp", 0);

        recyclerView = findViewById(R.id.rv_expenses_list);
        recyclerView.setHasFixedSize(true);

        mExpenseViewModel = ViewModelProviders.of(this).get(ExpenseViewModel.class);
        mExpenseViewModel.getSinceLastExpire(expire).observe(this, new Observer<List<Expense>>() {
            @Override
            public void onChanged(List<Expense> expenses) {
                setExpenses(expenses);
                mAdapter = new ExpenseAdapter(mExpenses);
                recyclerView.setAdapter(mAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                if (mListState != null) {
                    recyclerView.getLayoutManager().onRestoreInstanceState(mListState);
                }
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab_add_expense);
        fab.setOnClickListener(view -> {
            Intent intent1 = new Intent(getApplicationContext(), AddExpense.class);
            startActivity(intent1);
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putParcelable(LIST_STATE_KEY,
                recyclerView.getLayoutManager().onSaveInstanceState());
    }

    @Override
    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);

        // Retrieve list state and list/item positions
        if (state != null)
            mListState = state.getParcelable(LIST_STATE_KEY);
    }

    public void setExpenses(List<Expense> expenses) {
        mExpenses = expenses;
    }
}
