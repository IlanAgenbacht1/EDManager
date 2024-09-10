package com.example.edmanager.helper;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.example.edmanager.database.DeliveryModel;
import com.example.edmanager.utility.Constant;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class JsonHelper {


    public static File createOrAppendTrip(Context context, DeliveryModel data, String directory) {
        try {

            File file = new File(context.getFilesDir() + directory);

            if (!file.exists()) {

                file.mkdirs();
            }

            file = new File(file.getPath(), data.getTripId() + ".json");

            if (!file.exists()) {

                if (file.createNewFile()) {

                    Log.i("File", "New trip");

                    writeFile(context, file, data, false);
                }

            } else {

                Log.i("File", "Existing trip");

                writeFile(context, file, data, true);
            }

            return file;

        } catch (Exception e) {

            e.printStackTrace();

            return null;
        }        
    }
    
    
    public static JSONObject parseDeliveryData(DeliveryModel data) {
        try {

            JSONObject json = new JSONObject();
            json.put("documentNumber", data.getDocument());

            JSONObject jsonCustomer = new JSONObject();
            jsonCustomer.put("name", data.getCustomerName());
            jsonCustomer.put("contactName", data.getContactName());
            jsonCustomer.put("contactNumber", data.getContactNumber());
            json.put("customer", jsonCustomer);

            json.put("address", data.getAddress());

            JSONObject jsonGPS = new JSONObject();
            jsonGPS.put("latitude", data.getLocation().getLatitude());
            jsonGPS.put("longitude", data.getLocation().getLongitude());
            json.put("gpsLocation", jsonGPS);

            json.put("numParcels", data.getNumberOfParcels());

            JSONArray jsonParcels = new JSONArray();

            for (String item : data.getParcelNumbers()) {

                jsonParcels.put(item);
            }

            json.put("parcelNumbers", jsonParcels);

            return json;
            
        } catch(Exception e) {
            
            e.printStackTrace();
            
            return null;
        }
    }


    public static JSONObject readFile(Context context, String trip) {
        try {

            StringBuilder jsonString = new StringBuilder();

            JSONObject tripData;

            try (BufferedReader reader = new BufferedReader(new FileReader(new File(context.getFilesDir() + Constant.LOCAL_EXISTING_TRIP_DIRECTORY + trip + ".json")))) {

                String line;

                while ((line = reader.readLine()) != null) {

                    jsonString.append(line);
                }

                tripData = new JSONObject(jsonString.toString());
            }

            return tripData;

        } catch (Exception e) {

            e.printStackTrace();

            return null;
        }
    }


    public static void writeFile(Context context, File file, DeliveryModel data, Boolean existingTrip) {
        try {

            JSONObject jsonFinal = new JSONObject();
            JSONArray jsonArray = new JSONArray();

            if (existingTrip) {

                JSONObject jsonInitial = readFile(context, data.getTripId());
                jsonArray = jsonInitial.getJSONArray("stops");
            }

            JSONObject jsonData = parseDeliveryData(data);

            jsonArray.put(jsonData);
            jsonFinal.put("stops", jsonArray);

            Writer writer = new BufferedWriter(new FileWriter(file));
            writer.write(jsonFinal.toString(4));
            writer.close();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }


    public static int returnDeliveryCount(Context context, String trip) {
        try {

            JSONObject jsonData = readFile(context, trip);
            JSONArray jsonArray = jsonData.getJSONArray("stops");

            return jsonArray.length();

        } catch (Exception e) {

            e.printStackTrace();

            return 0;
        }
    }

    public static ArrayList<DeliveryModel> returnListDeliveryData(Context context, String trip) {
        try {

            JSONObject json = readFile(context, trip);
            JSONArray jsonArray = json.getJSONArray("stops");

            ArrayList<DeliveryModel> listDeliveries = new ArrayList<>();

            for (int i = 0; i < jsonArray.length(); i++) {

                DeliveryModel data = new DeliveryModel();

                JSONObject jsonData = jsonArray.getJSONObject(i);

                data.setDocument(jsonData.getString("documentNumber"));

                JSONObject jsonCustomerData = jsonData.getJSONObject("customer");
                data.setCustomerName(jsonCustomerData.getString("name"));
                data.setContactName(jsonCustomerData.getString("contactName"));
                data.setContactNumber(jsonCustomerData.getString("contactNumber"));

                data.setAddress(jsonData.getString("address"));

                JSONObject jsonGpsData = jsonData.getJSONObject("gpsLocation");
                Location location = new Location("null");
                location.setLatitude(jsonGpsData.getDouble("latitude"));
                location.setLongitude(jsonGpsData.getDouble("longitude"));
                data.setLocation(location);

                data.setNumberOfParcels(jsonData.getInt("numParcels"));

                List<String> listParcels = new ArrayList<>();
                JSONArray jsonParcels = jsonData.getJSONArray("parcelNumbers");
                for (int j = 0; j < jsonParcels.length(); j++) {
                    listParcels.add(jsonParcels.getString(j));
                }
                data.setParcelNumbers(listParcels);

                listDeliveries.add(data);
            }

            return listDeliveries;

        } catch (Exception e) {

            e.printStackTrace();

            return null;
        }
    }

}
