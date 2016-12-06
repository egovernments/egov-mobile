/*
 * ******************************************************************************
 *  eGov suite of products aim to improve the internal efficiency,transparency,
 *      accountability and the service delivery of the government  organizations.
 *
 *        Copyright (C) <2016>  eGovernments Foundation
 *
 *        The updated version of eGov suite of products as by eGovernments Foundation
 *        is available at http://www.egovernments.org
 *
 *        This program is free software: you can redistribute it and/or modify
 *        it under the terms of the GNU General Public License as published by
 *        the Free Software Foundation, either version 3 of the License, or
 *        any later version.
 *
 *        This program is distributed in the hope that it will be useful,
 *        but WITHOUT ANY WARRANTY; without even the implied warranty of
 *        MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *        GNU General Public License for more details.
 *
 *        You should have received a copy of the GNU General Public License
 *        along with this program. If not, see http://www.gnu.org/licenses/ or
 *        http://www.gnu.org/licenses/gpl.html .
 *
 *        In addition to the terms of the GPL license to be adhered to in using this
 *        program, the following additional terms are to be complied with:
 *
 *    	1) All versions of this program, verbatim or modified must carry this
 *    	   Legal Notice.
 *
 *    	2) Any misrepresentation of the origin of the material is prohibited. It
 *    	   is required that all modified versions of this material be marked in
 *    	   reasonable ways as different from the original version.
 *
 *    	3) This license does not grant any rights to any user of the program
 *    	   with regards to rights under trademark law for use of the trade names
 *    	   or trademarks of eGovernments Foundation.
 *
 *      In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 *  *****************************************************************************
 */

package org.egovernments.egoverp.services;


import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import com.google.gson.JsonObject;

import org.egovernments.egoverp.activities.LoginActivity;
import org.egovernments.egoverp.activities.ProfileActivity;
import org.egovernments.egoverp.api.ApiController;
import org.egovernments.egoverp.api.ApiUrl;
import org.egovernments.egoverp.config.SessionManager;
import org.egovernments.egoverp.helper.AppUtils;
import org.egovernments.egoverp.models.ProfileAPIResponse;

import retrofit2.Call;

/**
 * Service fetches data from server in background
 **/

public class UpdateService extends Service {

    public static final String KEY_METHOD = "method";

    public static final String UPDATE_PROFILE = "UPDATE_PROFILE";
    public static final String GET_GRIEVANCE_COUNT_INFO = "UPDATE_GRIEVANCE_COUNT_INFO";
    public static final String UPDATE_ALL = "UPDATE_ALL";

    public static final String BROADCAST_PROFILE_DETAILS = "BROADCAST_PROFILE_DETAILS";
    public static final String KEY_PROFILE = "BROADCAST_PROFILE_DETAILS";

    Handler handler;
    private SessionManager sessionManager;
    private int flag = 1;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        flag = 1;

        handler = new Handler();

        sessionManager = new SessionManager(getApplicationContext());

        if (intent != null) {
            String method = intent.getStringExtra(KEY_METHOD);
            switch (method) {

                case UPDATE_PROFILE:
                    updateProfile();
                    break;

                case UPDATE_ALL:
                    updateProfile();
                    break;
                /*case GET_GRIEVANCE_COUNT_INFO:
                    updateGrievanceCountInfo();
                    break;*/
            }
        }
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private void updateProfile() {

        if (sessionManager.getAccessToken() != null) {

            Call<ProfileAPIResponse> getProfileDetails = ApiController.getRetrofit2API(getApplicationContext())
                    .getProfile(sessionManager.getAccessToken());

            getProfileDetails.enqueue(new retrofit2.Callback<ProfileAPIResponse>() {
                @Override
                public void onResponse(Call<ProfileAPIResponse> call, retrofit2.Response<ProfileAPIResponse> response) {

                    Intent boradCastIntent = new Intent(BROADCAST_PROFILE_DETAILS);
                    if (response.isSuccessful()) {
                        ProfileAPIResponse profileAPIResponse = response.body();
                        ProfileActivity.profile = profileAPIResponse.getProfile();
                        boradCastIntent.putExtra(KEY_PROFILE, profileAPIResponse.getProfile());
                    } else {
                        ProfileActivity.isUpdateFailed = true;
                    }
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(boradCastIntent);

                }

                @Override
                public void onFailure(Call<ProfileAPIResponse> call, Throwable t) {
                    if (t != null) {
                        if (!(t.getLocalizedMessage() != null && !t.getLocalizedMessage().equals("Invalid access token")))
                            //Flag counter to prevent multiple executions of the below
                            if (flag == 1) {
                                sessionManager.invalidateAccessToken();
                                renewCredentials();
                            }

                    }
                }

            });

        }
    }

    /*private void updateGrievanceCountInfo() {
        Log.v("Access_TOKEN_SERVICE", "token ---- "+sessionManager.getAccessToken());
        if (sessionManager.getAccessToken() != null) {

            ApiController.getAPI(UpdateService.this).getComplaintCountDetails(sessionManager.getAccessToken(), new Callback<JsonObject>() {
                @Override
                public void success(JsonObject jsonObject, Response response) {
                    Intent intent = new Intent(HomeActivity.GRIEVANCE_INFO_BROADCAST);
                    intent.putExtra("success", true);
                    intent.putExtra("data", jsonObject.get("result").getAsJsonObject().toString());
                    LocalBroadcastManager.getInstance(UpdateService.this).sendBroadcast(intent);
                }

                @Override
                public void failure(RetrofitError error) {
                    Intent intent = new Intent(HomeActivity.GRIEVANCE_INFO_BROADCAST);
                    intent.putExtra("success", false);
                    LocalBroadcastManager.getInstance(UpdateService.this).sendBroadcast(intent);
                }
            });
        }
    }*/

    private void renewCredentials() {

        Call<JsonObject> login = ApiController.getRetrofit2API(getApplicationContext())
                .login(ApiUrl.AUTHORIZATION, sessionManager.getUsername(), "read write", sessionManager.getPassword(), "password");

        login.enqueue(new retrofit2.Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                JsonObject jsonObject = response.body();
                sessionManager.loginUser(sessionManager.getUsername(), sessionManager.getPassword(), AppUtils.getNullAsEmptyString(jsonObject.get("name")),
                        AppUtils.getNullAsEmptyString(jsonObject.get("mobileNumber")), AppUtils.getNullAsEmptyString(jsonObject.get("emailId")) , jsonObject.get("access_token").getAsString(), jsonObject.get("cityLat").getAsDouble(), jsonObject.get("cityLng").getAsDouble());
                updateProfile();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                sessionManager.invalidateAccessToken();
                startActivity(new Intent(UpdateService.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });

    }

}
