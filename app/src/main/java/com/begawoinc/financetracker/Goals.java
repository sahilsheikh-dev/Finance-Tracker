package com.begawoinc.financetracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class Goals extends AppCompatActivity {

    public static String USERNAME;
    BottomNavigationView bottomNavigationView;
    Button addGoals;
    ProgressBar progressBar;
    RecyclerView recyclerView;
    FinancialGoalsRecyclerAdapter financialGoalsRecyclerAdapter;
    List<FinancialGoals> goalsList;
    FirebaseAuth mAuth;
    final Context context = this;
    String amtHaving, goalDate;
    double totalAmtNeededSt, totalAmtHavingSt;
    long count;
    TextView totalAmtHaving, totalAmtNeeded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goals);

        getSupportActionBar().hide();

        SharedPreferences sharedPreferences = getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
//        USERNAME = getIntent().getExtras().getString("USERNAME");
        USERNAME = sharedPreferences.getString("USERNAME", "");
        bottomNavigationView = findViewById(R.id.bottom_navigator);
        bottomNavigationView.setSelectedItemId(R.id.goals);
        recyclerView = findViewById(R.id.goalsRecyclerView);
        financialGoalsRecyclerAdapter = new FinancialGoalsRecyclerAdapter(goalsList);
        addGoals = findViewById(R.id.addGoals);
        mAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar);
        totalAmtHaving = findViewById(R.id.totalAmtHaving);
        totalAmtNeeded = findViewById(R.id.totalAmtNeeded);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home:
                        Intent intentAbout = new Intent(Goals.this, MainActivity.class);
                        intentAbout.putExtra("USERNAME", USERNAME);
                        startActivity(intentAbout);
                        overridePendingTransition(0,0);
                        finish();
                        return true;

                    case R.id.expenses:
                        Intent intentExpenses = new Intent(Goals.this, Expenses.class);
                        intentExpenses.putExtra("USERNAME", USERNAME);
                        startActivity(intentExpenses);
                        overridePendingTransition(0,0);
                        finish();
                        return true;

                    case R.id.profile:
                        Intent intentHistory = new Intent(Goals.this, Profile.class);
                        intentHistory.putExtra("USERNAME", USERNAME);
                        startActivity(intentHistory);
                        overridePendingTransition(0,0);
                        finish();
                        return true;

                    case R.id.goals:
                        return true;
                }
                return false;
            }
        });

//            write code here
        progressBar.setVisibility(View.VISIBLE);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(USERNAME).child("financialGoals");

        goalsList = new ArrayList<>();
        financialGoalsRecyclerAdapter = new FinancialGoalsRecyclerAdapter(goalsList);
        recyclerView.setAdapter(financialGoalsRecyclerAdapter);

