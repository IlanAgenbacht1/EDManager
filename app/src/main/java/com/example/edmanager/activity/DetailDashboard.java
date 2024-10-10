package com.example.edmanager.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionManager;

import com.example.edmanager.R;
import com.example.edmanager.adapter.DetailAdapter;
import com.example.edmanager.database.Database;
import com.example.edmanager.database.DeliveryModel;
import com.example.edmanager.helper.DropboxHelper;
import com.example.edmanager.helper.JsonHelper;
import com.example.edmanager.utility.Constant;
import com.example.edmanager.utility.ToastLogger;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class DetailDashboard extends AppCompatActivity {

    private ArrayList<DeliveryModel> listDeliveries;
    private RecyclerView recyclerView;
    private DetailAdapter detailAdapter;
    private TextView textViewTrip;
    private FloatingActionButton fabDelete, fabDeploy;

    private Database database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_dashboard);

        recyclerView = findViewById(R.id.dashboardDetailRecycler);
        textViewTrip = findViewById(R.id.textViewDetailTripLabel);
        fabDelete = findViewById(R.id.fabDelete);
        fabDeploy = findViewById(R.id.fabDeploy);

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

        fabDeploy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog dialog = new AlertDialog.Builder(DetailDashboard.this, R.style.AlertDialogStyle).create();

                dialog.setTitle("Deploy Trip?");

                dialog.setMessage("Once deployed, no deliveries can be added or modified. Are you sure?");

                dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Deploy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        ToastLogger.message(getApplicationContext(), "Deploying trip...");

                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {

                                DropboxHelper.deployTrip(Constant.trip);

                                startActivity(new Intent(DetailDashboard.this, Dashboard.class));

                                finish();
                            }
                        });

                        thread.start();
                    }
                });

                dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        dialog.cancel();
                    }
                });

                dialog.show();
            }
        });

        recyclerView.setAdapter(detailAdapter);

        textViewTrip.setText("SHOWING DELIVERIES FOR " + Constant.trip);

        if (Constant.tripStaging) {

            fabDeploy.setVisibility(View.VISIBLE);
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(fabDeploy, "scaleX", 1f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(fabDeploy, "scaleY", 1f);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(scaleX, scaleY);
            animatorSet.setDuration(200); // animation duration in milliseconds
            animatorSet.setStartDelay(1000);
            animatorSet.start();
        }
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