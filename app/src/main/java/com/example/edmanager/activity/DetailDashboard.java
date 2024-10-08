package com.example.edmanager.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionManager;

import com.example.edmanager.R;
import com.example.edmanager.adapter.DetailAdapter;
import com.example.edmanager.database.Database;
import com.example.edmanager.database.DeliveryModel;
import com.example.edmanager.helper.JsonHelper;
import com.example.edmanager.utility.Constant;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class DetailDashboard extends AppCompatActivity {

    private ArrayList<DeliveryModel> listDeliveries;
    private RecyclerView recyclerView;
    private DetailAdapter detailAdapter;
    private TextView textViewTrip;
    private FloatingActionButton fabDelete;

    private Database database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_dashboard);

        recyclerView = findViewById(R.id.dashboardDetailRecycler);
        textViewTrip = findViewById(R.id.textViewDetailTripLabel);
        fabDelete = findViewById(R.id.fabDelete);


        listDeliveries = JsonHelper.returnListDeliveryData(getApplicationContext(), Constant.trip);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        detailAdapter = new DetailAdapter(listDeliveries, new DetailAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String item) {

                if (item.equals("ADD_DELIVERY")) {

                    Constant.listParcels = new ArrayList<>();

                    startActivity(new Intent(DetailDashboard.this, NewDeliveryHeader.class));

                    finish();
                }
            }

            @Override
            public void onItemLongClick(String deliveryNumber) {

                fabDelete.setVisibility(View.VISIBLE);
                ObjectAnimator scaleX = ObjectAnimator.ofFloat(fabDelete, "scaleX", 1f);
                ObjectAnimator scaleY = ObjectAnimator.ofFloat(fabDelete, "scaleY", 1f);
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(scaleX, scaleY);
                animatorSet.setDuration(200); // animation duration in milliseconds
                animatorSet.start();
            }

        });

        recyclerView.setAdapter(detailAdapter);

        textViewTrip.setText("SHOWING DELIVERIES FOR " + Constant.trip);
    }


    /*public void runDeleteMode(String tripNumber) {

        Constant.tripHighlightedForDelete = true;

        if (Constant.listDeleteTrips.contains(tripNumber)) {

            Constant.listDeleteTrips.remove(tripNumber);

            Constant.tripDeleteMode = true;

            adapter.notifyItemChanged(Constant.listTrips.indexOf(tripNumber));

            if (Constant.listDeleteTrips.size() < 1) {

                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(constraintLayout);

                // Apply new end margin
                constraintSet.setMargin(R.id.buttonCreateTrip, ConstraintSet.END, originalEndMargin);

                TransitionManager.beginDelayedTransition(constraintLayout);
                constraintSet.applyTo(constraintLayout);

                buttonDeleteTrip.setVisibility(View.INVISIBLE);

                deleteModeAnimated = false;
                Constant.tripDeleteMode = false;
            }

        } else {

            Constant.listDeleteTrips.add(tripNumber);

            Constant.tripDeleteMode = true;

            adapter.notifyItemChanged(Constant.listTrips.indexOf(tripNumber));

            if (!deleteModeAnimated) {

                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(constraintLayout);

                // Apply new end margin
                constraintSet.setMargin(R.id.buttonCreateTrip, ConstraintSet.END, newEndMargin);

                TransitionManager.beginDelayedTransition(constraintLayout);
                constraintSet.applyTo(constraintLayout);

                buttonDeleteTrip.setVisibility(View.VISIBLE);

                deleteModeAnimated = true;
            }
        }
    }*/


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        startActivity(new Intent(DetailDashboard.this, Dashboard.class));

        finish();
    }
}