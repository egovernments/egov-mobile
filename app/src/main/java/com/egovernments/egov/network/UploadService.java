package com.egovernments.egov.network;


import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.Nullable;

import com.google.gson.JsonObject;

import java.io.File;
import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

public class UploadService extends Service {

    public static final String URI_LIST = "Uri list";

    public static final String COMPLAINT_NO = "Complaint No";

    private SessionManager sessionManager;

    private int i;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        sessionManager = new SessionManager(getApplicationContext());

        ArrayList<Uri> uriArrayList = intent.getParcelableArrayListExtra(URI_LIST);

        String complaintNo = intent.getStringExtra(COMPLAINT_NO);

        i = 1;
        for (Uri uri : uriArrayList) {

            uploadImage(uri, complaintNo);

        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private String getMimeType(Uri uri) {
        String mimeType;
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            ContentResolver contentResolver = getApplicationContext().getContentResolver();
            mimeType = contentResolver.getType(uri);
            return mimeType;
        }
        return "image/jpeg";
    }

    private String getRealPathFromURI(Uri contentUri) {
        try {
            String[] strings = {MediaStore.Images.Media.DATA};
            String s = null;

            Cursor cursor = getContentResolver().query(contentUri, strings, null, null, null);
            int column_index;
            if (cursor != null) {
                column_index = cursor
                        .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                s = cursor.getString(column_index);
                cursor.close();
            }
            if (s != null) {
                return s;
            }
        } catch (Exception e) {
            return contentUri.getPath();
        }

        return null;

    }


    private void uploadImage(final Uri uri, final String complaintNo) {

        String mimeType = getMimeType(uri);

        File imgFile = new File(uri.getPath());

        if (!imgFile.exists()) {
            imgFile = new File(getRealPathFromURI(uri));
        }

        TypedFile typedFile = new TypedFile(mimeType, imgFile);
        ApiController.getAPI().uploadImage(typedFile, complaintNo, sessionManager.getAccessToken(), String.valueOf(i), new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {

                i++;

            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }
}
