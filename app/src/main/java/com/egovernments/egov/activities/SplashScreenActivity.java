package com.egovernments.egov.activities;


import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.egovernments.egov.R;
import com.egovernments.egov.helper.ConfigManager;
import com.egovernments.egov.models.City;
import com.egovernments.egov.network.ApiController;
import com.egovernments.egov.network.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class SplashScreenActivity extends Activity {

    /**
     * Duration of wait
     **/
    private final int SPLASH_DISPLAY_LENGTH = 1000;

    private final int FINISH_TIMEOUT = 3000;

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

        sessionManager = new SessionManager(SplashScreenActivity.this);


        if (sessionManager.getUrlLocation() == null) {
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
                    JSONObject jsonObject = new JSONObject(response);
                    sessionManager.setBaseURL(jsonObject.get("url").toString(), jsonObject.get("city_name").toString(), 0);
                    timerThread.start();
                } else {
                    final List<City> cityList = ApiController.getAllCitiesURL(configManager.getString("api.multipleCitiesUrl"));

                    for (int i = 0; i < cityList.size(); i++) {
                        if (cityList.get(i).getCityCode() == (sessionManager.getUrlLocationCode())) {
                            url = cityList.get(i).getUrl();
                            location = cityList.get(i).getCityName();
                            code = cityList.get(i).getCityCode();
                        }
                    }
                    sessionManager.setBaseURL(url, location, code);
                    timerThread.start();
                }
            } catch (IOException e) {
                e.printStackTrace();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(SplashScreenActivity.this, "An unexpected error occurred while retrieving server info. The app will now close. Please ensure you are connected to the internet on next start.", Toast.LENGTH_LONG).show();
                    }
                });
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, FINISH_TIMEOUT);
            } catch (JSONException e) {
                e.printStackTrace();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(SplashScreenActivity.this, "An unexpected error occurred while retrieving server info. The app may be unavailable in your area. The app will now close.", Toast.LENGTH_LONG).show();
                    }
                });
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, FINISH_TIMEOUT);
            }

            return null;
        }
    }
}


