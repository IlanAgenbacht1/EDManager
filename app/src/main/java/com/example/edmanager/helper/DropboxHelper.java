package com.example.edmanager.helper;

import android.content.Context;
import android.util.Log;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.oauth.DbxCredential;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.ListFolderErrorException;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.WriteMode;
import com.example.edmanager.utility.Constant;
import com.example.edmanager.utility.ToastLogger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class DropboxHelper {

    private static String REFRESH_TOKEN = "z5wLDJpEALUAAAAAAAAAAaXxmMgnV5mEjNGWCcU81x0x7TmiG9kQLUU7IlGoTCay";
    private static String APP_KEY = "ma2o6e9amxvvuqk";
    private static String APP_SECRET = "q1t553htqu039lh";

    private static final String DROPBOX_CLIENT_PATH = "dropbox/";
    private static final String DROPBOX_CUSTOMER_PATH = "/Customers/";
    private static final String DROPBOX_IN_PROGRESS_PATH = "/InProgress/";
    private static final String DROPBOX_COMPLETED_PATH = "/Completed/";
    private static final String LOCAL_IMAGE_PATH = "/DeliveryApp/DeliveryImage/";
    private static final String LOCAL_SIGNATURE_PATH = "/DeliveryApp/DeliverySignature/";

    private static DbxClientV2 dropboxClient;


    private static DbxClientV2 getClient() {

        if (dropboxClient == null) {

            DbxRequestConfig config = DbxRequestConfig.newBuilder(DROPBOX_CLIENT_PATH).build();

            DbxCredential credential = new DbxCredential("", 0L, REFRESH_TOKEN, APP_KEY, APP_SECRET);

            dropboxClient = new DbxClientV2(config, credential);
        }

        return dropboxClient;
    }


    public static void uploadFile(Context context, File file) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    try (InputStream inputStream = new FileInputStream(new File(file.getPath()))) {

                        getClient().files().uploadBuilder(DROPBOX_CUSTOMER_PATH  + Constant.company + "/" + file.getName()).withMode(WriteMode.OVERWRITE).uploadAndFinish(inputStream);

                        ToastLogger.message(context, "Uploaded file " + file.getName());
                    }

                } catch(Exception e) {

                    e.printStackTrace();

                    ToastLogger.exception(context, e);
                }
            }
        });

        thread.start();
    }


    public static boolean fileExists(Context context, String trip) {

        ListFolderResult result = null;

        try {

            result = getClient().files().listFolder(DROPBOX_CUSTOMER_PATH  + Constant.company + "/");

        } catch (DbxException e) {

            e.printStackTrace();
        }

        for (int i = 0; i < result.getEntries().size(); i++) {

            String resultItem = result.getEntries().get(i).getName();

            resultItem = resultItem.substring(0, resultItem.length() - 5);

            Log.i("File", resultItem);

            if (resultItem.equals(trip)) {

                return true;
            }
        }

        return false;
    }


    public static void downloadFile(Context context, String tripName, String dropboxDirectory) {
        try {

            File file = new File(context.getFilesDir() + Constant.LOCAL_EXISTING_TRIP_DIRECTORY + Constant.company + "/");

            if (!file.exists()) {

                file.mkdirs();
            }

            try (OutputStream outputStream = new FileOutputStream(new File(file.getPath(), tripName + ".json"))) {

                if (dropboxDirectory == null) {

                    getClient().files().downloadBuilder(DROPBOX_CUSTOMER_PATH + Constant.company + "/" + tripName + ".json").download(outputStream);

                } else {

                    getClient().files().downloadBuilder(dropboxDirectory + tripName + ".json").download(outputStream);
                }
            }

        } catch (Exception e) {

            e.printStackTrace();
        }
    }


    public static void fetchAllTrips(Context context) {
        try {

            ListFolderResult result = getClient().files().listFolder(DROPBOX_CUSTOMER_PATH  + Constant.company + "/");

            for (int i = 0; i < result.getEntries().size(); i++) {

                String resultItem = result.getEntries().get(i).getName();

                if (resultItem.contains(".json")) {

                    downloadFile(context, resultItem.substring(0, resultItem.length() - 5), null);
                }
            }

            result = getClient().files().listFolder(DROPBOX_CUSTOMER_PATH + Constant.company + DROPBOX_IN_PROGRESS_PATH);

            for (int i = 0; i < result.getEntries().size(); i++) {

                String resultItem = result.getEntries().get(i).getName();

                if (resultItem.contains(".json")) {

                    downloadFile(context, resultItem.substring(0, resultItem.length() - 5), DROPBOX_CUSTOMER_PATH + Constant.company + DROPBOX_IN_PROGRESS_PATH);
                }
            }

            result = getClient().files().listFolder(DROPBOX_CUSTOMER_PATH + Constant.company + DROPBOX_COMPLETED_PATH);

            for (int i = 0; i < result.getEntries().size(); i++) {

                String resultItem = result.getEntries().get(i).getName();

                ListFolderResult completedResult = getClient().files().listFolder(DROPBOX_CUSTOMER_PATH + Constant.company + DROPBOX_COMPLETED_PATH + resultItem + "/");

                for (int j = 0; j < completedResult.getEntries().size(); j++) {

                    resultItem = completedResult.getEntries().get(j).getName();

                    if (resultItem.contains(".json")) {

                        downloadFile(context, resultItem.substring(0, resultItem.length() - 5), DROPBOX_CUSTOMER_PATH + Constant.company + DROPBOX_COMPLETED_PATH + resultItem.substring(0, resultItem.length() - 5) + "/");
                    }
                }
            }

        } catch (Exception e) {

            e.printStackTrace();
        }
    }


    public static int returnTripStatus(String trip) {
        try {

            ListFolderResult result = getClient().files().listFolder(DROPBOX_CUSTOMER_PATH + Constant.company + DROPBOX_IN_PROGRESS_PATH);

            for (int i = 0; i < result.getEntries().size(); i++) {

                String item = result.getEntries().get(i).getName();

                item = item.substring(0, item.length() - 5);

                if (item.equals(trip)) {

                    //Log.i("SYNC", trip + " in progress");

                    return 0;
                }
            }

            result = getClient().files().listFolder(DROPBOX_CUSTOMER_PATH + Constant.company + DROPBOX_COMPLETED_PATH + trip + "/");

            for (int i = 0; i < result.getEntries().size(); i++) {

                String item = result.getEntries().get(i).getName();

                if (item.contains(".json")) {

                    item = item.substring(0, item.length() - 5);

                    if (item.equals(trip)) {

                        //Log.i("SYNC",  trip + " completed");

                        return 1;
                    }
                }
            }

        } catch (ListFolderErrorException e) {

            Log.i("SYNC", trip + " not completed");

            return 2;

        } catch (Exception e) {

            e.printStackTrace();
        }

        return 2;
    }


    public static void deleteFile(String trip) {
        try {

            getClient().files().deleteV2(DROPBOX_CUSTOMER_PATH + Constant.company + "/" + trip + ".json");

            Log.i("SYNC", trip + " deleted.");

        } catch (Exception e) {


        }
    }

}