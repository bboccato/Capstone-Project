package com.nanodegree.bianca.capstone;

import java.util.Date;

public class Expense {
    private String expenseSummary;
    private float expenseValue;
    private Date expenseDate;

    public Expense(String expenseSummary, float expenseValue, Date expenseDate) {
        this.expenseSummary = expenseSummary;
        this.expenseValue = expenseValue;
        this.expenseDate = expenseDate;
    }

    public String getExpenseSummary() {
        return expenseSummary;
    }

    public void setExpenseSummary(String expenseSummary) {
        this.expenseSummary = expenseSummary;
    }

    public float getExpenseValue() {
        return expenseValue;
    }

    public void setExpenseValue(float expenseValue) {
        this.expenseValue = expenseValue;
    }

    public Date getExpenseDate() {
        return expenseDate;
    }

    public void setExpenseDate(Date expenseDate) {
        this.expenseDate = expenseDate;
    }
}
