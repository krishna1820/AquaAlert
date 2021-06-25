package com.example.aquaalert;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;


public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        int hour = 0, min = 0;
        return new TimePickerDialog(getActivity(), this, hour, min, true);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getActivity(), AlarmReminderReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(getActivity(), 2, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        if (hourOfDay == 0) {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() + minute * 60 * 1000, minute * 60 * 1000, alarmIntent);
            if (minute == 1)
                Toast.makeText(getActivity(), "You will be Notified every " + minute + " Minute", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getActivity(), "You will be Notified every " + minute + " Minutes", Toast.LENGTH_SHORT).show();
        } else {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() + ((hourOfDay * 60 * 60 * 1000) + (minute * 60 * 1000)), (hourOfDay * 60 * 60 * 1000) + (minute * 60 * 1000), alarmIntent);
            if (hourOfDay == 1)
                Toast.makeText(getActivity(), "You will be Notified every " + hourOfDay + " Hour And " + minute + " Minutes", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getActivity(), "You will be Notified every " + hourOfDay + " Hours And " + minute + " Minutes", Toast.LENGTH_SHORT).show();
        }

        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(UID);
        reference.child("Reminder_Interval_hour").setValue(hourOfDay);
        reference.child("Reminder_Interval_minutes").setValue(minute);
        reference.child("ReminderSet").setValue(1);
    }
}
