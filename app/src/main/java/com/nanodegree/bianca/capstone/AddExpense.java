package com.nanodegree.bianca.capstone;

import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.nanodegree.bianca.capstone.data.Expense;
import com.nanodegree.bianca.capstone.data.ExpenseDao;
import com.nanodegree.bianca.capstone.data.ExpenseRoomDatabase;

import java.util.Date;
import java.util.List;

public class AddExpense extends AppCompatActivity {

    private Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mButton = findViewById(R.id.button);
        final EditText summary = findViewById(R.id.et_summary_value);
        final EditText value = findViewById(R.id.et_total_value);
        Date date;


        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Expense expense = new Expense((new Date()).getTime(), summary.getText().toString(),
                        Float.valueOf(value.getText().toString()));
                ExpenseRoomDatabase mDb = ExpenseRoomDatabase.getDatabase(getApplicationContext());
                ExpenseDao dao = mDb.expenseDao();
                new AddExpenseAsyncTask(dao, expense).execute();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        //TODO persist data
    }

    public void showDatePicker(View view) {
        DialogFragment dialogFragment = new DatePickerFragment();
        dialogFragment.show(getSupportFragmentManager(), "date picker");
    }
    private class AddExpenseAsyncTask extends AsyncTask<Void, Void, Void> {

        private ExpenseDao mAsyncTaskDao;
        private Expense mExpense;

        AddExpenseAsyncTask(ExpenseDao dao, Expense expense) {
            mAsyncTaskDao = dao;
            mExpense = expense;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mAsyncTaskDao.insert(mExpense);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            finish();
        }
    }
}
