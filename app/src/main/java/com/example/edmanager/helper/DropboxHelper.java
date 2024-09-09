package com.example.edmanager.helper;

import android.content.Context;
import android.util.Log;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.oauth.DbxCredential;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.WriteMode;
import com.example.edmanager.utility.Constant;
import com.example.edmanager.utility.ToastLogger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class DropboxHelper {

    private static String REFRESH_TOKEN = "z5wLDJpEALUAAAAAAAAAAaXxmMgnV5mEjNGWCcU81x0x7TmiG9kQLUU7IlGoTCay";
    private static String APP_KEY = "ma2o6e9amxvvuqk";
    private static String APP_SECRET = "q1t553htqu039lh";

    private static final String DROPBOX_CLIENT_PATH = "dropbox/";
    private static final String DROPBOX_CUSTOMER_PATH = "/Customers/" + Constant.company + "/";
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


    public static void uploadNewFile(Context context, File file) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    try (InputStream inputStream = new FileInputStream(new File(file.getPath()))) {

                        getClient().files().uploadBuilder(DROPBOX_CUSTOMER_PATH + file.getName()).withMode(WriteMode.OVERWRITE).uploadAndFinish(inputStream);

                        file.delete();

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

            result = getClient().files().listFolder(DROPBOX_CUSTOMER_PATH);

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


    public static void downloadFile(Context context, String tripName) {
        try {

            File file = new File(context.getFilesDir() + Constant.LOCAL_EXISTING_TRIP_DIRECTORY);

            if (!file.exists()) {

                file.mkdirs();
            }

            try (OutputStream outputStream = new FileOutputStream(new File(file.getPath(), tripName + ".json"))) {

                getClient().files().downloadBuilder(DROPBOX_CUSTOMER_PATH + tripName + ".json").download(outputStream);
            }

        } catch (Exception e) {

            e.printStackTrace();
        }
    }


    public static void fetchAllTrips(Context context) {
        try {

            ListFolderResult result = null;

            try {

                result = getClient().files().listFolder(DROPBOX_CUSTOMER_PATH);

            } catch (DbxException e) {

                e.printStackTrace();
            }

            for (int i = 0; i < result.getEntries().size(); i++) {

                String resultItem = result.getEntries().get(i).getName();

                if (resultItem.contains(".json")) {

                    downloadFile(context, resultItem.substring(0, resultItem.length() - 5));
                }
            }

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}