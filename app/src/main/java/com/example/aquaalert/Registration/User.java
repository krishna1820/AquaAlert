package com.example.aquaalert.Registration;

public class User {
    public String Name, Email, Password;
    public int Total, Reminder_Interval_hour, Reminder_Interval_minutes, ReminderSet, OldTotal;
    public String Goal;

    public User() { }

    public User(String name, String email, String password, int Total, int Reminder_Interval_hour, int Reminder_Interval_minutes, int ReminderSet, int OldTotal, String Goal) {
        this.Name = name;
        this.Email = email;
        this.Password = password;
        this.Total = Total;
        this.Reminder_Interval_hour = Reminder_Interval_hour;
        this.Reminder_Interval_minutes = Reminder_Interval_minutes;
        this.ReminderSet = ReminderSet;
        this.OldTotal = OldTotal;
        this.Goal = Goal;
    }
}