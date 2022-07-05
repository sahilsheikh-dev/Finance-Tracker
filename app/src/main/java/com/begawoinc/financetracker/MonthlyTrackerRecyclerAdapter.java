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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

public class MonthlyTrackerRecyclerAdapter extends RecyclerView.Adapter<MonthlyTrackerRecyclerAdapter.MonthlyTrackerViewHolder> {

    public static String USERNAME;
    List<MonthlyActivity> activityList;
    String amount, activityDate, dateSt = "";
    int id;
    long count;
    Context context;
    RadioButton activityType;

    public MonthlyTrackerRecyclerAdapter(List<MonthlyActivity> activityList) {
        this.activityList = activityList;
    }

    @NonNull
    @Override
    public MonthlyTrackerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.monthly_activity_item, parent, false);
        MonthlyTrackerViewHolder monthlyTrackerViewHoler = new MonthlyTrackerViewHolder(view);
        context = parent.getContext();
        return monthlyTrackerViewHoler;
    }

    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(@NonNull MonthlyTrackerViewHolder holder, int position) {

//        String.format("%,.2f", Double.parseDouble(amtNeed)).toString().trim()

//        dateSt = "";
//        int monthSt = Integer.parseInt(activityList.get(position).getDate().charAt(5) + "" + activityList.get(position).getDate().charAt(6));
//        if (String.valueOf(monthSt).charAt(0) == 0) monthSt = String.valueOf(monthSt).charAt(1);
//
//        switch (monthSt){
//            case 1:
//                dateSt = dateSt + "January ";
//                break;
//            case 2:
//                dateSt = dateSt + "February ";
//                break;
//            case 3:
//                dateSt = dateSt + "March ";
//                break;
//            case 4:
//                dateSt = dateSt + "April ";
//                break;
//            case 5:
//                dateSt = dateSt + "May ";
//                break;
//            case 6:
//                dateSt = dateSt + "June ";
//                break;
//            case 7:
//                dateSt = dateSt + "July ";
//                break;
//            case 8:
//                dateSt = dateSt + "August ";
//                break;
//            case 9:
//                dateSt = dateSt + "September ";
//                break;
//            case 10:
//                dateSt = dateSt + "October ";
//                break;
//            case 11:
//                dateSt = dateSt + "November ";
//                break;
//            case 12:
//                dateSt = dateSt + "December ";
//                break;
//            default:
//                dateSt = dateSt + "ERROR ";
//                break;
//        }
//
//        dateSt = dateSt + String.valueOf(activityList.get(position).getDate()).trim().charAt(8) + String.valueOf(activityList.get(position).getDate()).trim().charAt(9) + ", ";
//        dateSt = dateSt + String.valueOf(activityList.get(position).getDate()).trim().charAt(0) + String.valueOf(activityList.get(position).getDate()).trim().charAt(1) + String.valueOf(activityList.get(position).getDate()).trim().charAt(2) + String.valueOf(activityList.get(position).getDate()).trim().charAt(3);
//        holder.activityItemDate.setText(dateSt);

//        Monthly on 06/01 (MM/DD)
//        String date = activityList.get(position).getDate();
//        if (date.charAt(0)==0 && date.charAt(1)==0 && date.charAt(2)==0 && date.charAt(3)==0) date = date.substring(5, (date.length()-1)) + " (MM/DD)";
//        else date = date + " (YYYY/MM/DD)";
        holder.activityItemDate.setText(activityList.get(position).getMonthlyActivityType() + " on " + activityList.get(position).getDate().replace("0000/", "") + "(MM/DD)");
        holder.activityItemName.setText(String.valueOf(activityList.get(position).getName()));
        holder.activityItemAmount.setText(formatAmount(activityList.get(position).getAmount(), context));

//        onclick update data
        holder.activityItemUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                id = activityList.get(position).getActivityID();

                SharedPreferences sharedPreferences = view.getContext().getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
                USERNAME = sharedPreferences.getString("USERNAME", "");

                LayoutInflater li = LayoutInflater.from(view.getContext());
                View monthlyActivityView = li.inflate(R.layout.save_monthly_activity, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(view.getContext());
                alertDialogBuilder.setView(monthlyActivityView);

                TextView monthlyActivityHeading = monthlyActivityView.findViewById(R.id.monthlyActivityHeading);
                Button saveMonthlyActivityBtn = monthlyActivityView.findViewById(R.id.saveMonthlyActivityBtn);
                Button closeBtn = monthlyActivityView.findViewById(R.id.closeBtn);
                ProgressBar progressBar = monthlyActivityView.findViewById(R.id.progressBar);
                TextInputLayout monthlyActivityType = monthlyActivityView.findViewById(R.id.monthlyActivityType);
                LinearLayout onceDateSection = monthlyActivityView.findViewById(R.id.onceDateSection);
                LinearLayout monthlyDateSection = monthlyActivityView.findViewById(R.id.monthlyDateSection);

                monthlyActivityHeading.setText("Update your Cash Flow");

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference(USERNAME).child("monthlyTracker").child(String.valueOf(id));

//                myRef.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        if (snapshot.exists()){
//                            MonthlyActivity monthlyActivity = snapshot.getValue(MonthlyActivity.class);
//
//                            TextView incorrectDate = monthlyActivityView.findViewById(R.id.incorrectDate);
//                            TextInputLayout activityNameIn = monthlyActivityView.findViewById(R.id.activityName);
//                            EditText dateIn = monthlyActivityView.findViewById(R.id.date);
//                            EditText monthIn = monthlyActivityView.findViewById(R.id.month);
//                            EditText yearIn = monthlyActivityView.findViewById(R.id.year);
//                            TextInputLayout amountIn = monthlyActivityView.findViewById(R.id.amount);
//
//                            RadioGroup rg = monthlyActivityView.findViewById(R.id.activityType);
//                            if (monthlyActivity.getActivity().toLowerCase().equals("income")) rg.check(R.id.income);
//                            else if (monthlyActivity.getActivity().toLowerCase().equals("expense")) rg.check(R.id.expenses);
//                            else if (monthlyActivity.getActivity().toLowerCase().equals("investment")) rg.check(R.id.investment);
//
//                            activityNameIn.getEditText().setText(monthlyActivity.getName());
////                            YYYY-MM-DD
//                            yearIn.setText(monthlyActivity.getDate().charAt(0)+""+monthlyActivity.getDate().charAt(1)+""+monthlyActivity.getDate().charAt(2)+""+monthlyActivity.getDate().charAt(3));
//                            monthIn.setText(monthlyActivity.getDate().charAt(5)+""+monthlyActivity.getDate().charAt(6));
//                            dateIn.setText(monthlyActivity.getDate().charAt(8)+""+monthlyActivity.getDate().charAt(9));
//                            amountIn.getEditText().setText(String.format("%.2f", monthlyActivity.getAmount()));
//
//                            saveMonthlyActivityBtn.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View view) {
//                                    progressBar.setVisibility(View.VISIBLE);
//
//                                    String activityName = activityNameIn.getEditText().getText().toString().trim();
//                                    activityDate = "";
//                                    activityDate = activityDate + yearIn.getText().toString().trim() + "/";
//                                    if (monthIn.getText().toString().trim().length() == 1) activityDate = activityDate + "0"+monthIn.getText().toString().trim() + "/";
//                                    else activityDate = activityDate + monthIn.getText().toString().trim() + "/";
//                                    if (dateIn.getText().toString().trim().length() == 1) activityDate = activityDate + "0"+dateIn.getText().toString().trim();
//                                    else activityDate = activityDate + dateIn.getText().toString().trim();
//                                    amount = amountIn.getEditText().getText().toString().trim();
//                                    RadioGroup rg = monthlyActivityView.findViewById(R.id.activityType);
//                                    int selectedId = rg.getCheckedRadioButtonId();
//                                    RadioButton activityType = monthlyActivityView.findViewById(selectedId);
//                                    String activity = activityType.getText().toString().trim().toLowerCase();
//
//                                    Date currentDate = new Date();
//
//                                    if (isEmpty(activityName) && isEmpty(amount) &&
//                                            isEmpty(monthIn.getText().toString().trim()) && isEmpty(dateIn.getText().toString().trim()) && isEmpty(yearIn.getText().toString().trim())) {
//                                        activityNameIn.setError("Please Enter Activity Name");
//                                        incorrectDate.setVisibility(View.VISIBLE);
//                                        amountIn.setError("Please Enter Amount");
//                                        progressBar.setVisibility(View.GONE);
//                                    } else if (isEmpty(activityName)) {
//                                        incorrectDate.setVisibility(View.GONE);
//                                        amountIn.setError(null);
//                                        activityNameIn.setError("Please Enter Activity Name");
//                                        progressBar.setVisibility(View.GONE);
//                                    } else if (isEmpty(amount)) {
//                                        activityNameIn.setError(null);
//                                        incorrectDate.setVisibility(View.GONE);
//                                        amountIn.setError("Please Enter Amount");
//                                        progressBar.setVisibility(View.GONE);
//                                    } else if (isEmpty(monthIn.getText().toString().trim()) || isEmpty(dateIn.getText().toString().trim()) || isEmpty(yearIn.getText().toString().trim())) {
//                                        activityNameIn.setError(null);
//                                        amountIn.setError(null);
//                                        incorrectDate.setVisibility(View.VISIBLE);
//                                        progressBar.setVisibility(View.GONE);
//                                    } else if (Integer.parseInt(yearIn.getText().toString().trim()) < (currentDate.getYear()+1900) || String.valueOf(yearIn.getText()).trim().length() < 4) {
//                                        activityNameIn.setError(null);
//                                        amountIn.setError(null);
//                                        incorrectDate.setVisibility(View.VISIBLE);
//                                        progressBar.setVisibility(View.GONE);
//                                    } else if (Integer.parseInt(monthIn.getText().toString().trim()) < 1 || Integer.parseInt(monthIn.getText().toString().trim()) > 12){
//                                        activityNameIn.setError(null);
//                                        amountIn.setError(null);
//                                        incorrectDate.setVisibility(View.VISIBLE);
//                                        progressBar.setVisibility(View.GONE);
//                                    } else {
//                                        activityNameIn.setError(null);
//                                        incorrectDate.setVisibility(View.GONE);
//                                        amountIn.setError(null);
//
//                                        FirebaseDatabase database = FirebaseDatabase.getInstance();
//                                        DatabaseReference myRef = database.getReference(USERNAME).child("monthlyTracker");
//
//                                        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                                            @Override
//                                            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                                                myRef.child(String.valueOf(id)).child("activity").setValue(activity.toString().trim().toLowerCase());
//                                                myRef.child(String.valueOf(id)).child("amount").setValue(Double.parseDouble(amount));
//                                                myRef.child(String.valueOf(id)).child("name").setValue(activityName.toString().trim());
//                                                myRef.child(String.valueOf(id)).child("date").setValue(activityDate.toString().trim());
//
//                                                progressBar.setVisibility(View.GONE);
//                                                Toast.makeText(view.getContext(), "Activity Saved", Toast.LENGTH_LONG).show();
//                                                Intent intent = new Intent(view.getContext(), MonthlyTracker.class);
//                                                alertDialog.dismiss();
//                                                view.getContext().startActivity(intent);
//                                                ((Activity)view.getContext()).finish();
//                                            }
//                                            @Override
//                                            public void onCancelled(DatabaseError databaseError) {
//                                                progressBar.setVisibility(View.GONE);
//                                                Toast.makeText(view.getContext(), "ERROR:"+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
//                                            }
//                                        });
//                                    }
//                                }
//                            });
//                        }
//                    }
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//                        Toast.makeText(view.getContext(), "ERROR::"+error.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });

                if (monthlyActivityType.getEditText().getText().toString().equals("Once")){
                    monthlyDateSection.setVisibility(View.GONE);
                    onceDateSection.setVisibility(View.VISIBLE);
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
                                        myRef.child(String.valueOf(count)).child("monthlyActivityType").setValue(monthlyActivityType.getEditText().getText().toString().trim());

                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(view.getContext(), "Activity Saved", Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(view.getContext(), MonthlyTracker.class);
                                        alertDialog.dismiss();
                                        view.getContext().startActivity(intent);
                                        ((Activity)view.getContext()).finish();
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Toast.makeText(view.getContext(), "ERROR:"+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                        }
                    });
                } else {
                    monthlyDateSection.setVisibility(View.VISIBLE);
                    onceDateSection.setVisibility(View.GONE);
                    saveMonthlyActivityBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            progressBar.setVisibility(View.VISIBLE);
                            TextView incorrectDate = monthlyActivityView.findViewById(R.id.incorrectDate);
                            TextInputLayout activityNameIn = monthlyActivityView.findViewById(R.id.activityName);
                            EditText dateIn = monthlyActivityView.findViewById(R.id.monthlyDate);
                            EditText monthIn = monthlyActivityView.findViewById(R.id.monthlyMonth);
//                                EditText yearIn = monthlyActivityView.findViewById(R.id.year);
                            TextInputLayout amountIn = monthlyActivityView.findViewById(R.id.amount);

                            RadioGroup rg = monthlyActivityView.findViewById(R.id.activityType);
                            int selectedId = rg.getCheckedRadioButtonId();
                            RadioButton activityType = monthlyActivityView.findViewById(selectedId);
                            String activity = activityType.getText().toString().trim().toLowerCase();

                            String activityName = activityNameIn.getEditText().getText().toString().trim();
                            activityDate = "";
//                                activityDate = activityDate + yearIn.getText().toString().trim() + "/";
                            activityDate = activityDate + "0000" + "/";
                            if (monthIn.getText().toString().trim().length() == 1) activityDate = activityDate + "0"+monthIn.getText().toString().trim() + "/";
                            else activityDate = activityDate + monthIn.getText().toString().trim() + "/";
                            if (dateIn.getText().toString().trim().length() == 1) activityDate = activityDate + "0"+dateIn.getText().toString().trim();
                            else activityDate = activityDate + dateIn.getText().toString().trim();
                            String amount = amountIn.getEditText().getText().toString().trim();

                            Date currentDate = new Date();

                            if (isEmpty(activityName) && isEmpty(amount) &&
                                    isEmpty(monthIn.getText().toString().trim()) && isEmpty(dateIn.getText().toString().trim())) {
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
                            } else if (isEmpty(monthIn.getText().toString().trim()) || isEmpty(dateIn.getText().toString().trim())) {
                                activityNameIn.setError(null);
                                amountIn.setError(null);
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
                                        myRef.child(String.valueOf(count)).child("monthlyActivityType").setValue(monthlyActivityType.getEditText().getText().toString().trim());

                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(view.getContext(), "Activity Saved", Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(view.getContext(), MonthlyTracker.class);
                                        alertDialog.dismiss();
                                        view.getContext().startActivity(intent);
                                        ((Activity)view.getContext()).finish();
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Toast.makeText(view.getContext(), "ERROR:"+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                        }
                    });
                }

                closeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) { alertDialog.dismiss(); }
                });
            }
        });

