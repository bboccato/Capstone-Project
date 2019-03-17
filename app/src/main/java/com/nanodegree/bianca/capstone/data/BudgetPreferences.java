package com.nanodegree.bianca.capstone.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.nanodegree.bianca.capstone.R;

public class BudgetPreferences {
    public static final String PREF_BUDGET = "budget";
    public static final String PREF_EXPIRE = "expire";
    public static final String PREF_NOTIFICATION = "notification";

    private static final float DEFAULT_BUDGET = 1000f;
    private static final int DEFAULT_EXPIRE = 10;
    private static final boolean DEFAULT_NOTIFICATION = true;

    public float getPreferredBudget(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String keyForBudget = context.getString(R.string.key_budget);
        return preferences.getFloat(keyForBudget, DEFAULT_BUDGET);
    }

    public int getPreferredExpire(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String keyForExpire = context.getString(R.string.key_expire);
        int defaultExpire = DEFAULT_EXPIRE;
        return preferences.getInt(keyForExpire, DEFAULT_EXPIRE);
    }

    public boolean getPreferredNotification(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String keyForNotification = context.getString(R.string.key_notification);
        return preferences.getBoolean(keyForNotification, DEFAULT_NOTIFICATION);
    }
}
