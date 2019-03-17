package com.nanodegree.bianca.capstone;

import android.arch.persistence.room.Room;
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

import com.nanodegree.bianca.capstone.data.AppDatabase;
import com.nanodegree.bianca.capstone.data.Expense;
import com.nanodegree.bianca.capstone.data.ExpenseDao;
import com.nanodegree.bianca.capstone.data.ExpenseRoomDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ExpensesDetails extends AppCompatActivity {
    private static final String TAG = "bib ExpensesDetails";
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

//    private List<ExpenseLocal> myDataset;

    private List<Expense> mExpenses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenses_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        long expire = intent.getLongExtra("exp", 0);

//        myDataset = new ArrayList<>();
//        myDataset.add(new ExpenseLocal("mercado", 10f, new Date()));
//        myDataset.add(new ExpenseLocal("luz", 12f, new Date()));
//        myDataset.add(new ExpenseLocal("telefone", 15f, new Date()));
        recyclerView = findViewById(R.id.rv_expenses_list);
        recyclerView.setHasFixedSize(true);

        ExpenseRoomDatabase mDb = ExpenseRoomDatabase.getDatabase(this);
        new CurrentExpensesAsyncTask(this, mDb.expenseDao(), expire).execute();


        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "bill-sms-db").build();

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
//            List<Expense> expenses = (List<Expense>) mAsyncTaskDao.getAllB();
            List<Expense> expenses =
                    (List<Expense>) mAsyncTaskDao.getSinceLastExpire(mLastExpireDate);

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
