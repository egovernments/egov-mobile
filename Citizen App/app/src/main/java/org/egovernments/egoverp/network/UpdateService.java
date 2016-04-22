/*
 *    eGov suite of products aim to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (c) 2016  eGovernments Foundation
 *
 *     The updated version of eGov suite of products as by eGovernments Foundation
 *     is available at http://www.egovernments.org
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     any later version.
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see http://www.gnu.org/licenses/ or
 *     http://www.gnu.org/licenses/gpl.html .
 *     In addition to the terms of the GPL license to be adhered to in using this
 *     program, the following additional terms are to be complied with:
 *         1) All versions of this program, verbatim or modified must carry this
 *            Legal Notice.
 *         2) Any misrepresentation of the origin of the material is prohibited. It
 *            is required that all modified versions of this material be marked in
 *            reasonable ways as different from the original version.
 *         3) This license does not grant any rights to any user of the program
 *            with regards to rights under trademark law for use of the trade names
 *            or trademarks of eGovernments Foundation.
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */

package org.egovernments.egoverp.network;


import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.egovernments.egoverp.activities.LoginActivity;
import org.egovernments.egoverp.activities.ProfileActivity;
import org.egovernments.egoverp.events.ProfileUpdatedEvent;
import org.egovernments.egoverp.models.ProfileAPIResponse;
import org.egovernments.egoverp.models.ProfileUpdateFailedEvent;

import de.greenrobot.event.EventBus;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Service fetches data from server in background
 **/

public class UpdateService extends Service {

    public static final String KEY_METHOD = "method";

    public static final String UPDATE_PROFILE = "UPDATE_PROFILE";
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


                case UPDATE_PROFILE:
                    updateProfile();
                    break;

                case UPDATE_ALL:
                    updateProfile();
                    break;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public String returnValidString(String string)
    {
        if(!TextUtils.isEmpty(string)){
            return string;
        }
        return "";
    }

    private void updateProfile() {

        if (sessionManager.getAccessToken() != null) {
            ApiController.getAPI(UpdateService.this).getProfile(sessionManager.getAccessToken(), new Callback<ProfileAPIResponse>() {
                @Override
                public void success(ProfileAPIResponse profileAPIResponse, Response response) {

                    ProfileActivity.profile = profileAPIResponse.getProfile();
                    EventBus.getDefault().post(new ProfileUpdatedEvent());

                }

                @Override
                public void failure(RetrofitError error) {
                    if (error != null) {
                        if (error.getLocalizedMessage() != null && !error.getLocalizedMessage().equals("Invalid access token"))
                            handler.post(new ToastRunnable("Failed to fetch profile. " + error.getLocalizedMessage()));
                        else {
                            //Flag counter to prevent multiple executions of the below
                            if (flag == 1) {
                                sessionManager.invalidateAccessToken();
                                renewCredentials();
                            }

                        }
                        ProfileActivity.isUpdateFailed = true;
                        EventBus.getDefault().post(new ProfileUpdateFailedEvent());
                    }

                }

            });
        }
    }

    private void renewCredentials() {

        ApiController.getAPI(UpdateService.this).login(ApiUrl.AUTHORIZATION, sessionManager.getUsername(), "read write", sessionManager.getPassword(), "password", new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {
                sessionManager.loginUser(sessionManager.getPassword(), sessionManager.getUsername(), jsonObject.get("access_token").toString(), jsonObject.get("cityLat").getAsDouble(), jsonObject.get("cityLng").getAsDouble());
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
            toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();
        }
    }
}
