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
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


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

        adapter = new ReturnsAdapter(listReturns, ReturnsDashboard.this, new ReturnsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String itemNumber) {

                String date = "";
                String time = "";
                String comment = "";
                String reference = "";

                for (DeliveryModel returnData : listReturns) {

                    if (returnData.getItemCode().equals(itemNumber)) {

                        SimpleDateFormat inputFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.US);
                        Date d = null;
                        try {
                            d = inputFormat.parse(returnData.getTime());
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }

                        SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
                        date = outputFormat.format(d);

                        SimpleDateFormat outputFormat2 = new SimpleDateFormat("HH:mm:ss", Locale.US);
                        time = outputFormat2.format(d);

                        comment = returnData.getComment();
                        reference = returnData.getReference();
                    }
                }

                LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
                View dialogView = inflater.inflate(R.layout.returns_dialog_layout, null);

                // Initialize fields in the dialog
                TextView dateField = dialogView.findViewById(R.id.dateField);
                TextView timeField = dialogView.findViewById(R.id.timeField);
                TextView commentField = dialogView.findViewById(R.id.commentField);
                TextView referenceField = dialogView.findViewById(R.id.referenceField);

                dateField.setText(date);
                timeField.setText(time);
                commentField.setText("Comment: " + comment);
                referenceField.setText("Reference: " + reference);

                // Build the AlertDialog
                AlertDialog.Builder builder = new AlertDialog.Builder(ReturnsDashboard.this, R.style.AlertDialogStyle);
                builder.setView(dialogView)
                        .setTitle("Return Details")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.cancel();
                            }
                        })
                        .create()
                        .show();
            }

            @Override
            public void onItemLongClick(String itemNumber) {

            }
        });

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