package com.nanodegree.bianca.capstone;

import android.Manifest;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.nanodegree.bianca.capstone.data.AppDatabase;

import java.util.ArrayList;
import java.util.List;

public class ExpensesDetails extends AppCompatActivity {
    private static final String TAG = "bib ExpensesDetails";
    private static final int MY_PERMISSIONS_REQUEST_READ_SMS = 1;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private List<ExpenseLocal> myDataset = new ArrayList<>();
//            {new ExpenseLocal("mercado", 10f, new Date()),
//            new ExpenseLocal("luz", 12f, new Date()),
//            new ExpenseLocal("telefone", 15f, new Date())};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenses_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        requestSmsReadPermission();

        recyclerView = findViewById(R.id.rv_expenses_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new ExpenseAdapter(myDataset);
        recyclerView.setAdapter(mAdapter);

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


        RecyclerView recyclerView = findViewById(R.id.rv_expenses_room_list);
        final ExpenseRoomAdapter adapter = new ExpenseRoomAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    private void requestSmsReadPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "parseSmsLog: READ_SMS permission not granted");
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_SMS},
                    MY_PERMISSIONS_REQUEST_READ_SMS);
        } else {
            Log.d(TAG, "parseSmsLog: READ_SMS permission granted :)");
            parseSmsLog();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_SMS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    parseSmsLog();
                } else {
                    // TODO permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }

    private void parseSmsLog() {
        // https://androidforums.com/threads/querying-the-sms-content-provider.158592/
        // filter by number and date

        Uri allMessages = Uri.parse("content://sms/inbox");
        //Cursor cursor = managedQuery(allMessages, null, null, null, null); Both are same
        Cursor cursor = this.getContentResolver().query(allMessages, null,
                null, null, null);

        int columnIndex = cursor.getColumnIndex("body");

        while (cursor.moveToNext()) {
            for (int i = 0; i < cursor.getColumnCount(); i++) {
                Log.d(TAG +  "- " + cursor.getColumnName(i) + "", cursor.getString(i) + "");
            }
            Log.d(TAG + "One row finished",
                    "**************************************************");
            String body = cursor.getString(columnIndex);
            myDataset.add(new ExpenseLocal(body));
        }

    }
}
