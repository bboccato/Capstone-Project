package com.nanodegree.bianca.capstone;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
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
import android.util.Log;

import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;
import com.nanodegree.bianca.capstone.data.Expense;
import com.nanodegree.bianca.capstone.data.ExpenseDao;
import com.nanodegree.bianca.capstone.data.ExpenseRoomDatabase;
import com.nanodegree.bianca.capstone.data.ExpenseViewModel;
import com.razerdp.widget.animatedpieview.AnimatedPieView;
import com.razerdp.widget.animatedpieview.AnimatedPieViewConfig;
import com.razerdp.widget.animatedpieview.callback.OnPieSelectListener;
import com.razerdp.widget.animatedpieview.data.IPieInfo;
import com.razerdp.widget.animatedpieview.data.SimplePieInfo;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    public static final String EXPENSES = "Expenses";
    public static final String REMAINING = "Remaining";
    private static final int MY_PERMISSIONS_REQUEST_READ_SMS = 1;

    private PublisherAdView mPublisherAdView;
    private AnimatedPieView mAnimatedPieView;
    private float mTotalExpenses;
    private static volatile ExpenseRoomDatabase INSTANCE;
    private List<ExpenseLocal> myDataset = new ArrayList<>();
    private ExpenseViewModel mExpenseViewModel;
    ExpenseRoomDatabase mDb;
    private Expense mLatestExpense;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDb = ExpenseRoomDatabase.getDatabase(this);
        ExpenseDao dao = mDb.expenseDao();
        new FetchLatestAsyncTask(this, dao).execute();

        //TODO and populate with latest expenses
        setTotalExpenses(200f);
        setupPieChart();
        setupAdBanner();
    }

    /* SETUP */
    private void setupPieChart() {
        // https://github.com/razerdp/AnimatedPieView/blob/master/README_EN.md
        mAnimatedPieView = findViewById(R.id.animatedPieView);
        AnimatedPieViewConfig config = new AnimatedPieViewConfig();
        config.startAngle(-90)
//                .pieRadius(300f)
                .strokeWidth(100)
                .drawText(true)
                .textSize(20)
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

            //TODO add expense to DB
            new insertAsyncTask(mDb.expenseDao()).execute(ExpenseLocal.parseExpense(body, date));
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
        mTotalExpenses = totalExpenses;
    }

    private float getTotalBudget() {
        //TODO replace with appropriate value from preferences
        return 400f;
    }

    private float getRemainingBudget() {
        return getTotalBudget() - getTotalExpenses();
    }

    private void startBudgetSettingActivity() {
        Intent intent = new Intent(getApplicationContext(), BudgetPreferences.class);
        startActivity(intent);
    }

    private void startExpenseDetailsActivity() {
        Intent intent = new Intent(getApplicationContext(), ExpensesDetails.class);
        startActivity(intent);
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

    private class UpdateDbAsyncTask extends AsyncTask<List<Expense>, Void, Void> {

        private ExpenseDao mAsyncTaskDao;
        private List<Expense> mExpenses;

        UpdateDbAsyncTask(ExpenseDao dao, List<Expense> expenses) {
            mAsyncTaskDao = dao;
            mExpenses = expenses;
        }

        @Override
        protected Void doInBackground(final List<Expense>... params) {
            mAsyncTaskDao.insert(mExpenses.get(0));
            return null;
        }
    }

    private class insertAsyncTask extends AsyncTask<Expense, Void, Void> {

        private ExpenseDao mAsyncTaskDao;

        insertAsyncTask(ExpenseDao dao) {
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

}
