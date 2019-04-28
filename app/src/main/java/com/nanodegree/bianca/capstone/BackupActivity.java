package com.nanodegree.bianca.capstone;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nanodegree.bianca.capstone.data.Expense;
import com.nanodegree.bianca.capstone.data.ExpenseDao;
import com.nanodegree.bianca.capstone.data.ExpenseRoomDatabase;

import java.util.Arrays;
import java.util.List;

public class BackupActivity extends AppCompatActivity {
    private static final String TAG = "BackupActivity";
    private static final String BILL_SMS_COLLECTION = "bill-sms-expenses";
    private static final String EXPENSES_COLLECTION = "BackupActivity";
    private static final int RC_SIGN_IN = 1;

    private Button mBackupButton;
    private FirebaseUser mFirebaseUser;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mBackupButton = findViewById(R.id.backup_button);
        mBackupButton.setOnClickListener(v -> signInFirebase());
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                setupFirebase();
            }
        }
    }

    private void signInFirebase() {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build());

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }

    private void setupFirebase() {
        ExpenseRoomDatabase mDb = ExpenseRoomDatabase.getDatabase(this);
        new CurrentExpensesAsyncTask(mDb.expenseDao()).execute();
    }


    private class CurrentExpensesAsyncTask extends AsyncTask<Void, Void, List<Expense>> {
        private ExpenseDao mAsyncTaskDao;
        FirebaseFirestore db = FirebaseFirestore.getInstance();


        CurrentExpensesAsyncTask(ExpenseDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected List<Expense> doInBackground(Void... voids) {
            List<Expense> expenses = mAsyncTaskDao.getAll();

            return expenses;
        }

        @Override
        protected void onPostExecute(List<Expense> expenses) {
            if (mFirebaseUser == null) return;
            CollectionReference allBills = db.collection(BILL_SMS_COLLECTION);
            DocumentReference userDb = allBills.document(mFirebaseUser.getUid());
            CollectionReference userExpenses = userDb.collection(EXPENSES_COLLECTION);
            for (Expense expense : expenses) {
                userExpenses.add(expense);
            }
            Bundle bundle = new Bundle();
            bundle.putString("UID", mFirebaseUser.getUid());
            mFirebaseAnalytics.logEvent("BACKUP", bundle);
        }
    }
}
