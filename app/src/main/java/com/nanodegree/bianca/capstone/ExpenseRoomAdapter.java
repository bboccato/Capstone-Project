package com.nanodegree.bianca.capstone;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nanodegree.bianca.capstone.data.Expense;

import java.util.List;

public class ExpenseRoomAdapter extends RecyclerView.Adapter<ExpenseRoomAdapter.ExpenseViewHolder> {

    class ExpenseViewHolder extends RecyclerView.ViewHolder {
        private final TextView expenseItemView;

        private ExpenseViewHolder(View itemView) {
            super(itemView);
            expenseItemView = itemView.findViewById(R.id.tv_room_expense_summary);
        }
    }

    private final LayoutInflater mInflater;
    private List<Expense> mExpenses; // Cached copy of expenses

    ExpenseRoomAdapter(Context context) { mInflater = LayoutInflater.from(context); }

    @Override
    public ExpenseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.expense_room_item, parent, false);
        return new ExpenseViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ExpenseViewHolder holder, int position) {
        if (mExpenses != null) {
            Expense current = mExpenses.get(position);
            holder.expenseItemView.setText(current.summary);
        } else {
            // Covers the case of data not being ready yet.
            holder.expenseItemView.setText("No Expense");
        }
    }

    void setExpenses(List<Expense> expenses){
        mExpenses = expenses;
        notifyDataSetChanged();
    }

    // getItemCount() is called many times, and when it is first called,
    // mExpenses has not been updated (means initially, it's null, and we can't return null).
    @Override
    public int getItemCount() {
        if (mExpenses != null)
            return mExpenses.size();
        else return 0;
    }
}
