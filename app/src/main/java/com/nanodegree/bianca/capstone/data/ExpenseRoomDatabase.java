package com.nanodegree.bianca.capstone.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
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
