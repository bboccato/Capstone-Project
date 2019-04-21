package com.nanodegree.bianca.capstone;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.nanodegree.bianca.capstone.data.Expense;
import com.nanodegree.bianca.capstone.data.ExpenseDao;
import com.nanodegree.bianca.capstone.data.ExpenseRoomDatabase;

import java.util.Arrays;
import java.util.List;

public class BackupActivity extends AppCompatActivity {
    private static final String TAG = "BackupActivity";
    private static final int RC_SIGN_IN = 1;
    Button mBackupButton;
    private ExpenseRoomDatabase mDb;
    FirebaseUser mFirebaseUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mBackupButton = findViewById(R.id.backup_button);
        mBackupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInFirebase();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                setupFirebase();
                // ...
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
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
        mDb = ExpenseRoomDatabase.getDatabase(this);
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
            if (mFirebaseUser == null) {
                Log.d("bib", "no user");
                return;
            }
            Log.d(TAG, "onPostExecute: bib " + expenses.size());
            CollectionReference allBills = db.collection("bill-sms-expenses");
            DocumentReference userDb = allBills.document(mFirebaseUser.getUid());
            CollectionReference userExpenses = userDb.collection("expenses");
            for (Expense expense : expenses) {
                Log.d(TAG, "onPostExecute: bib " + expense.date);
                userExpenses.add(expense);
            }
        }
    }

    private void retrieveFirebase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if (mFirebaseUser == null) {
            Log.d("bib", "no user");
            return;
        }
        CollectionReference allBills = db.collection("bill-sms-expenses");
        DocumentReference userDb = allBills.document(mFirebaseUser.getUid());
        CollectionReference userExpenses = userDb.collection("expenses");
        Task<QuerySnapshot> expenses =
                db.collection("bill-sms-expenses").document(mFirebaseUser.getUid()).collection(
                        "expenses").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: bib " + task.getResult().getDocuments());
                        } else {
                            Log.d(TAG, "onComplete: bib task not succeeded");
                        }

                    }
                });

    }
}
