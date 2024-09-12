package com.example.edmanager.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.edmanager.R;
import com.example.edmanager.adapter.DashboardAdapter;
import com.example.edmanager.database.Database;
import com.example.edmanager.database.DeliveryModel;
import com.example.edmanager.helper.DropboxHelper;
import com.example.edmanager.helper.FileHelper;
import com.example.edmanager.helper.JsonHelper;
import com.example.edmanager.utility.Constant;
import com.example.edmanager.utility.ToastLogger;
import com.google.android.gms.maps.model.Dash;

import java.io.File;

public class Dashboard extends AppCompatActivity {

    Button buttonCreateTrip;
    EditText tripEditText;

    DashboardAdapter adapter;
    RecyclerView recyclerView;

    DeliveryModel tripData;

    Database database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        buttonCreateTrip = findViewById(R.id.buttonCreateTrip);
        recyclerView = findViewById(R.id.recyclerViewDashboardMain);


        adapter = new DashboardAdapter(Constant.listTrips, getApplicationContext(), new DashboardAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String tripNumber) {

                if (Constant.listConfiguredTrips.contains(tripNumber)) {

                    Constant.trip = tripNumber;
                    Constant.deliveryCount = JsonHelper.returnDeliveryCount(getApplicationContext(), tripNumber);

                    startActivity(new Intent(Dashboard.this, DetailDashboard.class));

                    finish();

                } else {

                    AlertDialog dialog = new AlertDialog.Builder(Dashboard.this, R.style.AlertDialogStyle).create();

                    dialog.setTitle("Delete Empty Trip/Add Delivery");

                    dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Add Delivery", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            Constant.trip = tripNumber;

                            startActivity(new Intent(Dashboard.this, NewDelivery.class));

                            finish();
                        }
                    });

                    dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Delete Trip", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            //database.deleteTrip(tripNumber);

                            //listTrips.remove(tripNumber);

                            adapter.notifyDataSetChanged();

                            dialog.cancel();
                        }
                    });

                    dialog.show();
                }
            }
        });


        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        buttonCreateTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                tripEditText = new EditText(Dashboard.this);
                tripEditText.setTextColor(getResources().getColor(R.color.white, null));
                tripEditText.setHintTextColor(getResources().getColor(R.color.gold, null));

                AlertDialog dialog = new AlertDialog.Builder(Dashboard.this, R.style.AlertDialogStyle).create();

                dialog.setTitle("Enter Trip Name");
                dialog.setView(tripEditText);

                tripEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        tripEditText.post(new Runnable() {
                            @Override
                            public void run() {
                                InputMethodManager inputMethodManager= (InputMethodManager) Dashboard.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                                inputMethodManager.showSoftInput(tripEditText, InputMethodManager.SHOW_IMPLICIT);
                            }
                        });
                    }
                });

                tripEditText.requestFocus();

                dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        try {

                            Constant.listTrips.add(tripEditText.getText().toString());

                            adapter.notifyDataSetChanged();

                            dialog.cancel();

                        } catch (Exception e) {

                            e.printStackTrace();

                            ToastLogger.exception(getApplicationContext(), e);
                        }
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

        updateFiles();
    }


    public void updateFiles() {
        try {

            if (Constant.refreshDashboard) {

                File[] trips = FileHelper.returnTripFiles(getApplicationContext());

                if (trips != null) {

                    for (File tripFile : trips) {

                        String trip = tripFile.getName().substring(0, tripFile.getName().length() - 5);

                        if (!Constant.listTrips.contains(trip)) {

                            Constant.listTrips.add(trip);
                        }

                        if (JsonHelper.returnDeliveryCount(getApplicationContext(), trip) > 0 && !Constant.listConfiguredTrips.contains(trip)) {

                            Constant.listConfiguredTrips.add(trip);
                        }
                    }

                    adapter.notifyDataSetChanged();
                }

                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {

                        while (true) {

                            for (String trip : Constant.listTrips) {

                                int status = DropboxHelper.returnTripStatus(trip);

                                if (status == 1) {

                                    if (!Constant.listCompletedTrips.contains(trip)) {

                                        Constant.listCompletedTrips.add(trip);

                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {

                                                adapter.notifyItemChanged(Constant.listTrips.indexOf(trip));
                                            }
                                        });
                                    }

                                } else if (status == 0) {

                                    if (!Constant.listInProgressTrips.contains(trip)) {

                                        Constant.listInProgressTrips.add(trip);

                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {

                                                adapter.notifyItemChanged(Constant.listTrips.indexOf(trip));
                                            }
                                        });
                                    }
                                }
                            }
                        }
                    }
                });

                thread.start();

                Constant.refreshDashboard = false;
            }

        } catch (Exception e) {

            e.printStackTrace();
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        startActivity(new Intent(Dashboard.this, Splash.class));
        finish();
    }

}