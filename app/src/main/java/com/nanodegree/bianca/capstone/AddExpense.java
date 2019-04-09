package com.nanodegree.bianca.capstone;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.nanodegree.bianca.capstone.data.Expense;
import com.nanodegree.bianca.capstone.data.ExpenseDao;
import com.nanodegree.bianca.capstone.data.ExpenseRoomDatabase;

import java.util.Date;

public class AddExpense extends AppCompatActivity {

    private Button mSaveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSaveButton = findViewById(R.id.button);
        final EditText summary = findViewById(R.id.et_summary_value);
        final EditText value = findViewById(R.id.et_total_value);
        Date date;


        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Expense expense = new Expense((new Date()).getTime(), summary.getText().toString(),
                            Float.valueOf(value.getText().toString()));
                    ExpenseRoomDatabase mDb = ExpenseRoomDatabase.getDatabase(getApplicationContext());
                    ExpenseDao dao = mDb.expenseDao();
                    new AddExpenseAsyncTask(dao, expense).execute();
                } catch (NumberFormatException nfe) {
                    Toast.makeText(getApplicationContext(), "NO GO", Toast.LENGTH_LONG).show();

                }
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