//        onclick delete data
        holder.activityItemDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                id = activityList.get(position).getActivityID();
                SharedPreferences sharedPreferences = view.getContext().getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
                USERNAME = sharedPreferences.getString("USERNAME", "");

                LayoutInflater li = LayoutInflater.from(view.getContext());
                View monthlyActivityView = li.inflate(R.layout.delete_dialog, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(view.getContext());
                alertDialogBuilder.setView(monthlyActivityView);

                Button deleteGoalBtn = monthlyActivityView.findViewById(R.id.deleteGoalBtn);
                Button closeBtn = monthlyActivityView.findViewById(R.id.closeBtn);
                ProgressBar progressBar = monthlyActivityView.findViewById(R.id.progressBar);

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference(USERNAME).child("monthlyTracker");

                deleteGoalBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        progressBar.setVisibility(View.VISIBLE);
//                        myRef.removeValue();
                        final int[] check = {0};
                        myRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                count = (long) snapshot.child("monthlyTrackerMaxCount").getValue();
                                if (count != 0 && count > 0 && check[0] != 1) {
                                    if (id == count || id > count) count -= 1;
                                    myRef.child("monthlyTrackerMaxCount").setValue(count);
                                    myRef.child(String.valueOf(id)).removeValue();
                                    check[0]++;
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(view.getContext(), "Data Deleted", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(view.getContext(), MonthlyTracker.class);
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
        return activityList.size();
    }

    class MonthlyTrackerViewHolder extends RecyclerView.ViewHolder {
        TextView activityItemName, activityItemDate, activityItemAmount;
        ImageButton activityItemUpdateBtn, activityItemDeleteBtn;

        public MonthlyTrackerViewHolder(@NonNull View itemView) {
            super(itemView);

            activityItemName = itemView.findViewById(R.id.activityItemName);
            activityItemDate = itemView.findViewById(R.id.activityItemDate);
            activityItemAmount = itemView.findViewById(R.id.activityItemAmount);
            activityItemUpdateBtn = itemView.findViewById(R.id.activityItemUpdateBtn);
            activityItemDeleteBtn = itemView.findViewById(R.id.activityItemDeleteBtn);

        }
    }

    public boolean isEmpty(String s){
        return s.isEmpty();
    }

    public String formatAmount(double amount, Context context){
        String formatedAmount = "";

        if (!isDollar(context)) formatedAmount = formatedAmount + "$";
        else formatedAmount = formatedAmount + "₹";

        if (!inLakhsCrore(context)){
            if (Double.parseDouble(String.valueOf(amount)) < 1000 && Double.parseDouble(String.valueOf(amount)) >= 0) {
                formatedAmount = formatedAmount + removeZeroes(String.format("%,.2f", amount).toString().trim()) + "/-";
            } else if (Double.parseDouble(String.valueOf(amount)) < 100000 && Double.parseDouble(String.valueOf(amount)) >= 1000) {
//                double tempAmount = Double.parseDouble(String.valueOf(amount))/1000;
//                formatedAmount = formatedAmount + removeZeroes(String.format("%,.2f", tempAmount).toString().trim())+" K";
                formatedAmount = formatedAmount + removeZeroes(String.format("%,.2f", amount).toString().trim());
            } else if (Double.parseDouble(String.valueOf(amount)) >= 100000 && Double.parseDouble(String.valueOf(amount)) < 10000000){
                double tempAmount = Double.parseDouble(String.valueOf(amount))/100000;
                formatedAmount = formatedAmount + removeZeroes(String.format("%,.2f", tempAmount).toString().trim())+" Lakh";
            } else if (Double.parseDouble(String.valueOf(amount)) >= 10000000){
                double tempAmount = Double.parseDouble(String.valueOf(amount))/10000000;
                formatedAmount = formatedAmount + removeZeroes(String.format("%,.2f", tempAmount).toString().trim())+" Crore";
            } else if (Double.parseDouble(String.valueOf(amount)) > -1000 && Double.parseDouble(String.valueOf(amount)) < 0) {
                formatedAmount = formatedAmount + removeZeroes(String.format("%,.2f", amount).toString().trim()) + "/-";
            } else if (Double.parseDouble(String.valueOf(amount)) > -100000 && Double.parseDouble(String.valueOf(amount)) <= -1000) {
//                double tempAmount = Double.parseDouble(String.valueOf(amount))/1000;
//                formatedAmount = formatedAmount + removeZeroes(String.format("%,.2f", tempAmount).toString().trim())+" K";
                formatedAmount = formatedAmount + removeZeroes(String.format("%,.2f", amount).toString().trim());
            } else if (Double.parseDouble(String.valueOf(amount)) <= -100000 && Double.parseDouble(String.valueOf(amount)) > -10000000){
                double tempAmount = Double.parseDouble(String.valueOf(amount))/100000;
                formatedAmount = formatedAmount + removeZeroes(String.format("%,.2f", tempAmount).toString().trim())+" Lakh";
            } else if (Double.parseDouble(String.valueOf(amount)) <= -10000000){
                double tempAmount = Double.parseDouble(String.valueOf(amount))/10000000;
                formatedAmount = formatedAmount + removeZeroes(String.format("%,.2f", tempAmount).toString().trim())+" Crore";
            }
        } else {
            if (Double.parseDouble(String.valueOf(amount)) < 1000 && Double.parseDouble(String.valueOf(amount)) >= 0) {
                formatedAmount = formatedAmount + removeZeroes(String.format("%,.2f", amount).toString().trim()) + "/-";
            } else if (Double.parseDouble(String.valueOf(amount)) < 1000000 && Double.parseDouble(String.valueOf(amount)) >= 1000) {
//                double tempAmount = Double.parseDouble(String.valueOf(amount))/1000;
//                formatedAmount = formatedAmount + removeZeroes(String.format("%,.2f", tempAmount).toString().trim())+" K";
                formatedAmount = formatedAmount + removeZeroes(String.format("%,.2f", amount).toString().trim());
            } else if (Double.parseDouble(String.valueOf(amount)) >= 1000000 && Double.parseDouble(String.valueOf(amount)) < 1000000000){
                double tempAmount = Double.parseDouble(String.valueOf(amount))/1000000;
                formatedAmount = formatedAmount + removeZeroes(String.format("%,.2f", tempAmount).toString().trim())+" M";
            } else if (Double.parseDouble(String.valueOf(amount)) >= 1000000000){
                double tempAmount = Double.parseDouble(String.valueOf(amount))/1000000000;
                formatedAmount = formatedAmount + removeZeroes(String.format("%,.2f", tempAmount).toString().trim())+" B";
            } else if (Double.parseDouble(String.valueOf(amount)) > -1000 && Double.parseDouble(String.valueOf(amount)) < 0) {
                formatedAmount = formatedAmount + removeZeroes(String.format("%,.2f", amount).toString().trim()) + "/-";
            } else if (Double.parseDouble(String.valueOf(amount)) > -1000000 && Double.parseDouble(String.valueOf(amount)) <= -1000) {
//                double tempAmount = Double.parseDouble(String.valueOf(amount))/1000;
//                formatedAmount = formatedAmount + removeZeroes(String.format("%,.2f", tempAmount).toString().trim())+" K";
                formatedAmount = formatedAmount + removeZeroes(String.format("%,.2f", amount).toString().trim());
            } else if (Double.parseDouble(String.valueOf(amount)) <= -1000000 && Double.parseDouble(String.valueOf(amount)) > -1000000000){
                double tempAmount = Double.parseDouble(String.valueOf(amount))/1000000;
                formatedAmount = formatedAmount + removeZeroes(String.format("%,.2f", tempAmount).toString().trim())+" M";
            } else if (Double.parseDouble(String.valueOf(amount)) <= -1000000000){
                double tempAmount = Double.parseDouble(String.valueOf(amount))/1000000000;
                formatedAmount = formatedAmount + removeZeroes(String.format("%,.2f", tempAmount).toString().trim())+" B";
            }
        }

        return formatedAmount;
    }

    public boolean isDollar(Context context) {
        this.context = context;
        SharedPreferences sharedPreferences = context.getSharedPreferences("currencyDetails", Context.MODE_PRIVATE);
        return sharedPreferences.getString("isDollar", "").isEmpty()
                || sharedPreferences.getString("isDollar", "").equalsIgnoreCase(null)
                || sharedPreferences.getString("isDollar", "").equalsIgnoreCase("");
    }

    public boolean inLakhsCrore(Context context) {
        this.context = context;
        SharedPreferences sharedPreferences = context.getSharedPreferences("numberFormatDetails", Context.MODE_PRIVATE);
        return sharedPreferences.getString("inLakhsCrore", "").isEmpty()
                || sharedPreferences.getString("inLakhsCrore", "").equalsIgnoreCase(null)
                || sharedPreferences.getString("inLakhsCrore", "").equalsIgnoreCase("");
    }

    public String removeZeroes(String number){
        int dot = number.length()-3;
        if (number.charAt(number.length()-1) == 0 || number.charAt(number.length()-1) == '0') return number.substring(0, dot);
        else return number;
    }

}
