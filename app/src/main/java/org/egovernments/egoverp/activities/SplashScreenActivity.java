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

package org.egovernments.egoverp.activities;


import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.widget.Toast;

import org.egovernments.egoverp.R;
import org.egovernments.egoverp.helper.ConfigManager;
import org.egovernments.egoverp.models.City;
import org.egovernments.egoverp.network.ApiController;
import org.egovernments.egoverp.network.SessionManager;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public class SplashScreenActivity extends Activity {

    /**
     * Duration of wait
     **/
    private final int SPLASH_DISPLAY_LENGTH = 1000;

    private ConfigManager configManager;

    private Thread timerThread;

    private Handler handler;

    private SessionManager sessionManager;

    private String url;
    private String location;

    private int code;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_splashscreen);

        handler = new Handler();

        timerThread = new Thread() {
            public void run() {
                try {
                    sleep(SPLASH_DISPLAY_LENGTH);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };

        try {
            InputStream inputStream = getAssets().open("egov.conf");
            configManager = new ConfigManager(inputStream, SplashScreenActivity.this);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        sessionManager = new SessionManager(getApplicationContext());

        if (sessionManager.getUrlLocation() == null && configManager.getString("api.multicities").equals("true")) {
            timerThread.start();
        } else {
            if (sessionManager.getUrlAge() > Integer.valueOf(configManager.getString("app.timeoutdays"))) {
                new GetCityTask().execute();
            } else {
                timerThread.start();
            }
        }
    }

    class GetCityTask extends AsyncTask<String, Integer, Object> {

        @Override
        protected Object doInBackground(String... params) {

            try {

                if (configManager.getString("api.multicities").equals("false")) {
                    String response = ApiController.getCityURL(configManager.getString("api.cityUrl"));
                    if (response != null) {
                        JSONObject jsonObject = new JSONObject(response);
                        sessionManager.setBaseURL(jsonObject.get("url").toString(), jsonObject.get("city_name").toString(), 0);
                        timerThread.start();
                    } else {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast toast = Toast.makeText(SplashScreenActivity.this, "An unexpected error occurred while retrieving server info. Please ensure you are connected to the internet.", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                                toast.show();
                            }
                        });
                        sessionManager.logoutUser();
                    }
                } else {
                    City activeCity=ApiController.getCityURL(configManager.getString("api.multipleCitiesUrl"), sessionManager.getUrlLocationCode());
                    if (activeCity != null) {
                        url = activeCity.getUrl();
                        location = activeCity.getCityName();
                        code = activeCity.getCityCode();
                        sessionManager.setBaseURL(url, location, code);
                        timerThread.start();
                    } else {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast toast = Toast.makeText(SplashScreenActivity.this, "An unexpected error occurred while retrieving server info. Please ensure you are connected to the internet.", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                                toast.show();
                            }
                        });
                        sessionManager.logoutUser();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast toast = Toast.makeText(SplashScreenActivity.this, "An unexpected error occurred while retrieving server info. Please ensure you are connected to the internet.", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                        toast.show();
                    }
                });
                sessionManager.logoutUser();
            } catch (JSONException e) {
                e.printStackTrace();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast toast = Toast.makeText(SplashScreenActivity.this, "An unexpected error occurred while retrieving server info. The app may be unavailable in your area", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                        toast.show();
                    }
                });
                sessionManager.logoutUser();
            }
            return null;
        }
    }
}


