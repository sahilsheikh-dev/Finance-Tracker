package com.begawoinc.financetracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MonthlyTracker extends AppCompatActivity {

    public static String USERNAME;
    BottomNavigationView bottomNavigationView;
    ProgressBar progressBar;
    RecyclerView expensesRecyclerView, incomesRecyclerView, investmentsRecyclerView;
    MonthlyTrackerRecyclerAdapter expensesMTRecyclerAdapter, incomeMTRecyclerAdapter, investmentMTRecyclerAdapter;
    List<MonthlyActivity> expensesMonthlyActivities, incomeMonthlyActivities, investmentsMonthlyActivities, monthlyActivities;
    FirebaseAuth mAuth;
    final Context context = this;
    long count, incomeAmount, expenseAmount, investmentAmount, remaningAmount;
    float incomePer, expensePer, investmentPer, remaningPer;
    String activityDate;
    TextView incomeAmt, incomePercentage, investmentAmt, investmentPercentage, expenseAmt, expensePercentage, remaningAmt, remaningPercentage, noExpenseDataAvailable, noIncomeDataAvailable, noInvestmentDataAvailable;
    ImageButton showExpenses, hideExpenses, showIncomes, hideIncomes, showInvestments, hideInvestments, addMonthlyActivity;
    View incomrBar, expenseBar, investmentBar, remaningBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly_tracker);

        getSupportActionBar().hide();

        SharedPreferences sharedPreferences = getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        boolean isUsernameEmpty = sharedPreferences.getString("USERNAME", "").isEmpty();

        if (isUsernameEmpty){
            Intent intent = new Intent(MonthlyTracker.this, Login.class );
            startActivity(intent);
            finish();
        } else {
//            USERNAME = getIntent().getExtras().getString("USERNAME");
            USERNAME = sharedPreferences.getString("USERNAME", "");
            bottomNavigationView = findViewById(R.id.bottom_navigator);
            bottomNavigationView.setSelectedItemId(R.id.expenses);
            progressBar = findViewById(R.id.progressBar);
            expensesRecyclerView = findViewById(R.id.expensesRecyclerView);
            incomesRecyclerView = findViewById(R.id.incomesRecyclerView);
            investmentsRecyclerView = findViewById(R.id.investmentsRecyclerView);
            incomeAmt = findViewById(R.id.incomeAmt);
            incomePercentage = findViewById(R.id.incomePercentage);
            expenseAmt = findViewById(R.id.expenseAmt);
            expensePercentage = findViewById(R.id.expensePercentage);
            investmentAmt = findViewById(R.id.investmentAmt);
            investmentPercentage = findViewById(R.id.investmentPercentage);
            remaningAmt = findViewById(R.id.remaningAmt);
            remaningPercentage = findViewById(R.id.remaningPercentage);
            noExpenseDataAvailable = findViewById(R.id.noExpenseDataAvailable);
            noIncomeDataAvailable = findViewById(R.id.noIncomeDataAvailable);
            noInvestmentDataAvailable = findViewById(R.id.noInvestmentDataAvailable);
            showExpenses = findViewById(R.id.showExpenses);
            hideExpenses = findViewById(R.id.hideExpenses);
            showIncomes = findViewById(R.id.showIncomes);
            hideIncomes = findViewById(R.id.hideIncomes);
            showInvestments = findViewById(R.id.showInvestments);
            hideInvestments = findViewById(R.id.hideInvestments);
            addMonthlyActivity = findViewById(R.id.addMonthlyActivity);
            incomrBar = findViewById(R.id.incomeBar);
            expenseBar = findViewById(R.id.expenseBar);
            investmentBar = findViewById(R.id.investmentBar);
            remaningBar = findViewById(R.id.remaningBar);

            bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.home:
                            Intent intentExpenses = new Intent(MonthlyTracker.this, MainActivity.class);
                            intentExpenses.putExtra("USERNAME", USERNAME);
                            startActivity(intentExpenses);
                            overridePendingTransition(0, 0);
                            finish();
                            return true;

                        case R.id.goals:
                            Intent intentHistory = new Intent(MonthlyTracker.this, Goals.class);
                            intentHistory.putExtra("USERNAME", USERNAME);
                            startActivity(intentHistory);
                            overridePendingTransition(0, 0);
                            finish();
                            return true;

                        case R.id.profile:
                            Intent intentAbout = new Intent(MonthlyTracker.this, Profile.class);
                            intentAbout.putExtra("USERNAME", USERNAME);
                            startActivity(intentAbout);
                            overridePendingTransition(0, 0);
                            finish();
                            return true;

                        case R.id.expenses:
                            return true;
                    }
                    return false;
                }
            });

