package com.example.aquaalert;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MyService extends Service {


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotificationChannel();


        Intent intent1 = new Intent(this, SplashScreen.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification disconnected = new NotificationCompat.Builder(this, "ChannelId1")
                .setContentTitle("AquaAlert")
                .setContentText("Bottle is Disconnected")
                .setSmallIcon(R.drawable.ic_drop)
                .setColor(ContextCompat.getColor(this, R.color.holo_blue_light))
                .setContentIntent(pendingIntent)
                .setNotificationSilent()
                .build();

        Notification connected = new NotificationCompat.Builder(this, "ChannelId1")
                .setContentTitle("AquaAlert")
                .setContentText("Stay Hydrated!")
                .setSmallIcon(R.drawable.ic_drop)
                .setColor(ContextCompat.getColor(this, R.color.holo_blue_light))
                .setContentIntent(pendingIntent)
                .setNotificationSilent()
                .build();


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    //Bottle Connectivity
                    int connection = snapshot.child("Connectivity").getValue(Integer.class);
                    int trigger = snapshot.child("Trigger").getValue(Integer.class);
                    if (connection == 1) {
                        reference.child("Trigger").setValue(0);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                reference.child("Connectivity").setValue(0);
                            }
                        }, 3000);
                    } else {
                        if (trigger <= 5) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    reference.child("Trigger").setValue(trigger + 1);
                                }
                            }, 1000);
                        }
                    }

                    if (trigger >= 5) {
                        startForeground(1, disconnected);
                    } else
                        startForeground(1, connected);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        return START_STICKY;
    }

    private void createNotificationChannel() {
        //if above oreo
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    "ChannelId1", "Foreground Notification", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationChannel notificationChannel2 = new NotificationChannel(
                    "ChannelId2", "Reminder Notification", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(notificationChannel2);
            manager.createNotificationChannel(notificationChannel);

        }

    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        stopForeground(false);
        super.onDestroy();
    }

    @Override
    public void onCreate() {
        Intent intent1 = new Intent(this, SplashScreen.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification connected = new NotificationCompat.Builder(this, "ChannelId1")
                .setContentTitle("AquaAlert")
                .setContentTitle("Stay Hydrated!")
                .setSmallIcon(R.drawable.ic_drop)
                .setColor(ContextCompat.getColor(this, R.color.holo_blue_light))
                .setContentIntent(pendingIntent)
                .setNotificationSilent()
                .build();

        startForeground(1, connected);

        super.onCreate();

    }
}
