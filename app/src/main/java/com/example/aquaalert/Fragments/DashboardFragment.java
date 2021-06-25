package com.example.aquaalert.Fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aquaalert.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;


public class DashboardFragment extends Fragment {

    ImageView imageView, imageView2;
    TextView textView, progress, textView2;
    Button button;
    CardView cardView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);

        imageView = rootView.findViewById(R.id.waterlevel);
        imageView2 = rootView.findViewById(R.id.sad_drop);
        textView = rootView.findViewById(R.id.textView);
        button = rootView.findViewById(R.id.SetGoal);
        progress = rootView.findViewById(R.id.progress);
        textView2 = rootView.findViewById(R.id.textView3);
        cardView = rootView.findViewById(R.id.card);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    //Bottle Connectivity
                    int connection = snapshot.child("Connectivity").getValue(Integer.class);
                    int trigger = snapshot.child("Trigger").getValue(Integer.class);
                    int total = snapshot.child("Total").getValue(Integer.class);
                    reference.child("Users").child(UID).child("Total").setValue(total);

                    if (connection == 1) {
                        textView2.setVisibility(View.VISIBLE);
                        cardView.setVisibility(View.VISIBLE);
                        imageView.setVisibility(View.VISIBLE);
                        imageView2.setVisibility(View.INVISIBLE);
                        textView.setVisibility(View.INVISIBLE);
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
                        imageView.setVisibility(View.INVISIBLE);
                        textView2.setVisibility(View.INVISIBLE);
                        cardView.setVisibility(View.INVISIBLE);
                        imageView2.setVisibility(View.VISIBLE);
                        textView.setVisibility(View.VISIBLE);
                    }
                    Long water = (Long) snapshot.child("WaterLevel").getValue();
                    if (water == 0) {
                        imageView.setImageResource(R.drawable.ic_water_level_0);
                    } else if (water == 100) {
                        imageView.setImageResource(R.drawable.ic_water_level_100);
                    } else if (water == 200) {
                        imageView.setImageResource(R.drawable.ic_water_level_200);
                    } else if (water == 300) {
                        imageView.setImageResource(R.drawable.ic_water_level_300);
                    } else if (water == 400) {
                        imageView.setImageResource(R.drawable.ic_water_level_400);
                    } else if (water == 500) {
                        imageView.setImageResource(R.drawable.ic_water_level_500);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertSetGoal = new AlertDialog.Builder(getActivity());
                View mView = getLayoutInflater().inflate(R.layout.dialog_spinner, null);
                alertSetGoal.setTitle("Set your Daily Goal!")
                        .setIcon(R.drawable.ic_drop);
                Spinner spinner = mView.findViewById(R.id.spinner);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.Litres));
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
                alertSetGoal.setPositiveButton("Set", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!spinner.getSelectedItem().toString().equalsIgnoreCase("Litres…")) {
                            String Goal = spinner.getSelectedItem().toString();
                            reference.child("Users").child(UID).child("Goal").setValue(Goal);

                            Toast.makeText(getActivity(), "Daily Goal Set to " + spinner.getSelectedItem().toString() + "L", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                alertSetGoal.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                alertSetGoal.setView(mView);
                AlertDialog alertDialog = alertSetGoal.create();
                alertDialog.show();
            }
        });

        // Daily Water Goal Calculation
        ProgressBar progressBar = rootView.findViewById(R.id.dailyProgress);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int goalSet;
                String goal = snapshot.child("Users").child(UID).child("Goal").getValue(String.class);
                int totalWaterDrank = snapshot.child("Users").child(UID).child("Total").getValue(Integer.class);
                switch (Objects.requireNonNull(goal)) {
                    case "1":
                        goalSet = 1000;
                        break;
                    case "1.5":
                        goalSet = 1500;
                        break;
                    case "2":
                        goalSet = 2000;
                        break;
                    case "2.5":
                        goalSet = 2500;
                        break;
                    case "3":
                        goalSet = 3000;
                        break;
                    case "3.5":
                        goalSet = 3500;
                        break;
                    case "4":
                        goalSet = 4000;
                        break;
                    case "4.5":
                        goalSet = 4500;
                        break;
                    case "5":
                        goalSet = 5000;
                        break;
                    default:
                        goalSet = 2000;
                        break;
                }
                progressBar.setProgress((Integer) (totalWaterDrank * 100) / goalSet);
                double waterDrank = totalWaterDrank / (double) 1000;
                progress.setText(String.format("%s/%sL", waterDrank, goal));
                Log.i("totalWaterDrank", String.valueOf(waterDrank));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        return rootView;
    }
}