//            write code here
            progressBar.setVisibility(View.VISIBLE);
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference(USERNAME).child("monthlyTracker");

            expensesMonthlyActivities = new ArrayList<>();
            expensesMTRecyclerAdapter = new MonthlyTrackerRecyclerAdapter(expensesMonthlyActivities);
            expensesRecyclerView.setAdapter(expensesMTRecyclerAdapter);

            incomeMonthlyActivities = new ArrayList<>();
            incomeMTRecyclerAdapter = new MonthlyTrackerRecyclerAdapter(incomeMonthlyActivities);
            incomesRecyclerView.setAdapter(incomeMTRecyclerAdapter);

            investmentsMonthlyActivities = new ArrayList<>();
            investmentMTRecyclerAdapter = new MonthlyTrackerRecyclerAdapter(investmentsMonthlyActivities);
            expensesRecyclerView.setAdapter(investmentMTRecyclerAdapter);

            monthlyActivities = new ArrayList<>();

//        to get all the data of monthly activities
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    progressBar.setVisibility(View.VISIBLE);
                    if (snapshot.exists()){
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                            if (!dataSnapshot.getKey().equals("monthlyTrackerMaxCount")) {
                                MonthlyActivity activity = dataSnapshot.getValue(MonthlyActivity.class);
                                monthlyActivities.add(activity);
                            }
                        }

                        Collections.sort(monthlyActivities, new Comparator<MonthlyActivity>() {
                            @Override
                            public int compare(MonthlyActivity m1, MonthlyActivity m2) {
                                if (m1.getDate() == null || m2.getDate() == null) return 0;
                                else return Long.compare(Long.parseLong(m1.getDate().replace("/", "")), Long.parseLong(m2.getDate().replace("/", "")));
                            }
                        });

                        incomeAmount = 0;
                        expenseAmount = 0;
                        investmentAmount = 0;
                        remaningAmount = 0;
                        incomePer = 0.0f;
                        expensePer = 0.0f;
                        investmentPer = 0.0f;
                        remaningPer = 0.0f;

                        for (MonthlyActivity activity: monthlyActivities){
                            if (activity.getActivity().equals("income")) incomeMonthlyActivities.add(activity);
                            else if (activity.getActivity().equals("expense")) expensesMonthlyActivities.add(activity);
                            else if (activity.getActivity().equals("investment")) investmentsMonthlyActivities.add(activity);
                        }

                        for (MonthlyActivity income: incomeMonthlyActivities) incomeAmount += income.getAmount();
                        for (MonthlyActivity expense: expensesMonthlyActivities) expenseAmount += expense.getAmount();
                        for (MonthlyActivity investment: investmentsMonthlyActivities) investmentAmount += investment.getAmount();

                        remaningAmount = incomeAmount - (expenseAmount + investmentAmount);

                        incomePer = 100;
                        expensePer = percentageCalculate(expenseAmount, incomeAmount);
                        investmentPer = percentageCalculate(investmentAmount, incomeAmount);
                        remaningPer = percentageCalculate(remaningAmount, incomeAmount);

                        incomeAmt.setText(formatAmount(incomeAmount));
                        expenseAmt.setText(formatAmount(expenseAmount));
                        investmentAmt.setText(formatAmount(investmentAmount));
                        remaningAmt.setText(formatAmount(remaningAmount));

                        incomePercentage.setText(String.format("%.2f", incomePer).toString().trim() + "%");
                        expensePercentage.setText(String.format("%.2f", expensePer).toString().trim() + "%");
                        investmentPercentage.setText(String.format("%.2f", investmentPer).toString().trim() + "%");
                        remaningPercentage.setText(String.format("%.2f", remaningPer).toString().trim() + "%");

                        int expenseWidth = (int) (expensePer * (float) expenseBar.getWidth()) / 100;
                        ViewGroup.LayoutParams expenseLayoutParams = expenseBar.getLayoutParams();
                        expenseLayoutParams.width = expenseWidth;
                        expenseBar.setLayoutParams(expenseLayoutParams);

                        int investmentWidth = (int) (investmentPer * (float) investmentBar.getWidth()) / 100;
                        ViewGroup.LayoutParams investmentLayoutParams = investmentBar.getLayoutParams();
                        investmentLayoutParams.width = investmentWidth;
                        investmentBar.setLayoutParams(investmentLayoutParams);

                        int remaningWidth = (int) (remaningPer * (float) remaningBar.getWidth()) / 100;
                        ViewGroup.LayoutParams remaningLayoutParams = remaningBar.getLayoutParams();
                        remaningLayoutParams.width = remaningWidth;
                        remaningBar.setLayoutParams(remaningLayoutParams);


                        if (incomeMonthlyActivities.size() == 0) noIncomeDataAvailable.setVisibility(View.VISIBLE);
                        else noIncomeDataAvailable.setVisibility(View.GONE);
                        if (expensesMonthlyActivities.size() == 0) noExpenseDataAvailable.setVisibility(View.VISIBLE);
                        else noExpenseDataAvailable.setVisibility(View.GONE);
                        if (investmentsMonthlyActivities.size() == 0) noInvestmentDataAvailable.setVisibility(View.VISIBLE);
                        else noInvestmentDataAvailable.setVisibility(View.GONE);

                        expensesRecyclerView.setAdapter(expensesMTRecyclerAdapter);
                        expensesMTRecyclerAdapter.notifyDataSetChanged();
                        incomesRecyclerView.setAdapter(incomeMTRecyclerAdapter);
                        incomeMTRecyclerAdapter.notifyDataSetChanged();
                        investmentsRecyclerView.setAdapter(investmentMTRecyclerAdapter);
                        investmentMTRecyclerAdapter.notifyDataSetChanged();

                    }
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(MonthlyTracker.this, "ERROR::"+error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });



