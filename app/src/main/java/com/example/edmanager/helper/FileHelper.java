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

            File directory = new File(context.getFilesDir() + Constant.LOCAL_EXISTING_TRIP_DIRECTORY + Constant.company + "/");

            if (directory.exists()) {

                return directory.listFiles();
            }

            directory.mkdirs();

            return null;

        } catch (Exception e) {

            e.printStackTrace();

            return null;
        }
    }


    public static void deleteAllFiles(Context context) {
        try {

            File[] files = returnTripFiles(context);

            if (files != null) {

                for (File file : files) {

                    file.delete();
                }
            }

        } catch (Exception e) {

            e.printStackTrace();
        }
    }


    public static void deleteFile(Context context, String fileName) {
        try {

            File[] files = returnTripFiles(context);

            if (files != null) {

                for (File file : files) {

                    if (file.getName().substring(0, file.getName().length() - 5).equals(fileName)) {

                        file.delete();
                    }
                }
            }

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

}

