package com.nanodegree.bianca.capstone;

import com.nanodegree.bianca.capstone.data.Expense;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExpenseLocal {
    private String expenseSummary;
    private float expenseValue;
    private Date expenseDate;

    public ExpenseLocal(String expenseSummary, float expenseValue, Date expenseDate) {
        this.expenseSummary = expenseSummary;
        this.expenseValue = expenseValue;
        this.expenseDate = expenseDate;
    }

    public ExpenseLocal(String message) {
        String regex =
                ".*ompra.*\\*(.*) valor RS (\\d+\\.\\d\\d).*(\\d\\d/\\d\\d).*(\\d\\d:\\d\\d).*";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            this.expenseSummary = matcher.group(1);
            String value = matcher.group(2);
            this.expenseValue = Float.valueOf(value);
            String date = matcher.group(3);
            String time = matcher.group(4);
            SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyyHH:mm");
            try {
                this.expenseDate =
                        dateFormatter.parse(date + "/" + Calendar.getInstance().get(Calendar.YEAR) + time);
            } catch (ParseException e) {
                this.expenseDate = new Date();
            }

        }
    }

    public static Expense parseExpense(String message, long indate) {
        String regex =
                ".*ompra.*\\*(.*) valor RS (\\d+\\.\\d\\d).*";
//                ".*ompra.*\\*(.*) valor RS (\\d+).*";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            String expenseSummary = matcher.group(1);
            String value = matcher.group(2);
            float expenseValue = Float.valueOf(value);

            return new Expense(indate, expenseSummary, expenseValue);
        } else {
            return null;
        }
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
