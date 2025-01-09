package com.example.edmanager.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.edmanager.R;
import com.example.edmanager.adapter.DashboardAdapter;
import com.example.edmanager.adapter.ReturnsAdapter;
import com.example.edmanager.database.DeliveryModel;
import com.example.edmanager.helper.DropboxHelper;
import com.example.edmanager.helper.JsonHelper;
import com.example.edmanager.utility.Constant;

import java.util.ArrayList;
import java.util.List;


public class ReturnsDashboard extends AppCompatActivity {

    ConstraintLayout constraintLayout;
    ProgressBar progressBar;
    ReturnsAdapter adapter;
    RecyclerView recyclerView;
    ArrayList<DeliveryModel> listReturns;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_returns_dashboard);

        recyclerView = findViewById(R.id.recyclerViewDashboardReturns);
        constraintLayout = findViewById(R.id.dashboardLayout);
        progressBar = findViewById(R.id.returnsDashProgressBar);

        listReturns = new ArrayList<>();

        adapter = new ReturnsAdapter(listReturns, ReturnsDashboard.this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        updateReturns();
    }


    public void updateReturns() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                DropboxHelper.fetchReturnsFile(getApplicationContext());

                listReturns.addAll(JsonHelper.parseReturnsData(getApplicationContext()));

                Log.i("RETURNS", "" + listReturns.size());
            }
        });

        thread.start();

        Handler textHandler = new Handler();
        textHandler.post(new Runnable() {
            @Override
            public void run() {

                if (!listReturns.isEmpty()) {

                    progressBar.setVisibility(View.GONE);
                }

                adapter.notifyDataSetChanged();

                textHandler.postDelayed(this, 500);
            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        startActivity(new Intent(ReturnsDashboard.this, Splash.class));
        finish();
    }

}