//            show and hide operations
            showExpenses.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    expensesRecyclerView.setVisibility(View.VISIBLE);
                    hideExpenses.setVisibility(View.VISIBLE);
                    showExpenses.setVisibility(View.GONE);
                }
            });

            hideExpenses.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    expensesRecyclerView.setVisibility(View.GONE);
                    showExpenses.setVisibility(View.VISIBLE);
                    hideExpenses.setVisibility(View.GONE);
                }
            });

            showIncomes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    incomesRecyclerView.setVisibility(View.VISIBLE);
                    hideIncomes.setVisibility(View.VISIBLE);
                    showIncomes.setVisibility(View.GONE);
                }
            });

            hideIncomes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    incomesRecyclerView.setVisibility(View.GONE);
                    showIncomes.setVisibility(View.VISIBLE);
                    hideIncomes.setVisibility(View.GONE);
                }
            });

            showInvestments.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    investmentsRecyclerView.setVisibility(View.VISIBLE);
                    hideInvestments.setVisibility(View.VISIBLE);
                    showInvestments.setVisibility(View.GONE);
                }
            });

            hideInvestments.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    investmentsRecyclerView.setVisibility(View.GONE);
                    showInvestments.setVisibility(View.VISIBLE);
                    hideInvestments.setVisibility(View.GONE);
                }
            });

