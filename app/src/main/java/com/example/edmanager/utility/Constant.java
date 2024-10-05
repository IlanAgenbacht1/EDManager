package com.example.edmanager.utility;

import android.location.Address;

import com.example.edmanager.database.DeliveryModel;

import java.util.ArrayList;

public class Constant {

    public static String company = "";
    public static String trip = "";

    public static DeliveryModel deliveryData;

    public static ArrayList<String> listParcels = new ArrayList<>();
    public static ArrayList<String> listTrips = new ArrayList<>();
    public static ArrayList<String> listConfiguredTrips = new ArrayList<>();
    public static ArrayList<String> listInProgressTrips = new ArrayList<>();
    public static ArrayList<String> listCompletedTrips = new ArrayList<>();
    public static ArrayList<String> listDeleteTrips = new ArrayList<>();

    public static int deliveryCount;

    public static boolean refreshDashboard;
    public static boolean highlightTripDelete;
    public static boolean deleteMode;

    public static Address address;

    public static final String LOCAL_TRIP_DIRECTORY = "/Trip/";
    public static final String LOCAL_EXISTING_TRIP_DIRECTORY = "/ExistingTrip/";
}
