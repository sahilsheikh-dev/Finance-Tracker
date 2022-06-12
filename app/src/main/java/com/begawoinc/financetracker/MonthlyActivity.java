package com.begawoinc.financetracker;

public class MonthlyActivity implements Comparable<MonthlyActivity> {

    private int activityID;
    private String activity;
    private double amount;
    private String name;
    private String date;

    public int getActivityID() {
        return activityID;
    }

    public void setActivityID(int activityID) {
        this.activityID = activityID;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public int compareTo(MonthlyActivity o) {
        if (getDate() == null || o.getDate() == null)
            return 0;
        else return getDate().compareTo(o.getDate());
    }

}
