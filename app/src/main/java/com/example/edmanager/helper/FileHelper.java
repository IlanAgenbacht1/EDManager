package com.example.edmanager.helper;

import android.content.Context;

import com.example.edmanager.utility.Constant;
import com.example.edmanager.utility.ToastLogger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileHelper {

    public static File getTripFile(Context context, String trip, String directory) {
        try {

            File file = new File(context.getFilesDir() + directory, trip + ".json");

            if (file.exists()) {

                return file;

            } else {

                return null;
            }

        } catch (Exception e) {

            e.printStackTrace();

            return null;
        }
    }


    public static File[] returnTripFiles(Context context) {
        try {

            File directory = new File(context.getFilesDir() + Constant.LOCAL_EXISTING_TRIP_DIRECTORY);

            if (directory.exists()) {

                return directory.listFiles();
            }

            return null;

        } catch (Exception e) {

            e.printStackTrace();

            return null;
        }
    }
}

