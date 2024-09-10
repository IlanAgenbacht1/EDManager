package com.example.edmanager.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.edmanager.R;
import com.example.edmanager.adapter.ParcelAdapter;
import com.example.edmanager.database.Database;
import com.example.edmanager.database.DeliveryModel;
import com.example.edmanager.helper.DropboxHelper;
import com.example.edmanager.helper.JsonHelper;
import com.example.edmanager.utility.Constant;
import com.example.edmanager.utility.ToastLogger;

import java.io.File;
import java.util.ArrayList;


public class NewDelivery extends AppCompatActivity {

    TextView textViewTrip, textViewAddress;
    EditText editTextDocument, editTextItem, editTextCompanyName, editTextContactName, editTextContactNumber;
    Button buttonNext, buttonFindAddress, buttonAdd, buttonAddressBack;
    RecyclerView recyclerView;
    ParcelAdapter adapter;
    ArrayList<String> listItems;
    ConstraintLayout layoutMain, layoutAddress;

    DeliveryModel deliveryData;
    Database database;

    int fillCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_delivery);

        textViewTrip = findViewById(R.id.NewDelivery_textViewTrip);
        editTextDocument = findViewById(R.id.NewDelivery_editTextDocument);
        editTextItem = findViewById(R.id.NewDelivery_editTextItem);
        buttonNext = findViewById(R.id.NewDelivery_buttonNext);
        buttonAdd = findViewById(R.id.buttonAddParcel);
        recyclerView = findViewById(R.id.recyclerViewItems);
        textViewAddress = findViewById(R.id.NewDelivery_textVIewAddress);
        buttonFindAddress = findViewById(R.id.NewDelivery_buttonFindAddress);
        layoutMain = findViewById(R.id.layoutNewDelivery);
        layoutAddress = findViewById(R.id.layoutAddress);
        buttonAddressBack = findViewById(R.id.NewDelivery_buttonAddressBack);
        editTextCompanyName = findViewById(R.id.NewDelivery_editTextCompanyName);
        editTextContactName = findViewById(R.id.NewDelivery_editTextContactName);
        editTextContactNumber = findViewById(R.id.NewDelivery_editTextContactNumber);

        Constant.address = null;

        deliveryData = new DeliveryModel();
        database = new Database(this);
        database.open();

        textViewTrip.setText(Constant.trip);

        listItems = new ArrayList<>();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ParcelAdapter(listItems, new ParcelAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String item) {

                AlertDialog dialog = new AlertDialog.Builder(NewDelivery.this, R.style.AlertDialogStyle).create();

                dialog.setTitle("Remove Item " + item + "?");

                dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        listItems.remove(item);

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
        });

        recyclerView.setAdapter(adapter);

        editTextCompanyName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (editTextCompanyName.getText().length() > 0 && editTextContactName.getText().length() > 0 && editTextContactNumber.getText().length() > 0) {

                    buttonFindAddress.setText("Add Delivery");

                } else if (!buttonFindAddress.getText().equals("Change Address")) {

                    buttonFindAddress.setText("Change Address");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        editTextContactName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (editTextCompanyName.getText().length() > 0 && editTextContactName.getText().length() > 0 && editTextContactNumber.getText().length() > 0) {

                    buttonFindAddress.setText("Add Delivery");

                } else if (!buttonFindAddress.getText().equals("Change Address")) {

                    buttonFindAddress.setText("Change Address");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        editTextContactNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (editTextCompanyName.getText().length() > 0 && editTextContactName.getText().length() > 0 && editTextContactNumber.getText().length() > 0) {

                    buttonFindAddress.setText("Add Delivery");

                } else if (!buttonFindAddress.getText().equals("Change Address")) {

                    buttonFindAddress.setText("Change Address");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String item = editTextItem.getText().toString();

                if (itemValidation(item)) {

                    listItems.add(item);

                    adapter.notifyDataSetChanged();

                    editTextItem.setText("");
                }
            }
        });

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                deliveryData.setTripId(textViewTrip.getText().toString());
                deliveryData.setDocument(editTextDocument.getText().toString());
                deliveryData.setNumberOfParcels(listItems.size());
                deliveryData.setParcelNumbers(listItems);

                layoutMain.setVisibility(View.GONE);
                layoutAddress.setVisibility(View.VISIBLE);
            }
        });

        buttonFindAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (buttonFindAddress.getText().toString().equals("Add Delivery")) {

                    Location location = new Location("");
                    location.setLatitude(Constant.address.getLatitude());
                    location.setLongitude(Constant.address.getLongitude());

                    deliveryData.setLocation(location);
                    deliveryData.setAddress(Constant.address.getAddressLine(0));
                    deliveryData.setCustomerName(editTextCompanyName.getText().toString());
                    deliveryData.setContactName(editTextContactName.getText().toString());
                    deliveryData.setContactNumber(editTextContactNumber.getText().toString());
                    deliveryData.setNumberOfParcels(listItems.size());
                    deliveryData.setDocument(editTextDocument.getText().toString());
                    deliveryData.setParcelNumbers(listItems);
                    deliveryData.setTripId(textViewTrip.getText().toString());

                    uploadDelivery();

                } else {

                    startActivity(new Intent(NewDelivery.this, GoogleLocation.class));
                }
            }
        });

        buttonAddressBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                layoutAddress.setVisibility(View.GONE);
                layoutMain.setVisibility(View.VISIBLE);

                deliveryData = new DeliveryModel();
            }
        });
    }


    private void uploadDelivery() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                if (!DropboxHelper.fileExists(getApplicationContext(), deliveryData.getTripId())) {

                    File file = JsonHelper.createOrAppendTrip(getApplicationContext(), deliveryData, Constant.LOCAL_TRIP_DIRECTORY);

                    DropboxHelper.uploadFile(getApplicationContext(), file);

                    Constant.refreshDashboard = true;

                } else {

                    DropboxHelper.downloadFile(getApplicationContext(), deliveryData.getTripId());

                    File file = JsonHelper.createOrAppendTrip(getApplicationContext(), deliveryData, Constant.LOCAL_EXISTING_TRIP_DIRECTORY);

                    DropboxHelper.uploadFile(getApplicationContext(), file);

                    Constant.refreshDashboard = true;
                }

                startActivity(new Intent(NewDelivery.this, DetailDashboard.class));

                finish();
            }
        });

        thread.start();
    }


    private boolean itemValidation(String item) {

        if (listItems.contains(item)) {

            ToastLogger.message(getApplicationContext(), "Duplicate item");

            editTextItem.setText("");

            return false;
        }

        return true;
    }


    private void setLocation() {

        if (Constant.address != null) {

            textViewAddress.setTextColor(getResources().getColor(R.color.white, null));
            textViewAddress.setText(Constant.address.getAddressLine(0));
            buttonFindAddress.setText("Change Address");
            buttonFindAddress.setTextColor(getResources().getColor(R.color.gold, null));
            buttonFindAddress.setBackgroundColor(getResources().getColor(R.color.black, null));

            editTextCompanyName.setVisibility(View.VISIBLE);
            editTextContactName.setVisibility(View.VISIBLE);
            editTextContactNumber.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onResume() {
        super.onResume();

        setLocation();
    }

}