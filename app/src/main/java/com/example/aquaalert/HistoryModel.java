package com.example.aquaalert;

public class HistoryModel {
    String date, goal;
    int waterDrank;

    HistoryModel() {

    }

    public HistoryModel(String date, int waterDrank, String goal) {
        this.date = date;
        this.waterDrank = waterDrank;
        this.goal = goal;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getWaterDrank() {
        return waterDrank;
    }

    public void setWaterDrank(int waterDrank) {
        this.waterDrank = waterDrank;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }
}
