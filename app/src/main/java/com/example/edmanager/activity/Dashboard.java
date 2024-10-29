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
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.ArrayList;

public class Dashboard extends AppCompatActivity {

    Button buttonCreateTrip;
    ConstraintLayout buttonDeleteTrip, constraintLayout;
    EditText tripEditText;
    TextView textViewStatus;
    FloatingActionButton fabCreateTrip, fabDelete, fabViewArchive;
    DashboardAdapter adapter;
    RecyclerView recyclerView;
    Boolean deleteModeAnimated;
    int originalEndMargin;
    File[] localTrips;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        recyclerView = findViewById(R.id.recyclerViewDashboardMain);
        constraintLayout = findViewById(R.id.dashboardLayout);
        fabCreateTrip = findViewById(R.id.fabCreateTrip);
        fabDelete = findViewById(R.id.fabDeleteTrip);
        fabViewArchive = findViewById(R.id.fabViewArchive);

        localTrips = null;
        Constant.listDeleteTrips = new ArrayList<>();

        adapter = new DashboardAdapter(Constant.listTrips, getApplicationContext(), new DashboardAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String tripNumber) {

                if (!Constant.tripDeleteMode) {

                    if (Constant.listConfiguredTrips.contains(tripNumber)) {

                        if (Constant.listCompletedTrips.contains(tripNumber)) {

                            Constant.tripStaging = false;
                            Constant.deliveryCompleted = true;
                            Constant.trip = tripNumber;

                            startActivity(new Intent(Dashboard.this, DetailDashboardCompleted.class));

                            finish();

                        } else if (Constant.listInProgressTrips.contains(tripNumber)) {

                            Constant.deliveryCompleted = false;

                            ToastLogger.message(getApplicationContext(), "Trip in progress.");

                        } else {

                            Constant.deliveryCompleted = false;

                            if (Constant.listStagingTrips.contains(tripNumber)) {

                                Constant.tripStaging = true;

                            } else {

                                Constant.tripStaging = false;
                            }

                            Constant.trip = tripNumber;
                            Constant.deliveryCount = JsonHelper.returnDeliveryCount(getApplicationContext(), tripNumber);

                            startActivity(new Intent(Dashboard.this, DetailDashboard.class));

                            finish();
                        }

                    } else {

                        Constant.trip = tripNumber;
                        Constant.tripStaging = true;
                        Constant.listParcels = new ArrayList<>();

                        startActivity(new Intent(Dashboard.this, DetailDashboard.class));

                        finish();
                    }

                } else {

                    if (Constant.listDeleteTrips.size() == 1) {

                        ToastLogger.message(getApplicationContext(), "Please finish deleting " + Constant.listDeleteTrips.get(0));

                    } else {

                        ToastLogger.message(getApplicationContext(), "Please finish deleting selected trips");
                    }
                }
            }

