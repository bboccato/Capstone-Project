package com.nanodegree.bianca.capstone.data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;


@Entity(tableName = "expense_table")
public class Expense {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "uid")
    public int uid;

    @ColumnInfo(name = "date")
    public long date;

    @ColumnInfo(name = "summary")
    public String summary;

    @ColumnInfo(name = "value")
    public float value;

    public Expense(long date, String summary, float value) {
        this.date = date;
        this.summary = summary;
        this.value = value;
    }
}