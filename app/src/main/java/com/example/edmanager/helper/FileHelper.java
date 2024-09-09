package com.example.edmanager.helper;

import android.content.Context;

import com.example.edmanager.utility.Constant;
import com.example.edmanager.utility.ToastLogger;

import java.io.File;

public class FileHelper {

    public static File getTripFile(Context context, String trip) {
        try {

            File file = new File(context.getFilesDir() + Constant.LOCAL_TRIP_DIRECTORY);

            if (!file.exists()) {

                file.mkdirs();
            }

            file = new File(file.getPath(), trip + ".json");

            return file;

        } catch (Exception e) {

            e.printStackTrace();

            return null;
        }
    }
}

