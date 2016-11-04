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

package org.egovernments.egoverp.activities;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.egovernments.egoverp.R;
import org.egovernments.egoverp.helper.AppUtils;
import org.egovernments.egoverp.helper.ConfigManager;
import org.egovernments.egoverp.listeners.SMSListener;
import org.egovernments.egoverp.models.City;
import org.egovernments.egoverp.network.ApiController;
import org.egovernments.egoverp.network.SessionManager;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class SplashScreenActivity extends Activity {

    private static final int REQUEST_CODE_ASK_PERMISSION_READ_SMS = 115;
    /**
     * Duration of wait
     **/
    private final int SPLASH_DISPLAY_LENGTH = 2000;

    private ConfigManager configManager;

    private Thread timerThread;

    private Handler handler;

    private SessionManager sessionManager;

    private final String PLAYSTORE_URL="https://play.google.com/store/apps/details?id=";
    private final String MARKET_URL="market://details?id=";

    String url;
    String location;

    int code;

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

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            long lastForgotPwdOTPTime=sessionManager.getForgotPasswordTime();

                            long expiryOTPTime=lastForgotPwdOTPTime+(5*60*1000); //Find OTP Expiry Time (+5 mins)

                            if((expiryOTPTime-System.currentTimeMillis()) > 0 && !TextUtils.isEmpty(sessionManager.getResetPasswordLastMobileNo()))
                            {
                                Intent resetPasswordActivity=new Intent(getApplicationContext(), ResetPasswordActivity.class);
                                resetPasswordActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                resetPasswordActivity.putExtra(ResetPasswordActivity.MESSAGE_SENT_TO, sessionManager.getResetPasswordLastMobileNo());

                                if(getIntent().getBooleanExtra(SMSListener.PARAM_LAUCNH_FROM_SMS, false))
                                {
                                    resetPasswordActivity.putExtra(SMSListener.PARAM_OTP_CODE, getIntent().getStringExtra(SMSListener.PARAM_OTP_CODE));
                                }

                                startActivity(resetPasswordActivity);
                                finish();
                                return;
                            }

                            startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
                            finish();

                        }
                    });
                }
            }
        };

        try {
            configManager = AppUtils.getConfigManager(getApplicationContext());
        } catch (IOException e) {
            e.printStackTrace();
        }

        sessionManager = new SessionManager(getApplicationContext());
        sessionManager.setKeyDebugLog(Boolean.valueOf(configManager.getString("app.debug.log")));

        if (sessionManager.getUrlLocation() == null && configManager.getString("api.multicities").equals("true")) {
            startTimerThread();
        } else {
            if (sessionManager.getUrlAge() > Integer.valueOf(configManager.getString("app.timeoutdays")) || TextUtils.isEmpty(sessionManager.getBaseURL())) {
                new GetCityTask().execute();
            } else {
                startTimerThread();
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
                        sessionManager.setBaseURL(jsonObject.get("url").toString(), jsonObject.get("city_name").toString(), jsonObject.getInt("city_code"));
                        startTimerThread();
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
                        startTimerThread();
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


    class GetLatestAppVersion extends AsyncTask<String, Integer, JsonObject>{

        final String KEY_RESULT="result";
        final String KEY_APP_VERSION_CODE="versionCode";
        final String KEY_APP_IS_FORCE_UPDATE="isForceUpdate";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JsonObject doInBackground(String... params) {
            JsonObject response = null;
            try {
                String resp = ApiController.getCityURL(configManager.getString("api.appVersionCheck")+getApplicationContext().getPackageName());
                response=new JsonParser().parse(resp).getAsJsonObject();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(JsonObject response) {
            super.onPostExecute(response);

            if(response!=null)
            {
                JsonObject appDetails=response.get(KEY_RESULT).getAsJsonObject();

                if(AppUtils.getAppVersionCode(getApplicationContext()) < appDetails.get(KEY_APP_VERSION_CODE).getAsNumber().intValue()){
                    if(appDetails.get(KEY_APP_IS_FORCE_UPDATE).getAsBoolean())
                    {
                        showForceUpdateAlert();
                    }
                    else{
                        showRecommendedUpdateAlert();
                    }
                }
                else{
                    launchScreen();
                }
            }
            else{
                launchScreen();
            }

        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean checkReadSMSPermision() {
        int hasWriteContactsPermission = checkSelfPermission(Manifest.permission.READ_SMS);
        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_SMS},
                    REQUEST_CODE_ASK_PERMISSION_READ_SMS);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSION_READ_SMS:
                if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(getApplicationContext(), R.string.permission_readsms_denied, Toast.LENGTH_LONG).show();
                    timerThread.start();
                }
                else if(grantResults[0]== PackageManager.PERMISSION_GRANTED)
                {
                    timerThread.start();
                }
                break;
        }
    }

    public void startTimerThread()
    {
        new GetLatestAppVersion().execute();
    }


    public void launchScreen(){
        if (Build.VERSION.SDK_INT < 23) {
            timerThread.start();
        } else {
            if(checkReadSMSPermision()) {
                timerThread.start();
            }
        }
    }

    void showForceUpdateAlert(){

        AlertDialog.Builder builder = new AlertDialog.Builder(SplashScreenActivity.this);
        builder.setCancelable(false);
        builder.setTitle("New update is available");
        builder.setMessage("Please download the latest app to use our upgraded services");
        builder.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(MARKET_URL + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(PLAYSTORE_URL + appPackageName)));
                }
                finish();
            }
        });
        builder.create().show();
    }


    void showRecommendedUpdateAlert(){

        AlertDialog.Builder builder = new AlertDialog.Builder(SplashScreenActivity.this);
        builder.setCancelable(false);
        builder.setTitle("New update is available");
        builder.setMessage("We're recommended to download the latest app to use our upgraded services");
        builder.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(MARKET_URL + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(PLAYSTORE_URL + appPackageName)));
                }
                finish();
            }
        });
        builder.setNegativeButton("NOT NOW", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                launchScreen();
            }
        });
        builder.create().show();
    }

}


