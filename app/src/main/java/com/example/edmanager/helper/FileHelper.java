package com.example.edmanager.helper;

import android.content.Context;

import com.example.edmanager.utility.Constant;
import com.example.edmanager.utility.ToastLogger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.io.FileInputStream;
import java.io.FileOutputStream;

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


    public static File[] returnTripArchiveFiles(Context context) {
        try {

            File directory = new File(context.getFilesDir() + Constant.LOCAL_TRIP_ARCHIVE_DIRECTORY + Constant.company + "/");

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


    public static void archiveTripFile(Context context, String trip) {
        try {

            File[] files = returnTripFiles(context);

            File sourceFile = null;

            if (files != null) {

                for (File file : files) {

                    if (file.getName().substring(0, file.getName().length() - 5).equals(trip)) {

                        sourceFile = file;
                    }
                }
            }

            File destinationFile = new File(context.getFilesDir() + Constant.LOCAL_TRIP_ARCHIVE_DIRECTORY + Constant.company + "/" + trip + ".json");

            try (FileInputStream in = new FileInputStream(sourceFile);
                 FileOutputStream out = new FileOutputStream(destinationFile)) {

                byte[] buffer = new byte[1024];
                int length;
                while ((length = in.read(buffer)) > 0) {
                    out.write(buffer, 0, length);
                }

                sourceFile.delete();
            }

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

}

