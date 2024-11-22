package com.example.edmanager.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.edmanager.R;
import com.example.edmanager.adapter.ParcelAdapter;
import com.example.edmanager.helper.DropboxHelper;
import com.example.edmanager.helper.JsonHelper;
import com.example.edmanager.utility.Constant;
import com.example.edmanager.utility.ToastLogger;

import java.io.File;
import java.util.ArrayList;

public class NewDeliveryDetail extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ParcelAdapter adapter;
    private Button buttonAddParcel, buttonCreateDelivery;
    private EditText editTextParcel;
    private TextView textViewLabel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_delivery_detail);

        recyclerView = findViewById(R.id.recyclerViewItems);
        buttonAddParcel = findViewById(R.id.NewDeliveryDetail_buttonAddParcel);
        editTextParcel = findViewById(R.id.NewDeliveryDetail_editTextParcel);
        buttonCreateDelivery = findViewById(R.id.NewDeliveryDetail_createDelivery);
        textViewLabel = findViewById(R.id.NewDeliveryDetail_textViewParcels);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ParcelAdapter(Constant.listParcels, new ParcelAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String item) {

                if (!Constant.viewDelivery) {

                    AlertDialog dialog = new AlertDialog.Builder(NewDeliveryDetail.this, R.style.AlertDialogStyle).create();

                    dialog.setTitle("Remove Item " + item + "?");

                    dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            Constant.listParcels.remove(item);

                            adapter.notifyDataSetChanged();

                            dialog.cancel();
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
            }
        });

        buttonAddParcel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!Constant.listParcels.contains(editTextParcel.getText().toString())) {

                    Constant.listParcels.add(editTextParcel.getText().toString());

                    adapter.notifyItemInserted(Constant.listParcels.indexOf(editTextParcel.getText().toString()));

                    editTextParcel.setText("");

                } else {

                    ToastLogger.message(NewDeliveryDetail.this, "Item already added");
                }
            }
        });

        buttonCreateDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!Constant.viewDelivery) {

                    if (!Constant.listParcels.isEmpty()) {

                        Constant.deliveryData.setNumberOfParcels(Constant.listParcels.size());
                        Constant.deliveryData.setParcelNumbers(Constant.listParcels);

                        if (Constant.deliveryEdit) {

                            JsonHelper.removeDelivery(getApplicationContext(), Constant.trip, Constant.deliveryData);

                            uploadDelivery();

                        } else {

                            uploadDelivery();
                        }

                    } else {

                        ToastLogger.message(NewDeliveryDetail.this, "Parcels not specified");
                    }

                } else {

                    Constant.viewDelivery = false;

                    startActivity(new Intent(NewDeliveryDetail.this, DetailDashboard.class));

                    finish();
                }
            }
        });

        recyclerView.setAdapter(adapter);

        if (Constant.deliveryEdit) {

            buttonCreateDelivery.setText("Update Delivery");
        }

        if (Constant.viewDelivery) {

            editTextParcel.setVisibility(View.GONE);
            buttonAddParcel.setVisibility(View.GONE);
            textViewLabel.setText("Parcels:");
            buttonCreateDelivery.setText("CLOSE");
        }
    }


    private void uploadDelivery() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                if (!DropboxHelper.fileExists(getApplicationContext(), Constant.deliveryData.getTripId())) {

                    File file = JsonHelper.createOrAppendTrip(getApplicationContext(), Constant.deliveryData, Constant.LOCAL_EXISTING_TRIP_DIRECTORY + Constant.company + "/");

                    DropboxHelper.uploadFile(getApplicationContext(), file);

                    Constant.refreshDashboard = true;

                } else {

                    if (!Constant.deliveryEdit) {

                        DropboxHelper.downloadFile(getApplicationContext(), Constant.deliveryData.getTripId(), null);
                    }

                    File file = JsonHelper.createOrAppendTrip(getApplicationContext(), Constant.deliveryData, Constant.LOCAL_EXISTING_TRIP_DIRECTORY + Constant.company + "/");

                    DropboxHelper.uploadFile(getApplicationContext(), file);

                    Constant.refreshDashboard = true;
                }

                startActivity(new Intent(NewDeliveryDetail.this, DetailDashboard.class));

                finish();
            }
        });

        thread.start();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
    }


}