package com.example.edmanager.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.edmanager.R;
import com.example.edmanager.adapter.ParcelAdapter;
import com.example.edmanager.helper.DropboxHelper;
import com.example.edmanager.utility.Constant;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CapturedData extends AppCompatActivity {

    private TextView textViewCustomer, textViewDeliveryNumber, textViewDate, textViewTime, textViewLocation, textViewComment;
    private ImageView imageViewItem, imageViewSignature;
    private RecyclerView recyclerViewCapturedParcels;
    private ParcelAdapter adapter;
    private ArrayList<String> listParcels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_captured_data);

        textViewCustomer = findViewById(R.id.CapturedData_customerName);
        textViewDeliveryNumber = findViewById(R.id.CapturedData_documentNumber);
        imageViewItem = findViewById(R.id.CapturedData_imageViewItem);
        imageViewSignature = findViewById(R.id.CapturedData_imageViewSignature);
        textViewDate = findViewById(R.id.CapturedData_textViewDate);
        textViewTime = findViewById(R.id.CapturedData_textViewTime);
        textViewLocation = findViewById(R.id.CapturedData_textViewCoordinates);
        textViewComment = findViewById(R.id.CapturedData_textViewComment);
        recyclerViewCapturedParcels = findViewById(R.id.CapturedData_recyclerViewCapturedParcels);

        textViewCustomer.setText(Constant.deliveryData.getCustomerName());
        textViewDeliveryNumber.setText(Constant.deliveryData.getDocument());
        textViewLocation.setText(Constant.deliveryData.getLocation().getLatitude() + " " + Constant.deliveryData.getLocation().getLongitude());

        listParcels = new ArrayList<>();

        for (String item :
                Constant.deliveryData.getParcelNumbers()) {
            listParcels.add(item);
        }

        recyclerViewCapturedParcels.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ParcelAdapter(listParcels, new ParcelAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String item) {

            }
        });

        recyclerViewCapturedParcels.setAdapter(adapter);

        fetchAndDisplayData();
    }


    private void fetchAndDisplayData() {
        try {

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {

                    Bitmap itemBitMap = DropboxHelper.returnCapturedImage("/Customers/" + Constant.company + "/Completed/" + Constant.trip + "/" + Constant.deliveryData.getDocument() + "/" + Constant.deliveryData.getImagePath() + ".jpg");
                    Bitmap signBitMap = DropboxHelper.returnCapturedImage("/Customers/" + Constant.company + "/Completed/" + Constant.trip + "/" + Constant.deliveryData.getDocument() + "/" + Constant.deliveryData.getSignPath() + ".jpg");

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            imageViewItem.setImageBitmap(itemBitMap);
                            imageViewSignature.setImageBitmap(signBitMap);
                        }
                    });
                }
            });

            thread.start();

            SimpleDateFormat inputFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

            Date date = inputFormat.parse(Constant.deliveryData.getTime());

            String dateString = dateFormat.format(date);
            String timeString = timeFormat.format(date);

            textViewTime.setText(timeString);
            textViewDate.setText(dateString);

            if (Constant.deliveryData.getComment().equals("")) {

                textViewComment.setVisibility(TextView.GONE);

            } else {

                textViewComment.setVisibility(TextView.VISIBLE);

                textViewComment.setText(Constant.deliveryData.getComment());
            }

        } catch (Exception e) {

            e.printStackTrace();
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        startActivity(new Intent(CapturedData.this, DetailDashboardCompleted.class));

        finish();
    }
}