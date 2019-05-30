package com.nanodegree.bianca.capstone;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

import com.nanodegree.bianca.capstone.data.Expense;
import com.nanodegree.bianca.capstone.data.ExpenseDao;
import com.nanodegree.bianca.capstone.data.ExpenseRoomDatabase;
import com.nanodegree.bianca.capstone.data.ExpenseViewModel;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import java.util.prefs.Preferences;

public class BillSmsWidgetProvider extends AppWidgetProvider {
    private ExpenseViewModel mExpenseViewModel;
    private ExpenseRoomDatabase mDb;
    private ExpenseDao mDao;
    public static final String BILL_SMS_WIDGET_IDS_KEY ="bill_sms_widget_ids_key";

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int N = appWidgetIds.length;
        update(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.hasExtra(BILL_SMS_WIDGET_IDS_KEY)) {
            int[] ids = intent.getExtras().getIntArray(BILL_SMS_WIDGET_IDS_KEY);
            this.onUpdate(context, AppWidgetManager.getInstance(context), ids);
        } else super.onReceive(context, intent);
    }

    private void update(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        float totalExpenses = sharedPreferences.getFloat(context.getResources().
                getString(R.string.key_current_expenses), 0f);
        Intent intent = new Intent(context, AddExpense.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        RemoteViews views = new RemoteViews(context.getPackageName(),
                R.layout.add_expense_widget);
        for (int appWidgetId : appWidgetIds) {
            views.setOnClickPendingIntent(R.id.add_expense_button, pendingIntent);
            views.setTextViewText(R.id.widget_expenses_summary, Util.formatSummary(
                    context.getResources().getString(R.string.expenses_label) + "\n", totalExpenses));
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
