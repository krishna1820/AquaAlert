package com.example.aquaalert;


import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;


public class Adapter extends FirebaseRecyclerAdapter<HistoryModel, Adapter.viewholder> {

    public Adapter(@NonNull FirebaseRecyclerOptions<HistoryModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull viewholder holder, int position, @NonNull HistoryModel model) {
        holder.date.setText(model.getDate());
        holder.goal.setText(model.getGoal());
        int waterDrank = model.getWaterDrank();
        String goal = model.getGoal();
        int goalSet;

        // No. Of Glass
        int GOAL = 0;
        if (goal.equals("1"))
            GOAL = 1;
        if (goal.equals("1.5") || goal.equals("2"))
            GOAL = 2;
        if (goal.equals("2.5") || goal.equals("3"))
            GOAL = 3;
        if (goal.equals("3.5") || goal.equals("4"))
            GOAL = 4;
        if (goal.equals("4.5") || goal.equals("5"))
            GOAL = 5;

        if (GOAL >= 1)
            holder.water1.setVisibility(View.VISIBLE);
        if (GOAL >= 2)
            holder.water2.setVisibility(View.VISIBLE);
        if (GOAL >= 3)
            holder.water3.setVisibility(View.VISIBLE);
        if (GOAL >= 4)
            holder.water4.setVisibility(View.VISIBLE);
        if (GOAL >= 5)
            holder.water5.setVisibility(View.VISIBLE);

        // Glass Filled
        if (waterDrank >= 1000)
            holder.water1.setImageResource(R.drawable.ic_filled_water);
        if (waterDrank >= 2000)
            holder.water2.setImageResource(R.drawable.ic_filled_water);
        if (waterDrank >= 3000)
            holder.water3.setImageResource(R.drawable.ic_filled_water);
        if (waterDrank >= 4000)
            holder.water4.setImageResource(R.drawable.ic_filled_water);
        if (waterDrank >= 5000)
            holder.water5.setImageResource(R.drawable.ic_filled_water);

        switch (goal) {
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
        if (waterDrank >= goalSet) {
            holder.goal.setText("Goal Reached!");
            holder.cardView.setCardBackgroundColor(Color.parseColor("#8033b5e5"));
        } else {
            holder.logo.setImageResource(R.drawable.ic_sad_drop);
            holder.goal.setText("Goal Not Reached");
            holder.cardView.setCardBackgroundColor(Color.parseColor("#0D33b5e5"));
        }
        holder.waterDrank.setText(((float) model.getWaterDrank() / 1000) + "/" + model.getGoal() + " Litres");
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history, parent, false);
        return new viewholder(view);
    }

    static class viewholder extends RecyclerView.ViewHolder {
        ImageView logo, water1, water2, water3, water4, water5;
        TextView date, waterDrank, goal;
        CardView cardView;

        public viewholder(@NonNull View itemView) {
            super(itemView);

            logo = (ImageView) itemView.findViewById(R.id.logo);
            date = (TextView) itemView.findViewById(R.id.date);
            waterDrank = (TextView) itemView.findViewById(R.id.waterDrank);
            goal = (TextView) itemView.findViewById(R.id.goalReached);
            water1 = (ImageView) itemView.findViewById(R.id.water1);
            water2 = (ImageView) itemView.findViewById(R.id.water2);
            water3 = (ImageView) itemView.findViewById(R.id.water3);
            water4 = (ImageView) itemView.findViewById(R.id.water4);
            water5 = (ImageView) itemView.findViewById(R.id.water5);
            cardView = (CardView) itemView.findViewById(R.id.item);
        }
    }
}
