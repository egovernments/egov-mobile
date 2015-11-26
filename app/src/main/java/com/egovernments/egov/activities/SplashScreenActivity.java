package com.egovernments.egov.activities;


import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import com.egovernments.egov.R;
import com.egovernments.egov.helper.ConfigManager;
import com.egovernments.egov.network.ApiController;
import com.egovernments.egov.network.SessionManager;

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

    private SessionManager sessionManager;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_splashscreen);

        timerThread = new Thread() {
            public void run() {
                try {
                    sleep(SPLASH_DISPLAY_LENGTH);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };

        sessionManager = new SessionManager(this);

        try {
            InputStream inputStream = getAssets().open("egov.conf");
            configManager = new ConfigManager(inputStream, SplashScreenActivity.this);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        int x = sessionManager.getUrlAge();
        int y = Integer.valueOf(configManager.getString("app.timeoutdays"));

        if (x > y) {

            new GetCityTask().execute();

        } else {
            timerThread.start();
        }
    }

    class GetCityTask extends AsyncTask<String, Integer, Object> {

        @Override
        protected Object doInBackground(String... params) {

            try {
                String response = ApiController.getCityURL(configManager.getString("api.cityUrl"));
                JSONObject jsonObject = new JSONObject(response);
                sessionManager.setBaseURL(jsonObject.get("url").toString());
                timerThread.start();
            } catch (IOException e) {
                Toast.makeText(SplashScreenActivity.this, "An unexpected error occurred while retrieving server info. The app will now close. Please ensure you are connected to the internet on next start.", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            } catch (JSONException e) {
                Toast.makeText(SplashScreenActivity.this, "The application may not be available in your area", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }

            return null;
        }
    }
}


