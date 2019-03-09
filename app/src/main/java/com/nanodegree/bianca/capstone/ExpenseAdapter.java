package com.nanodegree.bianca.capstone;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {
    private Expense[] mDataSet;

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

    public ExpenseAdapter(Expense[] dataSet) {
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
        expenseViewHolder.summary.setText(mDataSet[position].getExpenseSummary());
        expenseViewHolder.value.setText(String.valueOf(mDataSet[position].getExpenseValue()));
        expenseViewHolder.date.setText(mDataSet[position].getExpenseDate().toString());
    }

    @Override
    public int getItemCount() {
        return mDataSet.length;
    }
}
