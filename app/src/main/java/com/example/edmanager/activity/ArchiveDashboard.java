package com.example.edmanager.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionManager;

import com.example.edmanager.R;
import com.example.edmanager.adapter.DashboardAdapter;
import com.example.edmanager.helper.DropboxHelper;
import com.example.edmanager.helper.FileHelper;
import com.example.edmanager.helper.JsonHelper;
import com.example.edmanager.utility.Constant;
import com.example.edmanager.utility.ToastLogger;
import com.google.android.gms.maps.model.Dash;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.ArrayList;

public class ArchiveDashboard extends AppCompatActivity {

    ConstraintLayout buttonDeleteTrip, constraintLayout;
    DashboardAdapter adapter;
    RecyclerView recyclerView;
    File[] localTrips;
    ArrayList<String> listTrips;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archive_dashboard);

        recyclerView = findViewById(R.id.recyclerViewDashboardMain);
        constraintLayout = findViewById(R.id.dashboardLayout);

        listTrips = new ArrayList<>();

        adapter = new DashboardAdapter(listTrips, getApplicationContext(), new DashboardAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String tripNumber) {

                Constant.tripStaging = false;
                Constant.deliveryCompleted = true;
                Constant.trip = tripNumber;

                startActivity(new Intent(ArchiveDashboard.this, DetailDashboardCompleted.class));

                finish();
            }

            @Override
            public void onItemLongClick(String tripNumber) {

            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        updateFiles();
    }


    public void updateFiles() {
        try {

            localTrips = FileHelper.returnTripArchiveFiles(getApplicationContext());

            if (localTrips == null || localTrips.length == 0) {

                ToastLogger.message(getApplicationContext(), "No archived trips found. Returning to main dashboard.");

                Constant.archive = false;

                startActivity(new Intent(ArchiveDashboard.this, Dashboard.class));

                finish();

            } else {

                for (File tripFile : localTrips) {

                    String trip = tripFile.getName().substring(0, tripFile.getName().length() - 5);

                    if (!listTrips.contains(trip)) {

                        listTrips.add(trip);
                    }
                }

                adapter.notifyDataSetChanged();
            }

        } catch (Exception e) {

            e.printStackTrace();
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Constant.archive = false;

        startActivity(new Intent(ArchiveDashboard.this, Dashboard.class));
        finish();
    }

}