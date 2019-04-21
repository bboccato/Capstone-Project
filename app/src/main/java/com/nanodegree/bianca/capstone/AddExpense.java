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
    private EditText mSummary;
    private EditText mValue;
    private boolean mIsSave = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSaveButton = findViewById(R.id.button);
        mSummary = findViewById(R.id.et_summary_value);
        mValue = findViewById(R.id.et_total_value);


        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsSave = true;
                addExpense();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mIsSave) return;
        addExpense();
    }

    public void showDatePicker(View view) {
        DialogFragment dialogFragment = new DatePickerFragment();
        dialogFragment.show(getSupportFragmentManager(), "date picker");
    }

    private void addExpense() {
        try {
            Expense expense = new Expense((new Date()).getTime(), mSummary.getText().toString(),
                    Float.valueOf(mValue.getText().toString()));
            ExpenseRoomDatabase mDb = ExpenseRoomDatabase.getDatabase(getApplicationContext());
            ExpenseDao dao = mDb.expenseDao();
            new AddExpenseAsyncTask(dao, expense).execute();
        } catch (NumberFormatException nfe) {
            Toast.makeText(getApplicationContext(),
                    mIsSave ? R.string.add_expense_error_save_btn :
                            R.string.add_expense_error_pause,
                    Toast.LENGTH_SHORT).show();
        }
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
            setResult(RESULT_OK);
            finish();
            Toast.makeText(getApplicationContext(), R.string.add_expense_success,
                    Toast.LENGTH_SHORT).show();

        }
    }
}
