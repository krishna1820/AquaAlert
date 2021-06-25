package com.example.aquaalert.Fragments;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aquaalert.Registration.Login;
import com.example.aquaalert.R;
import com.example.aquaalert.TimePickerFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;


public class SettingsFragment extends Fragment {


    private FirebaseAuth mAuth;
    private static final int LOCATION = 1;
    TextView textView;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings,
                container, false);


        textView = view.findViewById(R.id.time);
        mAuth = FirebaseAuth.getInstance();

        String UID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(UID);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int hour = snapshot.child("Reminder_Interval_hour").getValue(Integer.class);
                    int min = snapshot.child("Reminder_Interval_minutes").getValue(Integer.class);
                    textView.setText(hour + ":" + min + "m");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        //WIFI
        Button connect = (Button) view.findViewById(R.id.wifi);
        connect.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                wifi();
            }
        });

        //RESET PASSWORD
        Button password = view.findViewById(R.id.reset);
        password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                String email = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();
                alertDialogBuilder.setTitle("Reset Password?")
                        .setIcon(R.drawable.ic_drop);
                alertDialogBuilder.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                assert email != null;
                                mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getActivity(), "Link for Password Reset sent to your Email", Toast.LENGTH_LONG).show();
                                        } else {
                                            Toast.makeText(getActivity(), Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                            }
                        });

                alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

        //LOGOUT
        Button logout = (Button) view.findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogWifi = new AlertDialog.Builder(getActivity());
                alertDialogWifi.setTitle("Logout")
                        .setMessage("Are You Sure?")
                        .setIcon(R.drawable.ic_sad_drop);

                alertDialogWifi.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(getActivity(), Login.class));
                        getActivity().finish();
                    }
                });
                alertDialogWifi.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                AlertDialog alertDialogLogout = alertDialogWifi.create();
                alertDialogLogout.show();
            }
        });

        //REMINDER
        Button reminder = view.findViewById(R.id.reminder);
        reminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerFragment timeFragment = new TimePickerFragment();
                timeFragment.show(getChildFragmentManager(), "TimePicker");
            }
        });

        return view;
    }

    public void wifi() {
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //Request permission from user
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION);
        } else {
            final String TAG = "SSID";
            WifiManager wifi = (WifiManager) requireActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            final Handler handler = new Handler(Looper.getMainLooper());

            if (!wifi.isWifiEnabled())
                wifi.setWifiEnabled(true);

            WifiConfiguration wifiConfig = new WifiConfiguration();
            wifiConfig.SSID = String.format("\"%s\"", "Aqua");
            wifiConfig.preSharedKey = String.format("\"%s\"", "123456789");
            int netId = wifi.addNetwork(wifiConfig);
            wifi.disconnect();
            wifi.enableNetwork(netId, true);
            wifi.reconnect();


            AlertDialog.Builder alertDialogWifi = new AlertDialog.Builder(getActivity());
            alertDialogWifi.setTitle("Cannot Reach the Bottle")
                    .setIcon(R.drawable.ic_sad_drop);

            alertDialogWifi.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            AlertDialog alertDialog = alertDialogWifi.create();

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    WifiInfo info = wifi.getConnectionInfo();
                    String ss = info.getSSID();
                    //startActivity(new Intent(getActivity(), Web.class));
                    if (!ss.equals("\"Aqua\""))
                        alertDialog.show();
                    else
                        startActivity(new Intent(getActivity(), Web.class));
                }
            }, 2500);
        }
    }
}