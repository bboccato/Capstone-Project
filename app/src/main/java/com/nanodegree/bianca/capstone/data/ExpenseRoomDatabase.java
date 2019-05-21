package com.nanodegree.bianca.capstone.data;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

@Database(entities = {Expense.class}, version = 1, exportSchema = false)
public abstract class ExpenseRoomDatabase extends RoomDatabase {
    public abstract ExpenseDao expenseDao();

    private static volatile ExpenseRoomDatabase INSTANCE;

    public static ExpenseRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (ExpenseRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            ExpenseRoomDatabase.class, "expense_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
