package com.nanodegree.bianca.capstone;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Telephony;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.PreferenceManager;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;
import com.nanodegree.bianca.capstone.data.Expense;
import com.nanodegree.bianca.capstone.data.ExpenseDao;
import com.nanodegree.bianca.capstone.data.ExpenseRoomDatabase;
import com.nanodegree.bianca.capstone.data.ExpenseViewModel;
import com.razerdp.widget.animatedpieview.AnimatedPieView;
import com.razerdp.widget.animatedpieview.AnimatedPieViewConfig;
import com.razerdp.widget.animatedpieview.callback.OnPieSelectListener;
import com.razerdp.widget.animatedpieview.data.SimplePieInfo;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String TAG = "MainActivity";

    public static final String EXPENSES = "Expenses";
    public static final String REMAINING = "Remaining";
    public static final float DEFAULT_BUDGET = 1000f;
    public static final float BUDGET_ALERT_TRIGGER = 50f;
    private static final int MY_PERMISSIONS_REQUEST_READ_SMS = 1;
    private static final int ADD_EXPENSE_RESULT = 2;

    private TextView mDaysLeftView;
    private TextView mRemainingLegendView;
    private TextView mExpensesLegendView;
    private float mTotalExpenses;
    private float mTotalBudget;
    private ExpenseRoomDatabase mDb;
    private ExpenseDao mDao;
    private Expense mLatestExpense;
    private long mLastExpireDate;
    private SharedPreferences mSharedPreferences;
    private ExpenseViewModel mExpenseViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDaysLeftView = findViewById(R.id.tv_days_left);
        mRemainingLegendView = findViewById(R.id.tv_legend_remaining);
        mRemainingLegendView.setOnClickListener(v -> startBudgetSettingActivity());
        mExpensesLegendView = findViewById(R.id.tv_legend_expenses);
        mExpensesLegendView.setOnClickListener(v -> startExpenseDetailsActivity());
        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setupAdBanner();
        setupSharedPreferences();
        setSupportActionBar(toolbar);

        mDb = ExpenseRoomDatabase.getDatabase(this);
        mDao = mDb.expenseDao();

        mTotalBudget =
                Float.valueOf(mSharedPreferences.getString(getResources().getString(R.string.key_budget),
                        String.valueOf(DEFAULT_BUDGET)));

        mExpenseViewModel = ViewModelProviders.of(this).get(ExpenseViewModel.class);
        mExpenseViewModel.getLatest().observe(this, new Observer<Expense>() {
            @Override
            public void onChanged(Expense expense) {
                setLatestExpense(expense);
                requestSmsReadPermission();
            }
        });

        mExpenseViewModel.getSinceLastExpire(mLastExpireDate).observe(this, new Observer<List<Expense>>() {
            @Override
            public void onChanged(List<Expense> expenses) {
                float total = 0f;
                for (Expense e : expenses) {
                    total += e.value;
                }
                setTotalExpenses(total);
                setupPieChart();
            }
        });
    }

    /* SHARED PREFERENCES */
    private void setupSharedPreferences() {
        Log.d(TAG, "setupSharedPreferences: ");
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        // Register the listener
        mSharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.d(TAG, "onSharedPreferenceChanged: ");
        if (key.equals(getString(R.string.key_budget))) {
            try {
                setTotalBudget(Float.valueOf(sharedPreferences.getString(key,
                        String.valueOf(DEFAULT_BUDGET))));
            } catch (NumberFormatException nfe) {
                sharedPreferences.edit().putString(key, String.valueOf(DEFAULT_BUDGET)).apply();
                setTotalBudget(DEFAULT_BUDGET);
            }
        }
        if (key.equals(getString(R.string.key_expire))) {
            updateLegends(sharedPreferences);
        }
        if (key.equals(getString(R.string.key_current_expenses))) {
            //TODO update widget
        }
        setupPieChart();
        updateLegends(mSharedPreferences);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startBudgetSettingActivity();
                break;
            case R.id.action_add_expense:
                startAddExpenseActivity();
                break;
            case R.id.action_backup:
                startBackupActivity();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /* PIE CHART */
    private void setupPieChart() {
        Log.d(TAG, "setupPieChart: ");
        // https://github.com/razerdp/AnimatedPieView/blob/master/README_EN.md
        AnimatedPieView mAnimatedPieView = findViewById(R.id.animatedPieView);
        AnimatedPieViewConfig config = new AnimatedPieViewConfig();
        config.startAngle(-90)
                .strokeWidth(100)
                .drawText(true)
                .textSize(30)
                .selectListener((OnPieSelectListener) (pieInfo, isFloatUp) -> {
                    Log.d(TAG, "onSelectPie: ");
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
                })
                .addData(new SimplePieInfo(getTotalExpenses(), Color.RED, EXPENSES), false)
                .addData(new SimplePieInfo(Math.max(getRemainingBudget(), 0f),
                        getResources().getColor(R.color.colorPrimary), REMAINING), false)
                .duration(750);
        mAnimatedPieView.applyConfig(config);
        mAnimatedPieView.start();
    }

    private void updateLegends(SharedPreferences sharedPreferences) {
        Log.d(TAG, "updateLegends: ");
        Calendar todayCalendar = Calendar.getInstance();
        Calendar nextExpireDayCalendar = Calendar.getInstance();
        Calendar lastExpireDayCalendar = Calendar.getInstance();
        int today = todayCalendar.get(Calendar.DATE);
        int expireDay = Integer.valueOf(sharedPreferences.getString(getString(R.string.key_expire),
                "1"));
        if (expireDay < today) {
            nextExpireDayCalendar.add(Calendar.MONTH, 1);
        } else {
            lastExpireDayCalendar.add(Calendar.MONTH, -1);
        }
        nextExpireDayCalendar.set(Calendar.DATE, expireDay);
        lastExpireDayCalendar.set(Calendar.DATE, expireDay);
        mLastExpireDate = lastExpireDayCalendar.getTimeInMillis();
        long daysLeft = TimeUnit.DAYS.convert(nextExpireDayCalendar.getTimeInMillis() -
                todayCalendar.getTimeInMillis(), TimeUnit.MILLISECONDS);
        String label = getResources().getQuantityString(R.plurals.days_left_label, Math.toIntExact(daysLeft),
                Math.toIntExact(daysLeft));
        mDaysLeftView.setText(label);

        mExpensesLegendView.setText(Util.formatSummary(
                getResources().getString(R.string.expenses_label), getTotalExpenses()));
        mRemainingLegendView.setText(Util.formatSummary(
                getResources().getString(R.string.remaining_label),
                Math.max(getRemainingBudget(), 0f)));
    }

    /* ADD BANNER */
    private void setupAdBanner() {
        Log.d(TAG, "setupAdBanner: ");
        PublisherAdView mPublisherAdView = findViewById(R.id.publisherAdView);
        PublisherAdRequest adRequest = new PublisherAdRequest.Builder().build();
        mPublisherAdView.loadAd(adRequest);
    }

    /* SMS */
    public void requestSmsReadPermission() {
        Log.d(TAG, "requestSmsReadPermission: ");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "requestSmsReadPermission: READ_SMS permission not granted :(");
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_SMS},
                    MY_PERMISSIONS_REQUEST_READ_SMS);
        } else {
            Log.d(TAG, "requestSmsReadPermission: READ_SMS permission granted :)");
            new InsertExpenseFromSmsAsyncTask().execute();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: ");
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_SMS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    new InsertExpenseFromSmsAsyncTask().execute();
                } else {
                    // Permission denied, boo!
                    Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void fetchSmsLog() {
        Log.d(TAG, "fetchSmsLog: ");
        // https://androidforums.com/threads/querying-the-sms-content-provider.158592/
        // filter by number and date

        Uri allMessages = Telephony.Sms.CONTENT_URI;
        String selection = Telephony.Sms.DATE + " > ?";
        String[] selectionArgs;
        if (mLatestExpense == null) {
            Log.d(TAG, "fetchSmsLog: latest expense is null");
            selectionArgs = new String[] {"0"};
        } else {
            // fetch messages since latest
            Log.d(TAG, "fetchSmsLog: latest expense is " + mLatestExpense.summary);
            selectionArgs = new String[] {Long.toString(mLatestExpense.date)};
        }
        Cursor cursor = this.getContentResolver().query(allMessages, null,
                selection, selectionArgs, null);

        int bodyColumnIndex = cursor.getColumnIndex("body");
        int dateColumnIndex = cursor.getColumnIndex("date");

        while (cursor.moveToNext()) {
            for (int i = 0; i < cursor.getColumnCount(); i++) {
                Log.d(TAG +  "- " + cursor.getColumnName(i) + "", cursor.getString(i) + "");
            }
            Log.d(TAG + "One row finished",
                    "**************************************************");
            String body = cursor.getString(bodyColumnIndex);
            long date = cursor.getLong(dateColumnIndex);

            Expense l = Util.parseExpense(body, date);
            if (l != null) {
                mDb.expenseDao().insert(l);
                checkBudget();
            }
        }

        cursor.close();
    }

    /* GETTERS / SETTERS */
    public void setLatestExpense(Expense latestExpense) {
        this.mLatestExpense = latestExpense;
    }

    public float getTotalExpenses() {
        return mTotalExpenses;
    }

    public void setTotalExpenses(float totalExpenses) {
        Log.d(TAG, "setTotalExpenses: " + totalExpenses);
        mTotalExpenses = totalExpenses;
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putFloat(getString(R.string.key_current_expenses), mTotalExpenses);
        editor.commit();
        updateWidgets();
        updateLegends(mSharedPreferences);
        setupPieChart();
    }

    private float getTotalBudget() {
        return mTotalBudget;
    }

    public void setTotalBudget(float totalBudget) {
        Log.d(TAG, "setTotalBudget: " + totalBudget);
        mTotalBudget = totalBudget;
        updateLegends(mSharedPreferences);
        setupPieChart();
    }

    private float getRemainingBudget() {
        return getTotalBudget() - getTotalExpenses();
    }

    private void startBudgetSettingActivity() {
        Intent intent = new Intent(getApplicationContext(), BudgetSettings.class);
        startActivity(intent);
        overridePendingTransition(R.anim.vertical_animation, 0);
    }

    private void startExpenseDetailsActivity() {
        Intent intent = new Intent(getApplicationContext(), ExpensesDetails.class);
        intent.putExtra("exp", mLastExpireDate);
        startActivity(intent);
        overridePendingTransition(R.anim.vertical_animation, 0);
    }

    private void startAddExpenseActivity() {
        Intent intent = new Intent(getApplicationContext(), AddExpense.class);
        startActivityForResult(intent, ADD_EXPENSE_RESULT);
        overridePendingTransition(R.anim.vertical_animation, 0);
    }

    private void startBackupActivity() {
        Intent intent = new Intent(getApplicationContext(), BackupActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.vertical_animation, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_EXPENSE_RESULT && resultCode == RESULT_OK) {
            checkBudget();
        }
    }

    private void checkBudget() {
        if (getRemainingBudget() - getTotalExpenses() > BUDGET_ALERT_TRIGGER) return;
        Intent intent = new Intent(this, MainActivity.class);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setContentTitle(getString(R.string.notification_message))
                .setContentText("Your budget is tight!")
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    private void updateWidgets() {
        AppWidgetManager widgetManager = AppWidgetManager.getInstance(this);
        int[] ids = widgetManager.getAppWidgetIds(new ComponentName(this, BillSmsWidgetProvider.class));
        Intent updateIntent = new Intent();
        updateIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        updateIntent.putExtra(BillSmsWidgetProvider.BILL_SMS_WIDGET_IDS_KEY, ids);
        this.sendBroadcast(updateIntent);
    }

    /* DB */
    private class InsertExpenseFromSmsAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            Log.d(TAG, "InsertExpenseTask doInBackground: ");
            fetchSmsLog();
            return null;
        }
    }
}
