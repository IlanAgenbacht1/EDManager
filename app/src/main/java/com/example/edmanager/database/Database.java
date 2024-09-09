package com.example.edmanager.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.edmanager.utility.ToastLogger;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Database {

    private static final String DATABASE_NAME = "EDManager";
    private final int DATABASE_VERSION = 1;

    private Context ourContext;
    private SQLiteDatabase ourDatabase;
    private DBHelper ourHelper;

    private static final String TRIP_TABLE = "TripTable";
    private static final String DELIVERY_TABLE = "DeliveryTable";
    private static final String PARCEL_TABLE = "ParcelTable";

    private static final String KEY_TRIPID = "_trip";
    private static final String KEY_DATE_CREATED = "_dateCreated";
    private static final String KEY_ROWID = "_id";
    private static final String KEY_DOCUMENT = "_docu";
    private static final String KEY_PARCEL = "_parcel";
    private static final String KEY_CUSTOMER = "_customer";
    private static final String KEY_ADDRESS = "_address";
    private static final String KEY_CONTACTNAME = "_contactName";
    private static final String KEY_CONTACTNUMBER = "_contactNumber";
    private static final String KEY_LATITUDE = "_latitude";
    private static final String KEY_LONGITUDE = "_longitude";
    private static final String KEY_PARCELS = "_parcelQty";


    public Database(Context ourContext) {
        this.ourContext = ourContext;
    }

    private class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }


        @Override
        public void onCreate(SQLiteDatabase db) {

            String sqlCreateTripTable = "CREATE TABLE " + TRIP_TABLE + " (" +
                    KEY_TRIPID + " TEXT NOT NULL, " +
                    KEY_DATE_CREATED + " TEXT NOT NULL);";

            db.execSQL(sqlCreateTripTable);

            String sqlCreateDeliveryTable = "CREATE TABLE " + DELIVERY_TABLE + " (" +
                    KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    KEY_TRIPID + " TEXT , " +
                    KEY_DOCUMENT + " TEXT NOT NULL, " +
                    KEY_CUSTOMER + " TEXT NOT NULL, " +
                    KEY_ADDRESS + " TEXT NOT NULL, " +
                    KEY_CONTACTNAME + " TEXT NOT NULL, " +
                    KEY_CONTACTNUMBER + " TEXT NOT NULL, " +
                    KEY_PARCELS + " INTEGER NOT NULL, " +
                    KEY_LATITUDE + " TEXT NOT NULL, " +
                    KEY_LONGITUDE + " TEXT NOT NULL);";

            db.execSQL(sqlCreateDeliveryTable);

            String sqlCreateParcelTable = "CREATE TABLE " + PARCEL_TABLE + " (" +
                    KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    KEY_TRIPID + " TEXT, " +
                    KEY_DOCUMENT + " TEXT NOT NULL, " +
                    KEY_PARCEL + " TEXT NOT NULL);";

            db.execSQL(sqlCreateParcelTable);
        }


        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            db.execSQL("DROP TABLE IF EXISTS " + TRIP_TABLE);

            onCreate(db);
        }
    }


    public Database open() throws SQLException {

        ourHelper = new DBHelper(ourContext);

        ourDatabase = ourHelper.getWritableDatabase();

        return this;
    }


    public ArrayList<String> returnTrips() {

        Cursor cursor = ourDatabase.rawQuery("SELECT " + KEY_TRIPID + " FROM " + TRIP_TABLE, null);

        int tripColumnIndex = cursor.getColumnIndex(KEY_TRIPID);

        ArrayList<String> listTrip = new ArrayList<>();

        while (cursor.moveToNext()) {

            listTrip.add(cursor.getString(tripColumnIndex));
        }

        return listTrip;
    }


    public boolean insertTrip(String trip) {

        try {

            String currentDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.US).format(new Date());

            Cursor cursor = ourDatabase.rawQuery("INSERT INTO " + TRIP_TABLE + "(" + KEY_TRIPID + ", " + KEY_DATE_CREATED + ")" + " VALUES ('" + trip + "', '" + currentDate + "')", null);

            cursor.moveToFirst();

            cursor.close();

            Log.i("Database", "Inserted into TripTable: " + trip + " " + currentDate);

            return true;

        } catch (Exception e) {

            ToastLogger.exception(ourContext.getApplicationContext(), e);

            e.printStackTrace();

            return false;
        }
    }


    public void deleteTrip(String trip) {

        Cursor cursor = ourDatabase.rawQuery("DELETE FROM " + TRIP_TABLE + " WHERE " + KEY_TRIPID + " = '" + trip + "'", null);

        cursor.moveToFirst();

        cursor.close();

        cursor = ourDatabase.rawQuery("DELETE FROM " + DELIVERY_TABLE + " WHERE " + KEY_TRIPID + "= '" + trip + "'", null);

        cursor.moveToFirst();

        cursor.close();
    }


    public long insertDelivery(DeliveryModel data) {

        ContentValues cv = new ContentValues();

        cv.put(KEY_TRIPID, data.getTripId());
        cv.put(KEY_DOCUMENT, data.getDocument());
        cv.put(KEY_CUSTOMER, data.getCustomerName());
        cv.put(KEY_CONTACTNAME, data.getContactName());
        cv.put(KEY_CONTACTNUMBER, data.getContactNumber());
        cv.put(KEY_ADDRESS, data.getAddress());
        cv.put(KEY_PARCELS, data.getNumberOfParcels());
        cv.put(KEY_LATITUDE, data.getLocation().getLatitude());
        cv.put(KEY_LONGITUDE, data.getLocation().getLongitude());

        return ourDatabase.insert(DELIVERY_TABLE, null, cv);
    }


    public int returnDeliveryCount(String trip) {

        Cursor cursor  = ourDatabase.rawQuery("SELECT * FROM " + DELIVERY_TABLE + " WHERE " + KEY_TRIPID + " = '" + trip + "'", null);

        int count = 0;

        while (cursor.moveToNext()) {

            count++;
        }

        return count;
    }



    public ArrayList<String> returnDeliveries(String trip) {

        ArrayList<String> listDeliveries = new ArrayList<>();

        Cursor cursor = ourDatabase.rawQuery("SELECT " + KEY_DOCUMENT + " FROM " + DELIVERY_TABLE + " WHERE " + KEY_TRIPID + " = '" + trip + "'", null);

        int documentKey = cursor.getColumnIndex(KEY_DOCUMENT);

        while (cursor.moveToNext()) {

            listDeliveries.add(cursor.getString(documentKey));

            Log.i("Database", cursor.getString(documentKey));
        }

        cursor.close();

        return listDeliveries;
    }


    public DeliveryModel returnDeliveryData(String document, String trip) {

        Cursor cursor = ourDatabase.rawQuery("SELECT * FROM " + DELIVERY_TABLE + " WHERE " + KEY_DOCUMENT + " = '" + document + "' AND " + KEY_TRIPID + " = '" + trip + "'", null);

        int documentIndex = cursor.getColumnIndex(KEY_DOCUMENT);
        int customerIndex = cursor.getColumnIndex(KEY_CUSTOMER);
        int addressIndex = cursor.getColumnIndex(KEY_ADDRESS);
        int contactNameIndex = cursor.getColumnIndex(KEY_CONTACTNAME);
        int contactNumberIndex = cursor.getColumnIndex(KEY_CONTACTNUMBER);
        int parcelsIndex = cursor.getColumnIndex(KEY_PARCELS);

        DeliveryModel data = new DeliveryModel();

        while (cursor.moveToNext()) {

            data.setDocument(cursor.getString(documentIndex));
            data.setCustomerName(cursor.getString(customerIndex));
            data.setAddress(cursor.getString(addressIndex));
            data.setContactName(cursor.getString(contactNameIndex));
            data.setContactNumber(cursor.getString(contactNumberIndex));
            data.setNumberOfParcels(cursor.getInt(parcelsIndex));
        }

        cursor.close();

        Log.i("Database", data.getDocument() + " " + data.getCustomerName() + " " + data.getAddress());

        return data;
    }

}