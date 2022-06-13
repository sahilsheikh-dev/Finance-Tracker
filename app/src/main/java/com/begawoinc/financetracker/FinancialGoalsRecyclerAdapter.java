package com.begawoinc.financetracker;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.List;

public class FinancialGoalsRecyclerAdapter extends RecyclerView.Adapter<FinancialGoalsRecyclerAdapter.FinancialGoalsViewHolder> {

    List<FinancialGoals> goalsList;
    public static String USERNAME;
    String amtHaving, goalDate, dateSt = "";
    int id;
    long count;

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

    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(@NonNull FinancialGoalsViewHolder holder, int position) {

//        String.format("%,.2f", Double.parseDouble(amtNeed)).toString().trim()
        holder.goalName.setText(String.valueOf(goalsList.get(position).getGoal()));

        dateSt = "";
        int monthSt = Integer.parseInt(goalsList.get(position).getGoalDate().charAt(5) + "" + goalsList.get(position).getGoalDate().charAt(6));
        if (String.valueOf(monthSt).charAt(0) == 0) monthSt = String.valueOf(monthSt).charAt(1);

        switch (monthSt){
            case 1:
                dateSt = dateSt + "January ";
                break;
            case 2:
                dateSt = dateSt + "February ";
                break;
            case 3:
                dateSt = dateSt + "March ";
                break;
            case 4:
                dateSt = dateSt + "April ";
                break;
            case 5:
                dateSt = dateSt + "May ";
                break;
            case 6:
                dateSt = dateSt + "June ";
                break;
            case 7:
                dateSt = dateSt + "July ";
                break;
            case 8:
                dateSt = dateSt + "August ";
                break;
            case 9:
                dateSt = dateSt + "September ";
                break;
            case 10:
                dateSt = dateSt + "October ";
                break;
            case 11:
                dateSt = dateSt + "November ";
                break;
            case 12:
                dateSt = dateSt + "December ";
                break;
            default:
                dateSt = dateSt + "ERROR ";
                break;
        }

        dateSt = dateSt + String.valueOf(goalsList.get(position).getGoalDate()).trim().charAt(8) + String.valueOf(goalsList.get(position).getGoalDate()).trim().charAt(9) + ", ";
        dateSt = dateSt + String.valueOf(goalsList.get(position).getGoalDate()).trim().charAt(0) + String.valueOf(goalsList.get(position).getGoalDate()).trim().charAt(1) + String.valueOf(goalsList.get(position).getGoalDate()).trim().charAt(2) + String.valueOf(goalsList.get(position).getGoalDate()).trim().charAt(3);

        holder.goalDate.setText(dateSt);

        if (Double.parseDouble(String.valueOf(goalsList.get(position).getAmtNeed())) < 1000) {
            holder.amtNeed.setText(String.format("%,.2f", goalsList.get(position).getAmtNeed()).toString().trim() + "/-");
        } else if (Double.parseDouble(String.valueOf(goalsList.get(position).getAmtNeed())) < 1000000 && Double.parseDouble(String.valueOf(goalsList.get(position).getAmtNeed())) >= 1000) {
            double tempAmount = Double.parseDouble(String.valueOf(goalsList.get(position).getAmtNeed()))/1000;
            holder.amtNeed.setText(String.format("%,.2f", tempAmount).toString().trim()+" K");
        } else if (Double.parseDouble(String.valueOf(goalsList.get(position).getAmtNeed())) >= 1000000 && Double.parseDouble(String.valueOf(goalsList.get(position).getAmtNeed())) < 1000000000){
            double tempAmount = Double.parseDouble(String.valueOf(goalsList.get(position).getAmtNeed()))/1000000;
            holder.amtNeed.setText(String.format("%,.2f", tempAmount).toString().trim()+" M");
        } else if (Double.parseDouble(String.valueOf(goalsList.get(position).getAmtNeed())) >= 1000000000){
            double tempAmount = Double.parseDouble(String.valueOf(goalsList.get(position).getAmtNeed()))/1000000000;
            holder.amtNeed.setText(String.format("%,.2f", tempAmount).toString().trim()+" B");
        }

        if (Double.parseDouble(String.valueOf(goalsList.get(position).getAmtHaving())) < 1000) {
            holder.amtHaving.setText(String.format("%,.2f", goalsList.get(position).getAmtHaving()).toString().trim() + "/-");
        } else if (Double.parseDouble(String.valueOf(goalsList.get(position).getAmtHaving())) < 1000000 && Double.parseDouble(String.valueOf(goalsList.get(position).getAmtHaving())) >= 1000) {
            double tempAmount = Double.parseDouble(String.valueOf(goalsList.get(position).getAmtHaving()))/1000;
            holder.amtHaving.setText(String.format("%,.2f", tempAmount).toString().trim()+" K");
        } else if (Double.parseDouble(String.valueOf(goalsList.get(position).getAmtHaving())) >= 1000000 && Double.parseDouble(String.valueOf(goalsList.get(position).getAmtHaving())) < 1000000000){
            double tempAmount = Double.parseDouble(String.valueOf(goalsList.get(position).getAmtHaving()))/1000000;
            holder.amtHaving.setText(String.format("%,.2f", tempAmount).toString().trim()+" M");
        } else if (Double.parseDouble(String.valueOf(goalsList.get(position).getAmtHaving())) >= 1000000000){
            double tempAmount = Double.parseDouble(String.valueOf(goalsList.get(position).getAmtHaving()))/1000000000;
            holder.amtHaving.setText(String.format("%,.2f", tempAmount).toString().trim()+" B");
        }

//        onclick update data
        holder.goalUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                id = goalsList.get(position).getGoalId();

                SharedPreferences sharedPreferences = view.getContext().getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
                USERNAME = sharedPreferences.getString("USERNAME", "");

                LayoutInflater li = LayoutInflater.from(view.getContext());
                View goalView = li.inflate(R.layout.save_financial_goal, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(view.getContext());
                alertDialogBuilder.setView(goalView);

                TextView goalHeading = goalView.findViewById(R.id.goalHeading);
                Button saveGoalBtn = goalView.findViewById(R.id.saveGoalBtn);
                Button closeBtn = goalView.findViewById(R.id.closeBtn);
                ProgressBar progressBar = goalView.findViewById(R.id.progressBar);

                goalHeading.setText("Update your financial goal");

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference(USERNAME).child("financialGoals").child(String.valueOf(id));

                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            FinancialGoals goal = snapshot.getValue(FinancialGoals.class);

                            TextView incorrectDate = goalView.findViewById(R.id.incorrectDate);
                            TextInputLayout goalNameIn = goalView.findViewById(R.id.goalName);
                            EditText dateIn = goalView.findViewById(R.id.date);
                            EditText monthIn = goalView.findViewById(R.id.month);
                            EditText yearIn = goalView.findViewById(R.id.year);
                            TextInputLayout amtNeedIn = goalView.findViewById(R.id.amtNeed);
                            TextInputLayout amtHavingIn = goalView.findViewById(R.id.amtHaving);

//                            YYYY-MM-DD
                            goalNameIn.getEditText().setText(goal.getGoal());

                            yearIn.setText(goal.getGoalDate().charAt(0)+""+goal.getGoalDate().charAt(1)+""+goal.getGoalDate().charAt(2)+""+goal.getGoalDate().charAt(3));
                            monthIn.setText(goal.getGoalDate().charAt(5)+""+goal.getGoalDate().charAt(6));
                            dateIn.setText(goal.getGoalDate().charAt(8)+""+goal.getGoalDate().charAt(9));
                            amtNeedIn.getEditText().setText(String.format("%.2f", goal.getAmtNeed()));
                            amtHavingIn.getEditText().setText(String.format("%.2f", goal.getAmtHaving()));

                            saveGoalBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    progressBar.setVisibility(View.VISIBLE);

                                    String goalName = goalNameIn.getEditText().getText().toString().trim();

//                                    String goalDate = monthIn.getText().toString().trim() + "/" + dateIn.getText().toString().trim() + "/" + yearIn.getText().toString().trim();

                                    goalDate = "";

                                    goalDate = goalDate + yearIn.getText().toString().trim() + "/";

                                    if (monthIn.getText().toString().trim().length() == 1) goalDate = goalDate + "0"+monthIn.getText().toString().trim() + "/";
                                    else goalDate = goalDate + monthIn.getText().toString().trim() + "/";

                                    if (dateIn.getText().toString().trim().length() == 1) goalDate = goalDate + "0"+dateIn.getText().toString().trim();
                                    else goalDate = goalDate + dateIn.getText().toString().trim();

                                    String amtNeed = amtNeedIn.getEditText().getText().toString().trim();
                                    amtHaving = amtHavingIn.getEditText().getText().toString().trim();

                                    Date currentDate = new Date();

                                    if (isEmpty(goalName) && isEmpty(amtNeed) &&
                                            isEmpty(monthIn.getText().toString().trim()) && isEmpty(dateIn.getText().toString().trim()) && isEmpty(yearIn.getText().toString().trim())) {
                                        goalNameIn.setError("Please Enter Goal Name");
                                        incorrectDate.setVisibility(View.VISIBLE);
                                        amtNeedIn.setError("Please Enter Your Target");
                                        progressBar.setVisibility(View.GONE);
                                    } else if (isEmpty(goalName)) {
                                        incorrectDate.setVisibility(View.GONE);
                                        amtNeedIn.setError(null);
                                        amtHavingIn.setError(null);
                                        goalNameIn.setError("Please Enter Goal Name");
                                        progressBar.setVisibility(View.GONE);
                                    } else if (isEmpty(amtNeed)) {
                                        goalNameIn.setError(null);
                                        incorrectDate.setVisibility(View.GONE);
                                        amtHavingIn.setError(null);
                                        amtNeedIn.setError("Please Enter Your Target");
                                        progressBar.setVisibility(View.GONE);
                                    } else if (isEmpty(monthIn.getText().toString().trim()) || isEmpty(dateIn.getText().toString().trim()) || isEmpty(yearIn.getText().toString().trim())) {
                                        goalNameIn.setError(null);
                                        amtNeedIn.setError(null);
                                        amtHavingIn.setError(null);
                                        incorrectDate.setVisibility(View.VISIBLE);
                                        progressBar.setVisibility(View.GONE);
                                    } else if (Integer.parseInt(yearIn.getText().toString().trim()) < (currentDate.getYear()+1900) || String.valueOf(yearIn.getText()).trim().length() < 4) {
                                        goalNameIn.setError(null);
                                        amtNeedIn.setError(null);
                                        amtHavingIn.setError(null);
                                        incorrectDate.setVisibility(View.VISIBLE);
                                        progressBar.setVisibility(View.GONE);
                                    } else if (Integer.parseInt(monthIn.getText().toString().trim()) < 1 || Integer.parseInt(monthIn.getText().toString().trim()) > 12){
                                        goalNameIn.setError(null);
                                        amtNeedIn.setError(null);
                                        amtHavingIn.setError(null);
                                        incorrectDate.setVisibility(View.VISIBLE);
                                        progressBar.setVisibility(View.GONE);
                                    } else {
                                        goalNameIn.setError(null);
                                        incorrectDate.setVisibility(View.GONE);
                                        amtNeedIn.setError(null);
                                        amtHavingIn.setError(null);

                                        if (isEmpty(String.valueOf(amtHaving))) amtHaving = "0";

                                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                                        DatabaseReference myRef = database.getReference(USERNAME).child("financialGoals");

                                        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                myRef.child(String.valueOf(id)).child("amtNeed").setValue(Double.parseDouble(amtNeed));
                                                myRef.child(String.valueOf(id)).child("amtHaving").setValue(Double.parseDouble(amtHaving));
                                                myRef.child(String.valueOf(id)).child("goal").setValue(goalName.toString().trim());
                                                myRef.child(String.valueOf(id)).child("goalDate").setValue(goalDate.toString().trim());

                                                progressBar.setVisibility(View.GONE);
                                                Toast.makeText(view.getContext(), "Goal Saved", Toast.LENGTH_LONG).show();
                                                Intent intent = new Intent(view.getContext(), Goals.class);
                                                alertDialog.dismiss();
                                                view.getContext().startActivity(intent);
                                                ((Activity)view.getContext()).finish();
                                            }
                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {
                                                progressBar.setVisibility(View.GONE);
                                                Toast.makeText(view.getContext(), "ERROR:"+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }
                            });

                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(view.getContext(), "ERROR::"+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

                closeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) { alertDialog.dismiss(); }
                });
