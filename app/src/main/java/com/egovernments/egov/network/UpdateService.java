package com.egovernments.egov.network;


import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;

import com.egovernments.egov.activities.GrievanceActivity;
import com.egovernments.egov.activities.LoginActivity;
import com.egovernments.egov.activities.NotificationsActivity;
import com.egovernments.egov.activities.ProfileActivity;
import com.egovernments.egov.events.GrievancesUpdatedEvent;
import com.egovernments.egov.events.ProfileUpdatedEvent;
import com.egovernments.egov.models.GrievanceAPIResponse;
import com.egovernments.egov.models.ProfileAPIResponse;
import com.google.gson.JsonObject;

import de.greenrobot.event.EventBus;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class UpdateService extends Service {

    public static final String KEY_METHOD = "method";

    public static final String UPDATE_PROFILE = "UPDATE_PROFILE";
    public static final String UPDATE_COMPLAINTS = "UPDATE_COMPLAINTS";
    public static final String UPDATE_ALL = "UPDATE_ALL";

    public static final String COMPLAINTS_PAGE = "UPDATE_ALL";

    private SessionManager sessionManager;

    private int flag = 1;

    private Handler handler;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        flag = 1;

        handler = new Handler();

        sessionManager = new SessionManager(getApplicationContext());

        if (intent != null) {
            String method = intent.getStringExtra(KEY_METHOD);
            switch (method) {

                case UPDATE_COMPLAINTS:
                    updateComplaints(intent.getStringExtra(COMPLAINTS_PAGE));
                    break;

                case UPDATE_PROFILE:
                    updateProfile();
                    break;

                case UPDATE_ALL:
                    updateProfile();
                    updateComplaints("1");
                    break;
            }
            NotificationsActivity.createList();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void updateComplaints(final String page) {

        if (sessionManager.getAccessToken() != null) {
            ApiController.getAPI().getMyComplaints(page, "10", sessionManager.getAccessToken(), new Callback<GrievanceAPIResponse>() {
                        @Override
                        public void success(GrievanceAPIResponse grievanceAPIResponse, Response response) {

                            if (page.equals("1")) {
                                GrievanceActivity.pageLoaded = 0;
                                GrievanceActivity.grievanceList = grievanceAPIResponse.getResult();
                                GrievanceActivity.grievanceList.add(null);
                                EventBus.getDefault().post(new GrievancesUpdatedEvent());
                            } else {
                                GrievanceActivity.grievanceList.addAll(GrievanceActivity.grievanceList.size() - 1, grievanceAPIResponse.getResult());
                                if (grievanceAPIResponse.getStatus().getHasNextPage().equals("false")) {

                                    GrievanceActivity.grievanceList.remove(GrievanceActivity.grievanceList.size() - 1);
                                }
                                EventBus.getDefault().post(new GrievancesUpdatedEvent());

                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            JsonObject jsonObject = null;
                            if (error != null) {
                                try {
                                    jsonObject = (JsonObject) error.getBodyAs(JsonObject.class);
                                } catch (Exception e) {
                                    handler.post(new ToastRunnable("Update failed. Server is down for maintenance or over capacity"));
                                }
                                if (jsonObject != null) {
                                    String errorDescription = jsonObject.get("error_description").toString().trim();
                                    if (errorDescription.contains("Invalid access token")) {

                                        if (flag == 1) {
                                            sessionManager.invalidateAccessToken();
                                            renewCredentials();
                                        }
                                    }
                                }
                            }
                        }
                    }

            );
        }

    }

    private void updateProfile() {
        if (sessionManager.getAccessToken() != null) {
            ApiController.getAPI().getProfile(sessionManager.getAccessToken(), new Callback<ProfileAPIResponse>() {
                @Override
                public void success(ProfileAPIResponse profileAPIResponse, Response response) {

                    ProfileActivity.profile = profileAPIResponse.getProfile();
                    EventBus.getDefault().post(new ProfileUpdatedEvent());

                }

                @Override
                public void failure(RetrofitError error) {
                    JsonObject jsonObject = null;
                    if (error != null) {
                        try {
                            jsonObject = (JsonObject) error.getBodyAs(JsonObject.class);
                        } catch (Exception e) {
                            handler.post(new ToastRunnable("Update failed. Server is down for maintenance or over capacity"));
                        }
                        if (jsonObject != null) {
                            String errorDescription = jsonObject.get("error_description").toString().trim();
                            if (errorDescription.contains("Invalid access token")) {

                                if (flag == 1) {
                                    sessionManager.invalidateAccessToken();
                                    renewCredentials();
                                }
                            }
                        }
                    }

                }
            });
        }
    }

    private void renewCredentials() {

        ApiController.getLoginAPI().Login(sessionManager.getUsername(), "read write", sessionManager.getPassword(), "password", new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {
                sessionManager.loginUser(sessionManager.getPassword(), sessionManager.getUsername(), jsonObject.get("access_token").toString());
                updateComplaints("1");
                updateProfile();
            }

            @Override
            public void failure(RetrofitError error) {
                sessionManager.invalidateAccessToken();
                startActivity(new Intent(UpdateService.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });
    }

    private class ToastRunnable implements Runnable {
        String mText;

        public ToastRunnable(String text) {
            mText = text;
        }

        @Override
        public void run() {
            Toast toast = Toast.makeText(UpdateService.this.getApplicationContext(), mText, Toast.LENGTH_SHORT);
            View view = toast.getView();
            view.setBackgroundResource(android.R.drawable.toast_frame);
            toast.show();
        }
    }
}
