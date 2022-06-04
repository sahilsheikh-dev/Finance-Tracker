package com.begawoinc.financetracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FinancialGoalsRecyclerAdapter extends RecyclerView.Adapter<FinancialGoalsRecyclerAdapter.FinancialGoalsViewHolder> {

    private static final String TAG = "RecyclerAdapter";
    List<FinancialGoals> goalsList;

    public FinancialGoalsRecyclerAdapter(List<FinancialGoals> goalsList) {
        this.goalsList = goalsList;
    }

    @NonNull
    @Override
    public FinancialGoalsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.goal_item, parent, false);
        FinancialGoalsViewHolder financialGoalsViewHolder = new FinancialGoalsViewHolder(view);
        return financialGoalsViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FinancialGoalsViewHolder holder, int position) {
        holder.goalName.setText(String.valueOf(goalsList.get(position).getGoal()));
        holder.goalDate.setText(String.valueOf(goalsList.get(position).getGoalDate()));
        holder.amtNeed.setText(String.valueOf(goalsList.get(position).getAmtNeed()));
        holder.amtHaving.setText(String.valueOf(goalsList.get(position).getAmtHaving()));
    }

    @Override
    public int getItemCount() {
        return goalsList.size();
    }

    class FinancialGoalsViewHolder extends RecyclerView.ViewHolder {
        TextView goalName, goalDate, amtHaving, amtNeed, amtMoreNeed;

        public FinancialGoalsViewHolder(@NonNull View itemView) {
            super(itemView);

            goalName = itemView.findViewById(R.id.goalName);
            goalDate = itemView.findViewById(R.id.goalDate);
            amtHaving = itemView.findViewById(R.id.amtHaving);
            amtNeed = itemView.findViewById(R.id.amtNeed);

        }
    }

}
