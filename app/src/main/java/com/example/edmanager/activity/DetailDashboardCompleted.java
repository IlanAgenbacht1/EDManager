package com.example.edmanager.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.edmanager.R;
import com.example.edmanager.adapter.DetailAdapter;
import com.example.edmanager.database.DeliveryModel;
import com.example.edmanager.helper.DropboxHelper;
import com.example.edmanager.helper.FileHelper;
import com.example.edmanager.helper.JsonHelper;
import com.example.edmanager.utility.Constant;
import com.example.edmanager.utility.ToastLogger;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

public class DetailDashboardCompleted extends AppCompatActivity {

    private ArrayList<DeliveryModel> listDeliveries;
    private RecyclerView recyclerView;
    private DetailAdapter detailAdapter;
    private TextView textViewTrip;
    private FloatingActionButton fabArchive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_dashboard_completed);

        recyclerView = findViewById(R.id.dashboardDetailRecycler);
        textViewTrip = findViewById(R.id.textViewDetailTripLabel);
        fabArchive = findViewById(R.id.fabDeploy);

        listDeliveries = JsonHelper.returnListDeliveryData(getApplicationContext(), Constant.trip);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        detailAdapter = new DetailAdapter(listDeliveries, new DetailAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String item) {

                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {

                        if (Constant.archive) {

                            String path = "/Customers/" + Constant.company + "/Completed/" + "Archive/" + Constant.trip + "/" + item + "/" + item + ".json";

                            DropboxHelper.returnCapturedData(item, path);

                        } else {

                            String path = "/Customers/" + Constant.company + "/Completed/" + Constant.trip + "/" + item + "/" + item + ".json";

                            DropboxHelper.returnCapturedData(item, path);
                        }

                        startActivity(new Intent(DetailDashboardCompleted.this, CapturedData.class));

                        finish();
                    }
                });

                thread.start();
            }

            @Override
            public void onItemLongClick(String deliveryNumber, int position) {


            }

        });

        fabArchive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {

                        DropboxHelper.archiveTrip(Constant.trip);

                        FileHelper.archiveTripFile(getApplicationContext(), Constant.trip);

                        Constant.listCompletedTrips.remove(Constant.trip);
                        Constant.listTrips.remove(Constant.trip);
                        Constant.listConfiguredTrips.remove(Constant.trip);

                        ToastLogger.message(getApplicationContext(), "Trip " + Constant.trip + " archived.");

                        Constant.archive = true;

                        startActivity(new Intent(DetailDashboardCompleted.this, ArchiveDashboard.class));
                    }
                });

                thread.start();
            }
        });

        recyclerView.setAdapter(detailAdapter);

        textViewTrip.setText("SHOWING DELIVERIES FOR " + Constant.trip);

        if (!Constant.archive) {

            fabArchive.setVisibility(View.VISIBLE);
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(fabArchive, "scaleX", 1f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(fabArchive, "scaleY", 1f);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(scaleX, scaleY);
            animatorSet.setDuration(200); // animation duration in milliseconds
            animatorSet.setStartDelay(1000);
            animatorSet.start();
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (Constant.archive) {

            startActivity(new Intent(DetailDashboardCompleted.this, ArchiveDashboard.class));

            finish();

        } else {

            startActivity(new Intent(DetailDashboardCompleted.this, Dashboard.class));

            finish();
        }
    }
}