//                alertDialog.dismiss();
            }
        });

//        onclick delete data
        holder.goalDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                id = goalsList.get(position).getGoalId();
                SharedPreferences sharedPreferences = view.getContext().getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
                USERNAME = sharedPreferences.getString("USERNAME", "");

                LayoutInflater li = LayoutInflater.from(view.getContext());
                View goalView = li.inflate(R.layout.delete_dialog, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(view.getContext());
                alertDialogBuilder.setView(goalView);

                Button deleteGoalBtn = goalView.findViewById(R.id.deleteGoalBtn);
                Button closeBtn = goalView.findViewById(R.id.closeBtn);
                ProgressBar progressBar = goalView.findViewById(R.id.progressBar);

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference(USERNAME).child("financialGoals");
//                .child(String.valueOf(id))

                deleteGoalBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        progressBar.setVisibility(View.VISIBLE);
//                        myRef.removeValue();
                        final int[] check = {0};
                        myRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                count = (long) snapshot.child("financialGoalMaxCount").getValue();
                                if (count != 0 && count > 0 && check[0] != 1) {
//                                    count -= 1;
                                    if (id == count || id > count) count -= 1;

                                    myRef.child("financialGoalMaxCount").setValue(count);
                                    myRef.child(String.valueOf(id)).removeValue();
                                    check[0]++;
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(view.getContext(), "Data Deleted", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(view.getContext(), Goals.class);
                                    alertDialog.dismiss();
                                    view.getContext().startActivity(intent);
                                    ((Activity)view.getContext()).finish();
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(view.getContext(), "ERROR:"+error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

                closeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) { alertDialog.dismiss(); }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return goalsList.size();
    }

    class FinancialGoalsViewHolder extends RecyclerView.ViewHolder {
        TextView goalName, goalDate, amtHaving, amtNeed;
        ImageButton goalUpdateBtn, goalDeleteBtn;

        public FinancialGoalsViewHolder(@NonNull View itemView) {
            super(itemView);

            goalName = itemView.findViewById(R.id.goalName);
            goalDate = itemView.findViewById(R.id.goalDate);
            amtHaving = itemView.findViewById(R.id.amtHaving);
            amtNeed = itemView.findViewById(R.id.amtNeed);
            goalUpdateBtn = itemView.findViewById(R.id.goalUpdateBtn);
            goalDeleteBtn = itemView.findViewById(R.id.goalDeleteBtn);

        }
    }

    public boolean isEmpty(String s){
        return s.isEmpty();
    }

}
