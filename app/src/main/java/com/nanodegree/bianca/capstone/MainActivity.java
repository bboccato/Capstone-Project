package com.nanodegree.bianca.capstone;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;
import com.razerdp.widget.animatedpieview.AnimatedPieView;
import com.razerdp.widget.animatedpieview.AnimatedPieViewConfig;
import com.razerdp.widget.animatedpieview.callback.OnPieSelectListener;
import com.razerdp.widget.animatedpieview.data.IPieInfo;
import com.razerdp.widget.animatedpieview.data.SimplePieInfo;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    public static final String EXPENSES = "Expenses";
    public static final String REMAINING = "Remaining";
    private PublisherAdView mPublisherAdView;
    private AnimatedPieView mAnimatedPieView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView budgetStatus = findViewById(R.id.iv_pie_chart_budget);
        budgetStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startBudgetSettingActivity();
            }
        });

        ImageView expenseStatus = findViewById(R.id.iv_pie_chart_expense);
        expenseStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startExpenseDetailsActivity();
            }
        });

        setupPieChart();
        setupAdBanner();

    }

    /* SETUP */
    private void setupPieChart() {
        mAnimatedPieView = findViewById(R.id.animatedPieView);
        AnimatedPieViewConfig config = new AnimatedPieViewConfig();
        config.startAngle(-90)
                .strokeWidth(100)
                .drawText(true)
                .textSize(40)
                .selectListener(new OnPieSelectListener() {
                    @Override
                    public void onSelectPie(@NonNull IPieInfo pieInfo, boolean isFloatUp) {
                        switch (pieInfo.getDesc()) {
                            case EXPENSES:
                                startExpenseDetailsActivity();
                                break;
                            case REMAINING:
                                startBudgetSettingActivity();
                                break;
                            default:
                                break;
                        }
                    }
                })
                .addData(new SimplePieInfo(getTotalExpenses(), Color.RED, EXPENSES), false)
                .addData(new SimplePieInfo(getRemainingBudget(), Color.GREEN, REMAINING), false)
                .duration(750);
        mAnimatedPieView.applyConfig(config);
        mAnimatedPieView.start();
    }

    private void setupAdBanner() {
        mPublisherAdView = findViewById(R.id.publisherAdView);
        PublisherAdRequest adRequest = new PublisherAdRequest.Builder().build();
        mPublisherAdView.loadAd(adRequest);
    }

    /* AUX */
    private float getTotalExpenses() {
        //TODO replace with appropriate value
        return 100f;
    }

    private float getTotalBudget() {
        //TODO replace with appropriate value
        return 400f - getTotalExpenses();
    }

    private float getRemainingBudget() {
        return getTotalBudget() - getTotalExpenses();
    }

    private void startBudgetSettingActivity() {
        Intent intent = new Intent(getApplicationContext(), BudgetSetting.class);
        startActivity(intent);
    }

    private void startExpenseDetailsActivity() {
        Intent intent = new Intent(getApplicationContext(), ExpensesDetails.class);
        startActivity(intent);
    }
}
