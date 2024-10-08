package com.example.edmanager.activity;

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

    FloatingActionButton fabCreateTrip;

    DashboardAdapter adapter;
    RecyclerView recyclerView;

    Boolean deleteModeAnimated;
    int originalEndMargin;

    File[] localTrips;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        //buttonCreateTrip = findViewById(R.id.buttonCreateTrip);
        buttonDeleteTrip = findViewById(R.id.customButtonDelete);
        recyclerView = findViewById(R.id.recyclerViewDashboardMain);
        constraintLayout = findViewById(R.id.dashboardLayout);
        fabCreateTrip = findViewById(R.id.fabCreateTrip);

        Constant.listDeleteTrips = new ArrayList<>();
        deleteModeAnimated = false;
        //originalEndMargin = ((ConstraintLayout.LayoutParams) buttonCreateTrip.getLayoutParams()).rightMargin;

        adapter = new DashboardAdapter(Constant.listTrips, getApplicationContext(), new DashboardAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String tripNumber) {

                if (!Constant.tripDeleteMode) {

                    if (Constant.listConfiguredTrips.contains(tripNumber)) {

                        if (Constant.listCompletedTrips.contains(tripNumber)) {

                            ToastLogger.message(getApplicationContext(), "This will open up a screen showing all captured data. The user might then archive the trip once done viewing.");

                        } else if (Constant.listInProgressTrips.contains(tripNumber)) {

                            ToastLogger.message(getApplicationContext(), "Trip in progress.");

                        } else {

                            Constant.trip = tripNumber;
                            Constant.deliveryCount = JsonHelper.returnDeliveryCount(getApplicationContext(), tripNumber);

                            startActivity(new Intent(Dashboard.this, DetailDashboard.class));

                            finish();
                        }

                    } else {

                        AlertDialog dialog = new AlertDialog.Builder(Dashboard.this, R.style.AlertDialogStyle).create();

                        dialog.setTitle("Delete Empty Trip/Add Delivery");

                        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Add Delivery", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                Constant.trip = tripNumber;
                                Constant.listParcels = new ArrayList<>();

                                startActivity(new Intent(Dashboard.this, NewDeliveryHeader.class));

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

        buttonDeleteTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                for (String tripName :Constant.listDeleteTrips) {

                    FileHelper.deleteFile(getApplicationContext(), tripName);

                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {

                            DropboxHelper.deleteFile(tripName);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    int position = Constant.listTrips.indexOf(tripName);

                                    Constant.listDeleteTrips.remove(tripName);

                                    adapter.notifyItemRemoved(position);

                                    Constant.listTrips.remove(tripName);
                                    Constant.listConfiguredTrips.remove(tripName);

                                    if (Constant.listDeleteTrips.size() < 1) {

                                        ConstraintSet constraintSet = new ConstraintSet();
                                        constraintSet.clone(constraintLayout);

                                        // Apply new end margin
                                        constraintSet.setMargin(R.id.buttonCreateTrip, ConstraintSet.END, originalEndMargin);

                                        TransitionManager.beginDelayedTransition(constraintLayout);
                                        constraintSet.applyTo(constraintLayout);

                                        buttonDeleteTrip.setVisibility(View.INVISIBLE);

                                        deleteModeAnimated = false;
                                        Constant.tripDeleteMode = false;
                                    }
                                }
                            });
                        }
                    });

                    thread.start();
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
            }

            updateStatus();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }


    public void updateStatus() {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                while (true) {

                    ArrayList<String> listTripsCopy = new ArrayList<>();

                    listTripsCopy.addAll(Constant.listTrips);

                    for (String trip : listTripsCopy) {

                        int status = DropboxHelper.returnTripStatus(trip);

                        if (status == 1) {

                            if (!Constant.listCompletedTrips.contains(trip)) {

                                Constant.listCompletedTrips.add(trip);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        adapter.notifyItemChanged(Constant.listTrips.indexOf(trip));

                                        Log.i("SYNC", trip + " completed");
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

                                        Log.i("SYNC", trip + " in progress");
                                    }
                                });
                            }

                        } else if (status == 2) {

                            if (Constant.listInProgressTrips.contains(trip)) {

                                Constant.listInProgressTrips.remove(trip);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        adapter.notifyItemChanged(Constant.listTrips.indexOf(trip));

                                        Log.i("SYNC", trip + " open");
                                    }
                                });
                            }
                        }
                    }
                }
            }
        });

        thread.start();
    }


    public void runDeleteMode(String tripNumber) {

        //Constant.tripHighlightedForDelete = true;

        if (Constant.listDeleteTrips.contains(tripNumber)) {

            Constant.listDeleteTrips.remove(tripNumber);

            Constant.tripDeleteMode = true;

            adapter.notifyItemChanged(Constant.listTrips.indexOf(tripNumber));

            if (Constant.listDeleteTrips.size() < 1) {

                deleteModeAnimated = false;
                Constant.tripDeleteMode = false;
            }

        } else {

            Constant.listDeleteTrips.add(tripNumber);

            Constant.tripDeleteMode = true;

            adapter.notifyItemChanged(Constant.listTrips.indexOf(tripNumber));

            if (!deleteModeAnimated) {

                deleteModeAnimated = true;
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