//            add monthly activity operation
            addMonthlyActivity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    LayoutInflater li = LayoutInflater.from(context);
                    View monthlyActivityView = li.inflate(R.layout.save_monthly_activity, null);
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                    alertDialogBuilder.setView(monthlyActivityView);

                    TextView monthlyActivityHeading = monthlyActivityView.findViewById(R.id.monthlyActivityHeading);
                    Button saveMonthlyActivityBtn = monthlyActivityView.findViewById(R.id.saveMonthlyActivityBtn);
                    Button closeBtn = monthlyActivityView.findViewById(R.id.closeBtn);

                    monthlyActivityHeading.setText("Save your\nCash Flow");

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();

                    saveMonthlyActivityBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            progressBar.setVisibility(View.VISIBLE);
                            TextView incorrectDate = monthlyActivityView.findViewById(R.id.incorrectDate);
                            TextInputLayout activityNameIn = monthlyActivityView.findViewById(R.id.activityName);
                            EditText dateIn = monthlyActivityView.findViewById(R.id.date);
                            EditText monthIn = monthlyActivityView.findViewById(R.id.month);
                            EditText yearIn = monthlyActivityView.findViewById(R.id.year);
                            TextInputLayout amountIn = monthlyActivityView.findViewById(R.id.amount);

                            RadioGroup rg = monthlyActivityView.findViewById(R.id.activityType);
                            int selectedId = rg.getCheckedRadioButtonId();
                            RadioButton activityType = monthlyActivityView.findViewById(selectedId);
                            String activity = activityType.getText().toString().trim().toLowerCase();

                            String activityName = activityNameIn.getEditText().getText().toString().trim();
                            activityDate = "";
                            activityDate = activityDate + yearIn.getText().toString().trim() + "/";
                            if (monthIn.getText().toString().trim().length() == 1) activityDate = activityDate + "0"+monthIn.getText().toString().trim() + "/";
                            else activityDate = activityDate + monthIn.getText().toString().trim() + "/";
                            if (dateIn.getText().toString().trim().length() == 1) activityDate = activityDate + "0"+dateIn.getText().toString().trim();
                            else activityDate = activityDate + dateIn.getText().toString().trim();
                            String amount = amountIn.getEditText().getText().toString().trim();

                            Date currentDate = new Date();

                            if (isEmpty(activityName) && isEmpty(amount) &&
                                    isEmpty(monthIn.getText().toString().trim()) && isEmpty(dateIn.getText().toString().trim()) && isEmpty(yearIn.getText().toString().trim())) {
                                activityNameIn.setError("Please Enter Activity Name");
                                incorrectDate.setVisibility(View.VISIBLE);
                                amountIn.setError("Please Enter Amount");
                                progressBar.setVisibility(View.GONE);
                            } else if (isEmpty(activityName)) {
                                incorrectDate.setVisibility(View.GONE);
                                amountIn.setError(null);
                                activityNameIn.setError("Please Enter Activity Name");
                                progressBar.setVisibility(View.GONE);
                            } else if (isEmpty(amount)) {
                                activityNameIn.setError(null);
                                incorrectDate.setVisibility(View.GONE);
                                amountIn.setError("Please Enter Amount");
                                progressBar.setVisibility(View.GONE);
                            } else if (isEmpty(monthIn.getText().toString().trim()) || isEmpty(dateIn.getText().toString().trim()) || isEmpty(yearIn.getText().toString().trim())) {
                                activityNameIn.setError(null);
                                amountIn.setError(null);
                                amountIn.setError(null);
                                incorrectDate.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);
                            } else if (Integer.parseInt(yearIn.getText().toString().trim()) < (currentDate.getYear()+1900) || String.valueOf(yearIn.getText()).trim().length() < 4) {
                                activityNameIn.setError(null);
                                amountIn.setError(null);
                                incorrectDate.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);
                            } else if (Integer.parseInt(monthIn.getText().toString().trim()) < 1 || Integer.parseInt(monthIn.getText().toString().trim()) > 12){
                                activityNameIn.setError(null);
                                amountIn.setError(null);
                                incorrectDate.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);
                            } else {
                                progressBar.setVisibility(View.VISIBLE);
                                activityNameIn.setError(null);
                                incorrectDate.setVisibility(View.GONE);
                                amountIn.setError(null);

                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference myRef = database.getReference(USERNAME).child("monthlyTracker");

                                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        if (dataSnapshot.child("monthlyTrackerMaxCount").exists()) count = (long) dataSnapshot.child("monthlyTrackerMaxCount").getValue()+1;
                                        else count = 1;

                                        myRef.child("monthlyTrackerMaxCount").setValue(count);
                                        myRef.child(String.valueOf(count)).child("activity").setValue(activity.toString().trim().toLowerCase());
                                        myRef.child(String.valueOf(count)).child("activityID").setValue(Integer.parseInt(String.valueOf(count)));
                                        myRef.child(String.valueOf(count)).child("amount").setValue(Double.parseDouble(amount));
                                        myRef.child(String.valueOf(count)).child("name").setValue(activityName.toString().trim());
                                        myRef.child(String.valueOf(count)).child("date").setValue(activityDate.toString().trim());

                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(MonthlyTracker.this, "Activity Saved", Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(MonthlyTracker.this, MonthlyTracker.class);
                                        alertDialog.dismiss();
                                        startActivity(intent);
                                        finish();
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Toast.makeText(MonthlyTracker.this, "ERROR:"+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
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

    }

    public boolean isEmpty(String s){
        return s.isEmpty();
    }

    public String formatAmount(double amount){
        String formatedAmount = "";
        if (Double.parseDouble(String.valueOf(amount)) < 1000) {
            formatedAmount = String.format("%,.2f", amount).toString().trim() + "/-";
        } else if (Double.parseDouble(String.valueOf(amount)) < 1000000 && Double.parseDouble(String.valueOf(amount)) >= 1000) {
            double tempAmount = Double.parseDouble(String.valueOf(amount))/1000;
            formatedAmount = String.format("%,.2f", tempAmount).toString().trim()+" K";
        } else if (Double.parseDouble(String.valueOf(amount)) >= 1000000 && Double.parseDouble(String.valueOf(amount)) < 1000000000){
            double tempAmount = Double.parseDouble(String.valueOf(amount))/1000000;
            formatedAmount = String.format("%,.2f", tempAmount).toString().trim()+" M";
        } else if (Double.parseDouble(String.valueOf(amount)) >= 1000000000){
            double tempAmount = Double.parseDouble(String.valueOf(amount))/1000000000;
            formatedAmount = String.format("%,.2f", tempAmount).toString().trim()+" B";
        }
        return formatedAmount;
    }

    public float percentageCalculate(float have, float total){
        if (total <= 0) total = 1;
        return (float) ((have/total)*100);
    }

}