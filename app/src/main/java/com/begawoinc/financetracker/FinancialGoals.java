package com.begawoinc.financetracker;

public class FinancialGoals implements Comparable<FinancialGoals> {

    private int goalId;
    private String goal;
    private String goalDate;
    private double amtHaving;
    private double amtNeed;

    public int getGoalId() { return goalId; }

    public void setGoalId(int goalId) { this.goalId = goalId; }

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

    @Override
    public int compareTo(FinancialGoals o) {
        if (getGoalDate() == null || o.getGoalDate() == null)
            return 0;
        else return getGoalDate().compareTo(o.getGoalDate());
    }

}
