package com.nanodegree.bianca.capstone;

import android.Manifest;
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
import java.util.Date;
import java.util.List;

public class ExpensesDetails extends AppCompatActivity {
    private static final String TAG = "bib ExpensesDetails";
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


        // Room https://codelabs.developers.google.com/codelabs/android-room-with-a-view/#11
        RecyclerView recyclerView = findViewById(R.id.rv_expenses_room_list);
        final ExpenseRoomAdapter adapter = new ExpenseRoomAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }
}
