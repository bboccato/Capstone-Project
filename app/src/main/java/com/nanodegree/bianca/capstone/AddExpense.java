package com.nanodegree.bianca.capstone;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.nanodegree.bianca.capstone.data.Expense;
import com.nanodegree.bianca.capstone.data.ExpenseDao;
import com.nanodegree.bianca.capstone.data.ExpenseRoomDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddExpense extends AppCompatActivity {
    public static final String SUMMARY = "summary";
    private static final String VALUE = "value";
    private static final String DATE = "date";
    private Button mSaveButton;
    private EditText mSummary;
    private EditText mValue;
    private Button mDate;
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
        mDate = findViewById(R.id.button_date);

        mSaveButton.setOnClickListener(v -> {
            mIsSave = true;
            addExpense();
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mIsSave) return;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mIsSave) return;
        if (isFinishing()) addExpense();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString(SUMMARY, mSummary.getText().toString());
        savedInstanceState.putString(VALUE, mValue.getText().toString());
        savedInstanceState.putString(DATE, mDate.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);

        // Retrieve values
        if (state != null) {
            if (state.containsKey(SUMMARY))
                mSummary.setText(state.getString(SUMMARY));
            if (state.containsKey(VALUE))
                mValue.setText(state.getString(VALUE));
            if (state.containsKey(DATE))
                mDate.setText(state.getString(DATE));
        }
    }

    public void showDatePicker(View view) {
        DialogFragment dialogFragment = new DatePickerFragment();
        dialogFragment.show(getSupportFragmentManager(), "date picker");
    }

    private void addExpense() {
        String dateString = mDate.getText().toString();
        long dateLong;
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
        try {
            dateLong = format.parse(dateString).getTime();
        } catch (ParseException e) {
            dateLong = (new Date()).getTime();
        }

        try {
            Expense expense = new Expense(dateLong, mSummary.getText().toString(),
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
