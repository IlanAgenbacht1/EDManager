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


public class NewDeliveryHeader extends AppCompatActivity {

    TextView textViewTrip, textViewAddress;
    EditText editTextDocument, editTextCompanyName, editTextContactName, editTextContactNumber;
    Button buttonAddDelivery, buttonFindAddress, buttonNext;
    ConstraintLayout layoutMain, layoutAddress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_delivery);

        textViewTrip = findViewById(R.id.NewDelivery_textViewTrip);
        buttonAddDelivery = findViewById(R.id.NewDelivery_buttonNext);
        textViewAddress = findViewById(R.id.NewDelivery_textVIewAddress);
        buttonFindAddress = findViewById(R.id.NewDelivery_buttonFindAddress);
        buttonNext = findViewById(R.id.NewDelivery_buttonNext);
        layoutMain = findViewById(R.id.layoutNewDelivery);
        layoutAddress = findViewById(R.id.layoutAddress);
        editTextCompanyName = findViewById(R.id.NewDelivery_editTextCompanyName);
        editTextContactName = findViewById(R.id.NewDelivery_editTextContactName);
        editTextContactNumber = findViewById(R.id.NewDelivery_editTextContactNumber);
        editTextDocument = findViewById(R.id.NewDelivery_editTextDocument);

        Constant.address = null;

        if (!Constant.deliveryEdit) {

            Constant.deliveryData = new DeliveryModel();

        } else {

            editTextCompanyName.setText(Constant.deliveryData.getCustomerName());
            editTextContactName.setText(Constant.deliveryData.getContactName());
            editTextContactNumber.setText(Constant.deliveryData.getContactNumber());
            editTextDocument.setText(Constant.deliveryData.getDocument());
            editTextDocument.setFocusable(false);
            editTextDocument.setClickable(false);
            textViewAddress.setText("Address saved");
            buttonFindAddress.setText("Change Address");

            for (String parcel : Constant.deliveryData.getParcelNumbers()) {

                Constant.listParcels.add(parcel);
            }
        }

        textViewTrip.setText(Constant.trip);

        buttonFindAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (buttonFindAddress.getText().toString().equals("Next")) {


                } else {

                    startActivity(new Intent(NewDeliveryHeader.this, GoogleLocation.class));
                }
            }
        });

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Constant.address != null) {

                    Location location = new Location("");

                    location.setLatitude(Constant.address.getLatitude());
                    location.setLongitude(Constant.address.getLongitude());

                    Constant.deliveryData.setLocation(location);
                    Constant.deliveryData.setAddress(Constant.address.getAddressLine(0));
                }


                Constant.deliveryData.setCustomerName(editTextCompanyName.getText().toString());
                Constant.deliveryData.setContactName(editTextContactName.getText().toString());
                Constant.deliveryData.setContactNumber(editTextContactNumber.getText().toString());
                Constant.deliveryData.setDocument(editTextDocument.getText().toString().toUpperCase());
                Constant.deliveryData.setTripId(textViewTrip.getText().toString());

                startActivity(new Intent(NewDeliveryHeader.this, NewDeliveryDetail.class));
            }
        });
    }


    private void setLocation() {

        if (Constant.address != null) {

            textViewAddress.setTextColor(getResources().getColor(R.color.white, null));
            textViewAddress.setText(Constant.address.getAddressLine(0));
            buttonFindAddress.setText("Change Address");
            buttonFindAddress.setTextColor(getResources().getColor(R.color.gold, null));
            buttonFindAddress.setBackgroundColor(getResources().getColor(R.color.black, null));
        }
    }


    @Override
    public void onResume() {
        super.onResume();

        setLocation();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        startActivity(new Intent(NewDeliveryHeader.this, DetailDashboard.class));

        finish();
    }

}