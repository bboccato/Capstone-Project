package com.nanodegree.bianca.capstone;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;
import com.nanodegree.bianca.capstone.data.Expense;
import com.nanodegree.bianca.capstone.data.ExpenseDao;
import com.nanodegree.bianca.capstone.data.ExpenseRoomDatabase;
import com.razerdp.widget.animatedpieview.AnimatedPieView;
import com.razerdp.widget.animatedpieview.AnimatedPieViewConfig;
import com.razerdp.widget.animatedpieview.callback.OnPieSelectListener;
import com.razerdp.widget.animatedpieview.data.IPieInfo;
import com.razerdp.widget.animatedpieview.data.SimplePieInfo;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.DAYS;

public class MainActivity extends AppCompatActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String TAG = "MainActivity";

    public static final String EXPENSES = "Expenses";
    public static final String REMAINING = "Remaining";
    public static final float DEFAULT_BUDGET = 1000f;
    private static final int MY_PERMISSIONS_REQUEST_READ_SMS = 1;

    private PublisherAdView mPublisherAdView;
    private AnimatedPieView mAnimatedPieView;
    private TextView mDaysLeftView;
    private float mTotalExpenses ;
    private float mTotalBudget = DEFAULT_BUDGET;
    private static volatile ExpenseRoomDatabase INSTANCE;
    private ExpenseRoomDatabase mDb;
    private Expense mLatestExpense;
    private long mLastExpireDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDaysLeftView = findViewById(R.id.tv_days_left);
        setupSharedPreferences();

        mDb = ExpenseRoomDatabase.getDatabase(this);
        ExpenseDao dao = mDb.expenseDao();
        new FetchLatestAsyncTask(this, dao).execute();
        new TotalExpensesAsyncTask(dao, mLastExpireDate).execute();

        setupAdBanner();
    }

    /* SETUP */
    private void setupSharedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        setTotalBudget(Float.valueOf(sharedPreferences.getString(getString(R.string.key_budget),
                "1000")));
        setRemainingDays(sharedPreferences);
        // Register the listener
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

    }

    private void setupPieChart() {
        // https://github.com/razerdp/AnimatedPieView/blob/master/README_EN.md
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
                .addData(new SimplePieInfo(getTotalExpenses(), Color.RED,
                        String.valueOf(getTotalExpenses())), false)
                .addData(new SimplePieInfo(getRemainingBudget(), Color.GREEN,
                        String.valueOf(getRemainingBudget())), false)
                .duration(750);
        mAnimatedPieView.applyConfig(config);
        mAnimatedPieView.start();
    }

    private void setupAdBanner() {
        mPublisherAdView = findViewById(R.id.publisherAdView);
        PublisherAdRequest adRequest = new PublisherAdRequest.Builder().build();
        mPublisherAdView.loadAd(adRequest);
    }

    /* SMS */
    public void requestSmsReadPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "parseSmsLog: READ_SMS permission not granted");
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_SMS},
                    MY_PERMISSIONS_REQUEST_READ_SMS);
        } else {
            Log.d(TAG, "parseSmsLog: READ_SMS permission granted :)");
            fetchSmsLog();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_SMS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fetchSmsLog();
                } else {
                    // TODO permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }

    private void fetchSmsLog() {
        // https://androidforums.com/threads/querying-the-sms-content-provider.158592/
        // filter by number and date

        Uri allMessages = Telephony.Sms.CONTENT_URI;
        String selection = Telephony.Sms.DATE + " > ?";
        String[] selectionArgs;
        if (mLatestExpense == null) {
            //TODO fetch messages from latest month
            selectionArgs = new String[] {"0"};
        } else {
            // fetch messages since latest
            selectionArgs = new String[] {Long.toString(mLatestExpense.date)};
        }
        Cursor cursor = this.getContentResolver().query(allMessages, null,
                selection, selectionArgs, null);

        int bodyColumnIndex = cursor.getColumnIndex("body");
        int dateColumnIndex = cursor.getColumnIndex("date");

        Log.d(TAG, "bib fetchSmsLog: > > > >");
        while (cursor.moveToNext()) {
            for (int i = 0; i < cursor.getColumnCount(); i++) {
                Log.d(TAG +  "- " + cursor.getColumnName(i) + "", cursor.getString(i) + "");
            }
            Log.d(TAG + "One row finished",
                    "**************************************************");
            String body = cursor.getString(bodyColumnIndex);
            long date = cursor.getLong(dateColumnIndex);

            new InsertExpenseAsyncTask(mDb.expenseDao()).execute(ExpenseLocal.parseExpense(body,
                    date));
        }
        Log.d(TAG, "bib fetchSmsLog: > > > > count = " + cursor.getColumnCount());

    }



    /* AUX */
    public void setLatestExpense(Expense latestExpense) {
        this.mLatestExpense = latestExpense;
    }

    public float getTotalExpenses() {
        return mTotalExpenses;
    }

    public void setTotalExpenses(float totalExpenses) {
        Log.d(TAG, "bib setTotalExpenses: " + totalExpenses);
        mTotalExpenses = totalExpenses;
    }

    private float getTotalBudget() {
        return mTotalBudget;
    }

    public void setTotalBudget(float totalBudget) {
        Log.d(TAG, "bib setTotalBudget: " + totalBudget);
        mTotalBudget = totalBudget;
        //TODO is it possible to just update the chart rather than recreating it?
        setupPieChart();
    }

    private float getRemainingBudget() {
        return getTotalBudget() - getTotalExpenses();
    }

    private void setRemainingDays(SharedPreferences sharedPreferences) {
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
        mDaysLeftView.setText(TimeUnit.DAYS.convert(nextExpireDayCalendar.getTimeInMillis() -
                        todayCalendar.getTimeInMillis(), TimeUnit.MILLISECONDS) + " days left");
    }

    private void startBudgetSettingActivity() {
        Intent intent = new Intent(getApplicationContext(), BudgetSettings.class);
        startActivity(intent);
    }

    private void startExpenseDetailsActivity() {
        Intent intent = new Intent(getApplicationContext(), ExpensesDetails.class);
        intent.putExtra("exp", mLastExpireDate);
        startActivity(intent);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.key_budget))) {
            setTotalBudget(Float.valueOf(sharedPreferences.getString(key, "1000")));
        }
        if (key.equals(getString(R.string.key_expire))) {
            setRemainingDays(sharedPreferences);
        }
    }

    private class FetchLatestAsyncTask extends AsyncTask<Expense, Void, Expense> {

        private ExpenseDao mAsyncTaskDao;
        private Context mContext;

        FetchLatestAsyncTask(Context context, ExpenseDao dao) {
            mContext = context;
            mAsyncTaskDao = dao;
        }

        @Override
        protected Expense doInBackground(final Expense... params) {
            Expense latest = mAsyncTaskDao.getLatest();
            return latest;
        }

        @Override
        protected void onPostExecute(Expense expense) {
            setLatestExpense(expense);
            requestSmsReadPermission();
        }
    }

    private class InsertExpenseAsyncTask extends AsyncTask<Expense, Void, Void> {

        private ExpenseDao mAsyncTaskDao;

        InsertExpenseAsyncTask(ExpenseDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Expense... params) {
            Log.d(TAG, "bib doInBackground: INSERT " + params[0].summary);
            if (params[0] != null) {
                mAsyncTaskDao.insert(params[0]);
            }
            return null;
        }
    }

    private class TotalExpensesAsyncTask extends AsyncTask<Void, Void, Void> {

        private ExpenseDao mAsyncTaskDao;
        private long mLastExpireDate;

        TotalExpensesAsyncTask(ExpenseDao dao, long lastExpireDate) {
            mAsyncTaskDao = dao;
            mLastExpireDate = lastExpireDate;
        }

        @Override
        protected Void doInBackground(Void... voids) {
//            List<Expense> expenses = (List<Expense>) mAsyncTaskDao.getAllB();
            List<Expense> expenses =
                    (List<Expense>) mAsyncTaskDao.getSinceLastExpire(mLastExpireDate);
            float total = 0f;
            for (Expense e : expenses) {
                total += e.value;
            }
            setTotalExpenses(total);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            setupPieChart();
        }
    }

}
