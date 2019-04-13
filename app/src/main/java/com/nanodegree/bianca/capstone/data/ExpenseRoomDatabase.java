package com.nanodegree.bianca.capstone.data;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

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

    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback(){
        @Override
        public void onOpen (@NonNull SupportSQLiteDatabase db){
            super.onOpen(db);
            new UpdateDbAsync(INSTANCE).execute();
        }
    };

    private static class UpdateDbAsync extends AsyncTask<Void, Void, Void> {
        private final ExpenseDao mDao;

        public UpdateDbAsync(ExpenseRoomDatabase db) {
            mDao = db.expenseDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            //TODO fetch latest expenses
            Expense latest = mDao.getLatest();

            //TODO fetch expenses since latest from SMS Content Provider


            //TODO update Total expenses this month

            return null;
        }
    }
}
