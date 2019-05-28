package com.nanodegree.bianca.capstone;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.nanodegree.bianca.capstone.data.Expense;
import com.nanodegree.bianca.capstone.data.ExpenseDao;
import com.nanodegree.bianca.capstone.data.ExpenseRoomDatabase;
import com.nanodegree.bianca.capstone.data.ExpenseViewModel;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

public class BillSmsWidgetProvider extends AppWidgetProvider {
    private ExpenseViewModel mExpenseViewModel;
    private ExpenseRoomDatabase mDb;
    private ExpenseDao mDao;

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int N = appWidgetIds.length;
        mDb = ExpenseRoomDatabase.getDatabase(context);
        mDao = mDb.expenseDao();

        // Perform this loop procedure for each App Widget that belongs to this provider
        for (int appWidgetId : appWidgetIds) {
            // Create an Intent to launch ExampleActivity
            Intent intent = new Intent(context, AddExpense.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

            // Get the layout for the App Widget and attach an on-click listener
            // to the button
            RemoteViews views = new RemoteViews(context.getPackageName(),
                    R.layout.add_expense_widget);
            views.setOnClickPendingIntent(R.id.add_expense_button, pendingIntent);
//            views.setTextViewText(R.id.widget_expenses_summary,
//                    Float.toString(mDao.getLatest().getValue().value));

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
