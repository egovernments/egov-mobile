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
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.Toast;

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

                            long lastRegisteredUserTime=sessionManager.getLastRegisteredUserTime();

                            long lastForgotPwdOTPTime=sessionManager.getForgotPasswordTime();

                            long expiryOTPTime=lastForgotPwdOTPTime+(5*60*1000); //Find OTP Expiry Time (+5 mins)

                            long timeOfExpiryRegisteredUser=lastRegisteredUserTime+(48*60*60*1000);

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
                            else if((timeOfExpiryRegisteredUser-System.currentTimeMillis())>0 && TextUtils.isEmpty(sessionManager.getAccessToken()))
                            {
                                Intent accountActivationIntent=new Intent(getApplicationContext(), AccountActivationActivity.class);
                                accountActivationIntent.putExtra(AccountActivationActivity.PARAM_USERNAME, sessionManager.getLastRegisteredUserName());
                                accountActivationIntent.putExtra(AccountActivationActivity.PARAM_PASSWORD, sessionManager.getLastRegisteredUserPassword());
                                if(getIntent().getBooleanExtra(SMSListener.PARAM_LAUCNH_FROM_SMS, false))
                                {
                                    accountActivationIntent.putExtra(SMSListener.PARAM_OTP_CODE, getIntent().getStringExtra(SMSListener.PARAM_OTP_CODE));
                                }
                                accountActivationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(accountActivationIntent);

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
        if (Build.VERSION.SDK_INT < 23) {
            timerThread.start();
        } else {
            if(checkReadSMSPermision()) {
                timerThread.start();
            }
        }

    }


}


