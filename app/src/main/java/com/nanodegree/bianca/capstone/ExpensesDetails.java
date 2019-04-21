package com.nanodegree.bianca.capstone;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.nanodegree.bianca.capstone.data.Expense;
import com.nanodegree.bianca.capstone.data.ExpenseDao;
import com.nanodegree.bianca.capstone.data.ExpenseRoomDatabase;

import java.util.List;

public class ExpensesDetails extends AppCompatActivity {
    private static final String TAG = "ExpensesDetails";
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

//    private List<ExpenseLocal> myDataset;

    private List<Expense> mExpenses;
    private ExpenseRoomDatabase mDb;
    private long expire;
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

        mDb = ExpenseRoomDatabase.getDatabase(this);
        new CurrentExpensesAsyncTask(this, mDb.expenseDao(), expire).execute();

        FloatingActionButton fab = findViewById(R.id.fab_add_expense);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddExpense.class);
                startActivity(intent);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new CurrentExpensesAsyncTask(this, mDb.expenseDao(), expire).execute();
    }

    public void setExpenses(List<Expense> expenses) {
        mExpenses = expenses;
    }

    private class CurrentExpensesAsyncTask extends AsyncTask<Void, Void, List<Expense>> {

        private Context mContext;
        private ExpenseDao mAsyncTaskDao;
        private long mLastExpireDate;

        CurrentExpensesAsyncTask(Context context, ExpenseDao dao, long lastExpireDate) {
            mContext = context;
            mAsyncTaskDao = dao;
            mLastExpireDate = lastExpireDate;
        }

        @Override
        protected List<Expense> doInBackground(Void... voids) {
            List<Expense> expenses = mAsyncTaskDao.getSinceLastExpire(mLastExpireDate);

            return expenses;
        }

        @Override
        protected void onPostExecute(List<Expense> expenses) {
            setExpenses(expenses);
            mAdapter = new ExpenseAdapter(mExpenses);
            recyclerView.setAdapter(mAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        }
    }
}
