package com.example.edmanager.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import com.example.edmanager.helper.FileHelper;
import com.example.edmanager.helper.JsonHelper;
import com.example.edmanager.utility.Constant;
import com.example.edmanager.utility.ToastLogger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Dashboard extends AppCompatActivity {

    Button buttonCreateTrip;
    EditText tripEditText;

    ArrayList<String> listTrips, listConfiguredTrips;
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

        listConfiguredTrips = new ArrayList<>();
        listTrips = new ArrayList<>();

        File[] trips = FileHelper.returnTripFiles(getApplicationContext());

        if (trips != null) {

            for (File tripFile : trips) {

                String trip = tripFile.getName().substring(0, tripFile.getName().length() - 5);

                listTrips.add(trip);

                if (JsonHelper.returnDeliveryCount(getApplicationContext(), trip) > 0) {

                    listConfiguredTrips.add(trip);
                }
            }
        }

        /*database = new Database(this);
        database.open();

        listConfiguredTrips = new ArrayList<>();
        listTrips = database.returnTrips();

        for (String trip : listTrips) {

            if (database.returnDeliveryCount(trip) > 0) {

                listConfiguredTrips.add(trip);
            }
        }*/

        adapter = new DashboardAdapter(listTrips, listConfiguredTrips, getApplicationContext(), new DashboardAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String tripNumber) {

                if (listConfiguredTrips.contains(tripNumber)) {

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

                            database.deleteTrip(tripNumber);

                            listTrips.remove(tripNumber);

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

                            if (database.insertTrip(tripEditText.getText().toString())) {

                                listTrips.add(tripEditText.getText().toString());

                                adapter.notifyDataSetChanged();
                            }

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

    }
}