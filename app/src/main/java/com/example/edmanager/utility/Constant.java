package com.example.edmanager.utility;

import android.location.Address;

import com.example.edmanager.database.DeliveryModel;

import java.util.ArrayList;

public class Constant {

    public static String company = "";
    public static String trip = "";

    public static String adapterRemoveInProgressTrip = "";
    public static String adapterRemoveStagingTrip = "";

    public static DeliveryModel deliveryData;
    public static DeliveryModel oldDeliveryData;

    public static ArrayList<String> listParcels = new ArrayList<>();
    public static ArrayList<String> listTrips = new ArrayList<>();
    public static ArrayList<String> listConfiguredTrips = new ArrayList<>();
    public static ArrayList<String> listInProgressTrips = new ArrayList<>();
    public static ArrayList<String> adapterInProgressTrips = new ArrayList<>();
    public static ArrayList<String> listCompletedTrips = new ArrayList<>();
    public static ArrayList<String> listStagingTrips = new ArrayList<>();
    public static ArrayList<String> adapterStagingTrips = new ArrayList<>();
    public static ArrayList<String> listDeleteTrips = new ArrayList<>();
    public static ArrayList<String> listDeleteDeliveries = new ArrayList<>();
    public static ArrayList<String> listDeliveryNames = new ArrayList<>();

    public static int deliveryCount;

    public static boolean refreshDashboard;
    public static boolean tripHighlightedForDelete;
    public static boolean tripDeleteMode;
    public static boolean tripStaging;
    public static boolean deliveryEdit;
    public static boolean deliveryCompleted;

    public static Address address;

    public static final String LOCAL_TRIP_DIRECTORY = "/Trip/";
    public static final String LOCAL_EXISTING_TRIP_DIRECTORY = "/ExistingTrip/";
}
