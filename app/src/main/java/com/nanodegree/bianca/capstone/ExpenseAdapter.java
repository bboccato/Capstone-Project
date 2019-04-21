package com.nanodegree.bianca.capstone;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nanodegree.bianca.capstone.data.Expense;

import java.text.SimpleDateFormat;
import java.util.List;

class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {
    private List<Expense> mDataSet;

    public static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        public TextView summary;
        public TextView value;
        public TextView date;
        public ExpenseViewHolder(@NonNull View view) {
            super(view);
            summary = view.findViewById(R.id.tv_expense_summary);
            value = view.findViewById(R.id.tv_expense_value);
            date = view.findViewById(R.id.tv_expense_date);
        }
    }

    public ExpenseAdapter(List<Expense> dataSet) {
        mDataSet = dataSet;
    }

    @NonNull
    @Override
    public ExpenseAdapter.ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                               int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.expense_item, parent, false);
        ExpenseViewHolder vh = new ExpenseViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseAdapter.ExpenseViewHolder expenseViewHolder,
                                 int position) {
        Expense expense = mDataSet.get(position);

        expenseViewHolder.summary.setText(expense.summary);

        expenseViewHolder.value.setText(String.format("US$ %.2f", expense.value));

        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
        String dateString = format.format(expense.date);
        expenseViewHolder.date.setText(dateString);
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }
}
