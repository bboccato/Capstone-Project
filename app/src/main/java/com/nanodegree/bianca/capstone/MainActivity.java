package com.nanodegree.bianca.capstone;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView budgetStatus = findViewById(R.id.iv_pie_chart_budget);
        budgetStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), BudgetSetting.class);
                startActivity(intent);
            }
        });

        ImageView expenseStatus = findViewById(R.id.iv_pie_chart_expense);
        expenseStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ExpensesDetails.class);
                startActivity(intent);
            }
        });
    }
}