//        to get all the data of financial goals
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressBar.setVisibility(View.VISIBLE);
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    if (!dataSnapshot.getKey().equals("financialGoalMaxCount")) {
                        FinancialGoals goal = dataSnapshot.getValue(FinancialGoals.class);
                        goalsList.add(goal);
                    }
                }

                Collections.sort(goalsList, new Comparator<FinancialGoals>() {
                    @Override
                    public int compare(FinancialGoals financialGoals1, FinancialGoals financialGoals2) {
                        if (financialGoals1.getGoalDate() == null || financialGoals2.getGoalDate() == null) return 0;
                        else return Long.compare(Long.parseLong(financialGoals1.getGoalDate().replace("/", "")), Long.parseLong(financialGoals2.getGoalDate().replace("/", "")));
                    }
                });

                recyclerView.setAdapter(financialGoalsRecyclerAdapter);
                financialGoalsRecyclerAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(Goals.this, "ERROR::"+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

//        to count total need of money
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressBar.setVisibility(View.VISIBLE);
                long i = (long) snapshot.child("financialGoalMaxCount").getValue();
                while (snapshot.exists() && i != 0){
                    System.out.println("COUNT::"+i+"/4");
                    myRef.child(String.valueOf(i)).child("amtNeed").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                totalAmtNeededSt = totalAmtNeededSt + Double.parseDouble(String.valueOf(snapshot.getValue()));
                                if (Double.parseDouble(String.valueOf(totalAmtNeededSt)) < 1000) {
                                    totalAmtNeeded.setText("₹" + String.format("%,.2f", totalAmtNeededSt).toString().trim() + "/-");
                                } else if (Double.parseDouble(String.valueOf(totalAmtNeededSt)) < 1000000 && Double.parseDouble(String.valueOf(totalAmtNeededSt)) >= 1000) {
                                    double tempAmount = Double.parseDouble(String.valueOf(totalAmtNeededSt))/1000;
                                    totalAmtNeeded.setText("₹"+String.format("%,.2f", tempAmount).toString().trim()+" K");
                                } else if (Double.parseDouble(String.valueOf(totalAmtNeededSt)) >= 1000000 && Double.parseDouble(String.valueOf(totalAmtNeededSt)) < 1000000000){
                                    double tempAmount = Double.parseDouble(String.valueOf(totalAmtNeededSt))/1000000;
                                    totalAmtNeeded.setText("₹"+String.format("%,.2f", tempAmount).toString().trim()+" M");
                                } else if (Double.parseDouble(String.valueOf(totalAmtNeededSt)) >= 1000000000){
                                    double tempAmount = Double.parseDouble(String.valueOf(totalAmtNeededSt))/1000000000;
                                    totalAmtNeeded.setText("₹"+String.format("%,.2f", tempAmount).toString().trim()+" B");
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(Goals.this, "ERROR:"+error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    i--;
                }
                progressBar.setVisibility(View.GONE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(Goals.this, "ERROR::"+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

//        to count total have of money
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressBar.setVisibility(View.VISIBLE);
                long i = (long) snapshot.child("financialGoalMaxCount").getValue();
                while (snapshot.exists() && i != 0){
                    myRef.child(String.valueOf(i)).child("amtHaving").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                totalAmtHavingSt = totalAmtHavingSt + Double.parseDouble(String.valueOf(snapshot.getValue()));
                                if (Double.parseDouble(String.valueOf(totalAmtHavingSt)) < 1000) {
                                    totalAmtHaving.setText("₹" + String.format("%,.2f", totalAmtHavingSt).toString().trim() + "/-");
                                } else if (Double.parseDouble(String.valueOf(totalAmtHavingSt)) < 1000000 && Double.parseDouble(String.valueOf(totalAmtHavingSt)) >= 1000) {
                                    double tempAmount = Double.parseDouble(String.valueOf(totalAmtHavingSt))/1000;
                                    totalAmtHaving.setText("₹"+String.format("%,.2f", tempAmount).toString().trim()+" K");
                                } else if (Double.parseDouble(String.valueOf(totalAmtHavingSt)) >= 1000000 && Double.parseDouble(String.valueOf(totalAmtHavingSt)) < 1000000000){
                                    double tempAmount = Double.parseDouble(String.valueOf(totalAmtHavingSt))/1000000;
                                    totalAmtHaving.setText("₹"+String.format("%,.2f", tempAmount).toString().trim()+" M");
                                } else if (Double.parseDouble(String.valueOf(totalAmtHavingSt)) >= 1000000000){
                                    double tempAmount = Double.parseDouble(String.valueOf(totalAmtHavingSt))/1000000000;
                                    totalAmtHaving.setText("₹"+String.format("%,.2f", tempAmount).toString().trim()+" B");
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(Goals.this, "ERROR:"+error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    i--;
                }
                progressBar.setVisibility(View.GONE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(Goals.this, "ERROR::"+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

//        onClick listner for add goals button
        addGoals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LayoutInflater li = LayoutInflater.from(context);
                View goalView = li.inflate(R.layout.save_financial_goal, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setView(goalView);

                TextView goalHeading = goalView.findViewById(R.id.goalHeading);
                Button saveGoalBtn = goalView.findViewById(R.id.saveGoalBtn);
                Button closeBtn = goalView.findViewById(R.id.closeBtn);

                goalHeading.setText("Save your financial goal");

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

                saveGoalBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        progressBar.setVisibility(View.VISIBLE);
                        TextView incorrectDate = goalView.findViewById(R.id.incorrectDate);
                        TextInputLayout goalNameIn = goalView.findViewById(R.id.goalName);
                        EditText dateIn = goalView.findViewById(R.id.date);
                        EditText monthIn = goalView.findViewById(R.id.month);
                        EditText yearIn = goalView.findViewById(R.id.year);
                        TextInputLayout amtNeedIn = goalView.findViewById(R.id.amtNeed);
                        TextInputLayout amtHavingIn = goalView.findViewById(R.id.amtHaving);

                        String goalName = goalNameIn.getEditText().getText().toString().trim();

                        goalDate = "";

                        goalDate = goalDate + yearIn.getText().toString().trim() + "/";

                        if (dateIn.getText().toString().trim().length() == 1) goalDate = goalDate + "0"+dateIn.getText().toString().trim() + "/";
                        else goalDate = goalDate + dateIn.getText().toString().trim() + "/";

                        if (monthIn.getText().toString().trim().length() == 1) goalDate = goalDate + "0"+monthIn.getText().toString().trim();
                        else goalDate = goalDate + monthIn.getText().toString().trim();

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
                        } else {
                            progressBar.setVisibility(View.VISIBLE);
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
                                    count = (long) dataSnapshot.child("financialGoalMaxCount").getValue()+1;
                                    myRef.child("financialGoalMaxCount").setValue(count);
                                    myRef.child(String.valueOf(count)).child("goalId").setValue(Integer.parseInt(String.valueOf(count)));
                                    myRef.child(String.valueOf(count)).child("amtNeed").setValue(Double.parseDouble(amtNeed));
                                    myRef.child(String.valueOf(count)).child("amtHaving").setValue(Double.parseDouble(amtHaving));
                                    myRef.child(String.valueOf(count)).child("goal").setValue(goalName.toString().trim());
                                    myRef.child(String.valueOf(count)).child("goalDate").setValue(goalDate.toString().trim());

                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(Goals.this, "Goal Saved", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(Goals.this, Goals.class);
                                    startActivity(intent);
                                    finish();
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Toast.makeText(Goals.this, "ERROR:"+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });

                closeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });

            }
        });

    }

    public boolean isEmpty(String s){
        return s.isEmpty();
    }

}