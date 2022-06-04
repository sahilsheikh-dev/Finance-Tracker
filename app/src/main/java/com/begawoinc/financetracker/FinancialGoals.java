package com.begawoinc.financetracker;

public class FinancialGoals {

    private String goal;
    private String goalDate;
    private double amtHaving;
    private double amtNeed;

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public String getGoalDate() {
        return goalDate;
    }

    public void setGoalDate(String goalDate) {
        this.goalDate = goalDate;
    }

    public double getAmtHaving() {
        return amtHaving;
    }

    public void setAmtHaving(double amtHaving) {
        this.amtHaving = amtHaving;
    }

    public double getAmtNeed() {
        return amtNeed;
    }

    public void setAmtNeed(double amtNeed) {
        this.amtNeed = amtNeed;
    }
}
