package com.nanodegree.bianca.capstone;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

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
import com.nanodegree.bianca.capstone.data.ExpenseViewModel;

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
    private ExpenseViewModel mExpenseViewModel;
    private List<Expense> mExpenses;

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

        mExpenseViewModel = ViewModelProviders.of(this).get(ExpenseViewModel.class);
        mExpenseViewModel.getAllExpenses().observe(this, new Observer<List<Expense>>() {
            @Override
            public void onChanged(List<Expense> expenses) {
                mExpenses = expenses;
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
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if (mFirebaseUser == null) return;
        CollectionReference allBills = db.collection(BILL_SMS_COLLECTION);
        DocumentReference userDb = allBills.document(mFirebaseUser.getUid());
        CollectionReference userExpenses = userDb.collection(EXPENSES_COLLECTION);
        for (Expense expense : mExpenses) {
            userExpenses.add(expense);
        }
        Bundle bundle = new Bundle();
        bundle.putString("UID", mFirebaseUser.getUid());
        mFirebaseAnalytics.logEvent("BACKUP", bundle);

    }
}
