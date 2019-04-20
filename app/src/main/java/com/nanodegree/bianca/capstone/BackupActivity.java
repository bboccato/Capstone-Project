package com.nanodegree.bianca.capstone;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;

public class BackupActivity extends AppCompatActivity {
    private static final String TAG = "BackupActivity";
    Button mBackupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mBackupButton = findViewById(R.id.backup_button);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


}

