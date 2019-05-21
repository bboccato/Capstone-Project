package com.nanodegree.bianca.capstone;

import android.annotation.SuppressLint;

import com.nanodegree.bianca.capstone.data.Expense;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {
    @SuppressLint("DefaultLocale")
    public static String formatSummary(String label, float value) {
        return String.format("%s US$ %.2f", label, value);
    }

    public static Expense parseExpense(String message, long indate) {
        /* Expected SMS is formatted as
        * Compra aprovada no seu PERSON VS PLATINUM final 7701 - PAG*SMS-21* valor US$21.00 em 07/03, as 19h16 .
        * */
        String regex = ".*\\*(.*)\\*.*\\$(\\d+\\.\\d+).*";
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
}
