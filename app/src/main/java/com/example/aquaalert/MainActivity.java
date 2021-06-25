package com.example.aquaalert;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.fragment.app.Fragment;


import android.annotation.SuppressLint;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import com.example.aquaalert.Fragments.DashboardFragment;
import com.example.aquaalert.Fragments.SettingsFragment;
import com.example.aquaalert.Fragments.HistoryFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class MainActivity <AppCompatActivity> extends androidx.appcompat.app.AppCompatActivity {
    ChipNavigationBar chipNavigationBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        chipNavigationBar = findViewById(R.id.bottomnav);
        chipNavigationBar.setItemSelected(R.id.home, true);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new DashboardFragment()).commit();
        bottomMenu();


        //24 hour Alarm
        Intent intent = new Intent(this, AlarmReceiver24.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 1, intent, 0);
        AlarmManager alarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        if (calendar.before(Calendar.getInstance()))
            calendar.add(Calendar.DATE, 1);
        //repeat alarm every 24hours
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarmIntent);

        //Reminder Notification
        String UID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(UID);
        Intent intent1 = new Intent(this, AlarmReminderReceiver.class);
        PendingIntent alarmIntent1 = PendingIntent.getBroadcast(this, 2, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int isReminderSet = snapshot.child("ReminderSet").getValue(Integer.class);

                if (isReminderSet != 1) {
                    alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() + 30 * 60 * 1000, 30 * 60 * 1000, alarmIntent1);
                    reference.child("Reminder_Interval_hour").setValue(0);
                    reference.child("Reminder_Interval_minutes").setValue(30);
                    Log.i("ReminderReset", String.valueOf(isReminderSet));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int TotalWaterDrank = snapshot.child("Total").getValue(Integer.class);
                String Goal = snapshot.child("Goal").getValue(String.class);
                HistoryModel historyModel = new HistoryModel(date, TotalWaterDrank, Goal);
                reference.child("History").child(date).setValue(historyModel);
                //Toast.makeText(MainActivity.this, date, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void bottomMenu() {
        chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public void onItemSelected(int i) {
                Fragment fragment = null;
                switch (i) {
                    case R.id.stats:
                        fragment = new HistoryFragment();
                        break;
                    case R.id.home:
                        fragment = new DashboardFragment();
                        break;
                    case R.id.profile:
                        fragment = new SettingsFragment();
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
            }
        });
    }
}