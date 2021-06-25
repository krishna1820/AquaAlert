package com.example.aquaalert;


import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class AlarmReminderReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intent1 = new Intent(context, SplashScreen.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 3, intent1, 0);
        String UID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int total = dataSnapshot.child("Users").child(UID).child("Total").getValue(Integer.class);
                int old_total = dataSnapshot.child("Users").child(UID).child("OldTotal").getValue(Integer.class);
                if (old_total != total) {
                    ref.child("Users").child(UID).child("OldTotal").setValue(total);
                } else {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if(user.isEmailVerified()){
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "ChannelId2")
                                .setContentTitle("AquaAlert")
                                .setContentText("Hey " + dataSnapshot.child("Users").child(UID).child("Name").getValue(String.class) + ", it's time to drink water!")
                                .setSmallIcon(R.drawable.ic_drop)
                                .setColor(ContextCompat.getColor(context, R.color.holo_blue_light))
                                .setContentIntent(pendingIntent);
                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                        notificationManager.notify(2, builder.build());
                        ref.child("LED").setValue(1);
                    }
                }
            }
            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {
                // ...
            }
        });
    }
}
