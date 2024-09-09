package com.example.edmanager.helper;

import android.content.Context;
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

public class JsonHelper {


    public static File writeTripFile(Context context, DeliveryModel data, String directory) {
        try {

            File file = new File(context.getFilesDir() + directory);

            if (!file.exists()) {

                file.mkdirs();
            }

            file = new File(file.getPath(), data.getTripId() + ".json");

            if (!file.exists()) {

                if (file.createNewFile()) {

                    Log.i("File", "New trip");

                    JSONObject jsonObject = parseDeliveryData(data);

                    JSONArray array = new JSONArray();
                    array.put(jsonObject);

                    JSONObject jsonFinal = new JSONObject();
                    jsonFinal.put("stops", array);

                    Writer writer = new BufferedWriter(new FileWriter(file));
                    writer.write(jsonFinal.toString(4));
                    writer.close();
                }

            } else {

                Log.i("File", "Existing trip");

                JSONObject jsonInitial = readFile(context, data.getTripId());

                JSONObject jsonNewDelivery = parseDeliveryData(data);

                JSONArray array = jsonInitial.getJSONArray("stops");
                array.put(jsonNewDelivery);

                JSONObject jsonFinal = new JSONObject();
                jsonFinal.put("stops", array);

                Writer writer = new BufferedWriter(new FileWriter(file));
                writer.write(jsonFinal.toString(4));
                writer.close();
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

            JSONObject tripData = new JSONObject();

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

}