            @Override
            public void onItemLongClick(String tripNumber) {

                runDeleteMode(tripNumber);
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        fabCreateTrip.setOnClickListener(new View.OnClickListener() {
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

        fabDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                for (String tripName :Constant.listDeleteTrips) {

                    FileHelper.deleteFile(getApplicationContext(), tripName);

                    Constant.listDeleteTrips.remove(tripName);

                    adapter.notifyItemRemoved(Constant.listTrips.indexOf(tripName));

                    Constant.listTrips.remove(tripName);
                    Constant.listConfiguredTrips.remove(tripName);

                    if (Constant.listDeleteTrips.isEmpty()) {

                        ObjectAnimator scaleX2 = ObjectAnimator.ofFloat(fabDelete, "scaleX", 0f);
                        ObjectAnimator scaleY2 = ObjectAnimator.ofFloat(fabDelete, "scaleY", 0f);
                        AnimatorSet animatorSet2 = new AnimatorSet();
                        animatorSet2.playTogether(scaleX2, scaleY2);
                        animatorSet2.setDuration(200); // animation duration in milliseconds

                        animatorSet2.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                fabDelete.setVisibility(View.INVISIBLE); // Set FAB to invisible after animation ends
                                fabCreateTrip.setVisibility(View.VISIBLE);
                                ObjectAnimator scaleX2 = ObjectAnimator.ofFloat(fabCreateTrip, "scaleX", 1f);
                                ObjectAnimator scaleY2 = ObjectAnimator.ofFloat(fabCreateTrip, "scaleY", 1f);
                                AnimatorSet animatorSet2 = new AnimatorSet();
                                animatorSet2.playTogether(scaleX2, scaleY2);
                                animatorSet2.setDuration(200);

                                animatorSet2.start();
                            }
                        });

                        animatorSet2.start();
                    }

                    DropboxHelper.deleteFile(tripName);
                }
            }
        });

        updateFiles();
    }


    public void updateFiles() {
        try {

            if (Constant.refreshDashboard) {

                localTrips = FileHelper.returnTripFiles(getApplicationContext());

                if (localTrips == null || localTrips.length == 0) {

                    ToastLogger.message(getApplicationContext(), "Loading trips...");

                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {

                            DropboxHelper.fetchAllTrips(getApplicationContext());

                            localTrips = FileHelper.returnTripFiles(getApplicationContext());

                            for (File tripFile : localTrips) {

                                String trip = tripFile.getName().substring(0, tripFile.getName().length() - 5);

                                if (!Constant.listTrips.contains(trip)) {

                                    Constant.listTrips.add(trip);
                                }

                                if (JsonHelper.returnDeliveryCount(getApplicationContext(), trip) > 0 && !Constant.listConfiguredTrips.contains(trip)) {

                                    Constant.listConfiguredTrips.add(trip);
                                }
                            }

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    adapter.notifyDataSetChanged();

                                    fabCreateTrip.setVisibility(View.VISIBLE);
                                    ObjectAnimator scaleX = ObjectAnimator.ofFloat(fabCreateTrip, "scaleX", 1f);
                                    ObjectAnimator scaleY = ObjectAnimator.ofFloat(fabCreateTrip, "scaleY", 1f);
                                    AnimatorSet animatorSet = new AnimatorSet();
                                    animatorSet.playTogether(scaleX, scaleY);
                                    animatorSet.setDuration(400); // animation duration in milliseconds
                                    animatorSet.start();

                                    fabViewArchive.setVisibility(View.VISIBLE);
                                    ObjectAnimator scaleX1 = ObjectAnimator.ofFloat(fabViewArchive, "scaleX", 1f);
                                    ObjectAnimator scaleY1 = ObjectAnimator.ofFloat(fabViewArchive, "scaleY", 1f);
                                    AnimatorSet animatorSet1 = new AnimatorSet();
                                    animatorSet1.playTogether(scaleX1, scaleY1);
                                    animatorSet1.setDuration(400); // animation duration in milliseconds
                                    animatorSet1.start();
                                }
                            });
                        }
                    });

                    thread.start();

                } else {

                    for (File tripFile : localTrips) {

                        String trip = tripFile.getName().substring(0, tripFile.getName().length() - 5);

                        if (!Constant.listTrips.contains(trip)) {

                            Constant.listTrips.add(trip);
                        }

                        if (JsonHelper.returnDeliveryCount(getApplicationContext(), trip) > 0 && !Constant.listConfiguredTrips.contains(trip)) {

                            Constant.listConfiguredTrips.add(trip);
                        }
                    }

                    adapter.notifyDataSetChanged();

                    fabCreateTrip.setVisibility(View.VISIBLE);
                    ObjectAnimator scaleX = ObjectAnimator.ofFloat(fabCreateTrip, "scaleX", 1f);
                    ObjectAnimator scaleY = ObjectAnimator.ofFloat(fabCreateTrip, "scaleY", 1f);
                    AnimatorSet animatorSet = new AnimatorSet();
                    animatorSet.playTogether(scaleX, scaleY);
                    animatorSet.setDuration(400); // animation duration in milliseconds
                    animatorSet.start();

                    fabViewArchive.setVisibility(View.VISIBLE);
                    ObjectAnimator scaleX1 = ObjectAnimator.ofFloat(fabViewArchive, "scaleX", 1f);
                    ObjectAnimator scaleY1 = ObjectAnimator.ofFloat(fabViewArchive, "scaleY", 1f);
                    AnimatorSet animatorSet1 = new AnimatorSet();
                    animatorSet1.playTogether(scaleX1, scaleY1);
                    animatorSet1.setDuration(400); // animation duration in milliseconds
                    animatorSet1.start();
                }

                Constant.refreshDashboard = false;

            } else {

                fabCreateTrip.setVisibility(View.VISIBLE);
                ObjectAnimator scaleX = ObjectAnimator.ofFloat(fabCreateTrip, "scaleX", 1f);
                ObjectAnimator scaleY = ObjectAnimator.ofFloat(fabCreateTrip, "scaleY", 1f);
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(scaleX, scaleY);
                animatorSet.setDuration(400); // animation duration in milliseconds
                animatorSet.start();

                fabViewArchive.setVisibility(View.VISIBLE);
                ObjectAnimator scaleX1 = ObjectAnimator.ofFloat(fabViewArchive, "scaleX", 1f);
                ObjectAnimator scaleY1 = ObjectAnimator.ofFloat(fabViewArchive, "scaleY", 1f);
                AnimatorSet animatorSet1 = new AnimatorSet();
                animatorSet1.playTogether(scaleX1, scaleY1);
                animatorSet1.setDuration(400); // animation duration in milliseconds
                animatorSet1.start();
            }

            updateStatus();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }


    public void updateStatus() {
        Thread thread = new Thread(() -> {
            while (true) {
                ArrayList<String> listTripsCopy = new ArrayList<>(Constant.listTrips);

                for (String trip : listTripsCopy) {
                    int status = DropboxHelper.returnTripStatus(trip);

                    if (status == 1 && !Constant.listCompletedTrips.contains(trip)) {
                        Constant.listCompletedTrips.add(trip);
                        updateAdapter(trip);
                    } else if (status == 0 && !Constant.adapterInProgressTrips.contains(trip)) {
                        if (!Constant.listInProgressTrips.contains(trip)) {
                            Constant.listInProgressTrips.add(trip);
                        }
                        updateAdapter(trip);
                    } else if (status == 2) {
                        if (Constant.listInProgressTrips.contains(trip)) {
                            Constant.adapterRemoveInProgressTrip = trip;
                            updateAdapter(trip);
                        } else if (Constant.listStagingTrips.contains(trip)) {
                            Constant.adapterRemoveStagingTrip = trip;
                            updateAdapter(trip);
                        }
                    } else if (status == 3 && !Constant.adapterStagingTrips.contains(trip)) {
                        if (!Constant.listStagingTrips.contains(trip)) {
                            Constant.listStagingTrips.add(trip);
                        }
                        updateAdapter(trip);
                    }
                }

                // Sleep to avoid overwhelming the UI thread
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        thread.start();
    }

    private void updateAdapter(String trip) {
        runOnUiThread(() -> {
            int position = Constant.listTrips.indexOf(trip);
            if (position != -1) {
                adapter.notifyItemChanged(position);
                Log.i("SYNC", "Updated " + trip + " at position " + position);
            } else {
                Log.w("SYNC", "Trip not found in list: " + trip);
            }
        });
    }


    public void runDeleteMode(String tripNumber) {

        //Constant.tripHighlightedForDelete = true;

        if (Constant.listDeleteTrips.contains(tripNumber)) {

            Constant.listDeleteTrips.remove(tripNumber);

            adapter.notifyItemChanged(Constant.listTrips.indexOf(tripNumber));

            if (Constant.listDeleteTrips.isEmpty()) {

                ObjectAnimator scaleX2 = ObjectAnimator.ofFloat(fabDelete, "scaleX", 0f);
                ObjectAnimator scaleY2 = ObjectAnimator.ofFloat(fabDelete, "scaleY", 0f);
                AnimatorSet animatorSet2 = new AnimatorSet();
                animatorSet2.playTogether(scaleX2, scaleY2);
                animatorSet2.setDuration(200); // animation duration in milliseconds

                animatorSet2.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        fabDelete.setVisibility(View.INVISIBLE); // Set FAB to invisible after animation ends
                        fabCreateTrip.setVisibility(View.VISIBLE);
                        ObjectAnimator scaleX2 = ObjectAnimator.ofFloat(fabCreateTrip, "scaleX", 1f);
                        ObjectAnimator scaleY2 = ObjectAnimator.ofFloat(fabCreateTrip, "scaleY", 1f);
                        AnimatorSet animatorSet2 = new AnimatorSet();
                        animatorSet2.playTogether(scaleX2, scaleY2);
                        animatorSet2.setDuration(200);

                        animatorSet2.start();
                    }
                });

                animatorSet2.start();
            }

        } else {

            if (!Constant.listInProgressTrips.contains(tripNumber) && !Constant.listCompletedTrips.contains(tripNumber)) {

                Constant.listDeleteTrips.add(tripNumber);

                adapter.notifyItemChanged(Constant.listTrips.indexOf(tripNumber));

                if (Constant.listDeleteTrips.size() == 1) {

                    ObjectAnimator scaleX2 = ObjectAnimator.ofFloat(fabCreateTrip, "scaleX", 0f);
                    ObjectAnimator scaleY2 = ObjectAnimator.ofFloat(fabCreateTrip, "scaleY", 0f);
                    AnimatorSet animatorSet2 = new AnimatorSet();
                    animatorSet2.playTogether(scaleX2, scaleY2);
                    animatorSet2.setDuration(200); // animation duration in milliseconds

                    animatorSet2.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            fabCreateTrip.setVisibility(View.INVISIBLE); // Set FAB to invisible after animation ends
                            fabDelete.setVisibility(View.VISIBLE);
                            ObjectAnimator scaleX2 = ObjectAnimator.ofFloat(fabDelete, "scaleX", 1f);
                            ObjectAnimator scaleY2 = ObjectAnimator.ofFloat(fabDelete, "scaleY", 1f);
                            AnimatorSet animatorSet2 = new AnimatorSet();
                            animatorSet2.playTogether(scaleX2, scaleY2);
                            animatorSet2.setDuration(200);

                            animatorSet2.start();
                        }
                    });

                    animatorSet2.start();
                }

            }
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        startActivity(new Intent(Dashboard.this, Splash.class));
        finish();
    }

}