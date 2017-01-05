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
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.egovernments.egoverp.R;
import org.egovernments.egoverp.api.ApiController;
import org.egovernments.egoverp.config.Config;
import org.egovernments.egoverp.config.SessionManager;
import org.egovernments.egoverp.helper.AppUtils;
import org.egovernments.egoverp.helper.ConfigManager;
import org.egovernments.egoverp.listeners.SMSListener;
import org.egovernments.egoverp.models.City;

import java.io.IOException;

import okhttp3.HttpUrl;

import static org.egovernments.egoverp.config.Config.API_MULTICITIES;

public class SplashScreenActivity extends Activity {

    private static final int REQUEST_CODE_ASK_PERMISSION_READ_SMS = 115;
    /**
     * Duration of wait
     **/
    private final int SPLASH_DISPLAY_LENGTH = 2000;
    private final String PLAYSTORE_URL = "https://play.google.com/store/apps/details?id=";
    private final String MARKET_URL = "market://details?id=";
    private ConfigManager configManager;
    private Thread timerThread;
    private Handler handler;
    private SessionManager sessionManager;
    private AlertDialog errorAlertDialog;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_splashscreen);

        handler = new Handler();

        timerThread = new Thread() {
            public void run() {
                try {
                    sleep(SPLASH_DISPLAY_LENGTH);

                    if (SplashScreenActivity.this.isFinishing()) {
                        return;
                    }

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

                        }
                    });

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    finish();
                }
            }
        };

        try {
            configManager = AppUtils.getConfigManager(getApplicationContext());
        } catch (IOException e) {
            e.printStackTrace();
        }

        sessionManager = new SessionManager(getApplicationContext());
        sessionManager.setKeyDebugLog(Boolean.valueOf(configManager.getString(Config.APP_DEBUG_LOG)));

        startInitialConditionCheck();

    }

    void startInitialConditionCheck()
    {
        if(AppUtils.checkInternetConnectivity(getApplicationContext()))
        {
            if (sessionManager.getUrlLocation() == null && configManager.getString(API_MULTICITIES).equals("true")) {
                startTimerThread();
            } else {
                if (sessionManager.getUrlAge() > Integer.valueOf(configManager.getString(Config.APP_TIMEOUTDAYS))
                        || TextUtils.isEmpty(sessionManager.getBaseURL())
                        || !sessionManager.getAppVersionCode().equals(AppUtils.getAppVersionCode(SplashScreenActivity.this))) {
                    new GetCityTask().execute();
                } else {
                    startTimerThread();
                }
            }
        }
        else{
            showErrorAlert(getString(R.string.no_connection), getString(R.string.check_internet),
                    getString(R.string.try_again), R.drawable.ic_network_wifi_black_48dp,
                    ContextCompat.getColor(SplashScreenActivity.this, R.color.red));
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
                    startTimerThread();
                } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startTimerThread();
                }
                break;
        }
    }

    public void startTimerThread() {
        new GetLatestAppVersion().execute();
    }

    public void launchScreen() {

        if (Build.VERSION.SDK_INT < 23) {
            timerThread.start();
        } else {
            if (checkReadSMSPermision()) {
                timerThread.start();
            }
        }

    }

    void showUpdateAlert(boolean isForceUpdate, String title, String content) {

        if (!this.isFinishing()) {

            AlertDialog.Builder builder = new AlertDialog.Builder(SplashScreenActivity.this);
            builder.setCancelable(false);
            builder.setTitle(title);
            builder.setMessage(content);
            builder.setPositiveButton(R.string.alert_button_update_text, new DialogInterface.OnClickListener() {
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

            if (!isForceUpdate) {
                builder.setNegativeButton(R.string.alert_button_notnow_text, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        launchScreen();
                    }
                });
            }
            builder.create().show();
        }
    }

    @SuppressWarnings("all")
    void showErrorAlert(String errorTitle, String errorDesc, String btnText, int imageResourceId, int imgColor) {

        if (!this.isFinishing()) {

            final AlertDialog.Builder builder = new AlertDialog.Builder(SplashScreenActivity.this);
            LayoutInflater inflater = this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_imagery_notification, null);
            ((TextView) dialogView.findViewById(R.id.tvTitle)).setText(errorTitle);
            ((TextView) dialogView.findViewById(R.id.tvContent)).setText(errorDesc);
            ImageView imageView = (ImageView) dialogView.findViewById(R.id.imgAlert);
            imageView.setImageResource(imageResourceId);
            imageView.setColorFilter(imgColor, PorterDuff.Mode.SRC_ATOP);

            Button btnAction = (Button) dialogView.findViewById(R.id.btnAction);
            btnAction.setText(btnText);
            btnAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    errorAlertDialog.dismiss();
                    startInitialConditionCheck();
                }
            });

            builder.setView(dialogView);
            builder.setCancelable(false);
            errorAlertDialog = builder.create();
            errorAlertDialog.show();
        }

    }

    class GetCityTask extends AsyncTask<String, Integer, Object> {

        @Override
        protected Object doInBackground(String... params) {

            try {

                HttpUrl.Builder urlBuilder = HttpUrl.parse(configManager.getString(Config.API_CITY_URL)).newBuilder();

                if (configManager.getString(API_MULTICITIES).equals("false")) {
                    City city = new Gson().
                            fromJson(ApiController.getResponseFromUrl(null, urlBuilder.build()), City.class);

                    if (city != null) {
                        sessionManager.setBaseURL(city.getUrl(), city.getCityName(),
                                city.getCityCode(), city.getModules()!=null?city.getModules().toString():null);
                        startTimerThread();
                    } else {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast toast = Toast.makeText(SplashScreenActivity.this, R.string.unexcepted_error, Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                                toast.show();
                            }
                        });
                        sessionManager.logoutUser();
                    }
                } else {

                    urlBuilder = HttpUrl.parse(configManager.getString(Config.API_MULTIPLE_CITIES_URL)).newBuilder();
                    HttpUrl url = urlBuilder
                            .addQueryParameter("code", String.valueOf(sessionManager.getUrlLocationCode())).build();
                    City activeCity = new Gson().
                            fromJson(ApiController.getResponseFromUrl(null, url), City.class);

                    if (activeCity != null) {
                        sessionManager.setBaseURL(activeCity.getUrl(), activeCity.getCityName(),
                                activeCity.getCityCode(), activeCity.getModules()!=null?activeCity.getModules().toString():null);
                        startTimerThread();
                    } else {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast toast = Toast.makeText(SplashScreenActivity.this, R.string.unexcepted_error, Toast.LENGTH_SHORT);
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
                        Toast toast = Toast.makeText(SplashScreenActivity.this, R.string.unexcepted_error, Toast.LENGTH_SHORT);
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
                HttpUrl.Builder urlBuilder = HttpUrl.parse(configManager.getString(Config.API_APP_VERSION_CHECK) + getApplicationContext().getPackageName()).newBuilder();
                String resp = ApiController.getResponseFromUrl(null, urlBuilder.build());
                response=new JsonParser().parse(resp).getAsJsonObject();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(JsonObject response) {
            super.onPostExecute(response);

            try
            {
                if (response != null && !TextUtils.isEmpty(response.get(KEY_RESULT).getAsString())) {
                    JsonObject appDetails = response.get(KEY_RESULT).getAsJsonObject();

                    if (AppUtils.getAppVersionCode(getApplicationContext()) < appDetails.get(KEY_APP_VERSION_CODE).getAsNumber().intValue()) {
                        if (appDetails.get(KEY_APP_IS_FORCE_UPDATE).getAsBoolean()) {
                            showUpdateAlert(true, getString(R.string.update_available_alert_title),
                                    getString(R.string.update_force_alert_content));
                        } else {
                            showUpdateAlert(false, getString(R.string.update_available_alert_title),
                                    getString(R.string.alert_recommended_update_content));
                        }
                    }
                    else{
                        launchScreen();
                    }
                }
                else{
                    launchScreen();
                }
            } catch (Exception ex) {
                launchScreen();
            }


        }
    }

}

