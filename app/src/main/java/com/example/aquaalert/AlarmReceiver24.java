package com.example.aquaalert;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AlarmReceiver24 extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("Total").setValue(0);
    }
}
