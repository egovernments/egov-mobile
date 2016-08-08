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

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.egovernments.egoverp.R;
import org.egovernments.egoverp.helper.AppUtils;
import org.egovernments.egoverp.helper.ConfigManager;
import org.egovernments.egoverp.helper.PasswordLevel;
import org.egovernments.egoverp.listeners.SMSListener;
import org.egovernments.egoverp.network.ApiController;
import org.egovernments.egoverp.network.SessionManager;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ResetPasswordActivity extends AppCompatActivity {

    public static String MESSAGE_SENT_TO="messageSentTo";
    public static String OTP_BROADCAST_LISTENER="OTP_Broadcast_Listener";

    EditText etOtp, etNewPwd, etConfirmPwd;
    String mobileNo;
    private ConfigManager configManager;
    SessionManager sessionManager;
    FloatingActionButton fab;
    ProgressBar progressBar;
    Button btnResendOTP;
    TextView tvCountDown;
    CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
        }

        try {
            configManager = AppUtils.getConfigManager(getApplicationContext());
        } catch (IOException e) {
            e.printStackTrace();
        }

        TextView tvResetPws=(TextView)findViewById(R.id.tvResetPwd);
        tvCountDown=(TextView)findViewById(R.id.tvCountDown);

        String recoveryMessage = "OTP has been sent to your registered ";
        mobileNo=getIntent().getStringExtra(MESSAGE_SENT_TO);

        progressBar=(ProgressBar)findViewById(R.id.progressBar);

        if(!TextUtils.isEmpty(mobileNo))
        {
            recoveryMessage=recoveryMessage+"mobile no ("+mobileNo+") and mail";
        }
        else
        {
            recoveryMessage=recoveryMessage+"mobile no and mail";
        }

        tvResetPws.setText(recoveryMessage);

        sessionManager = new SessionManager(getApplicationContext());

        etOtp=(EditText)findViewById(R.id.etOTP);
        etNewPwd=(EditText)findViewById(R.id.etNewPassword);
        etConfirmPwd=(EditText)findViewById(R.id.etConfirmPassword);
        btnResendOTP=(Button)findViewById(R.id.btn_resend_otp);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        if(fab!=null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    resetPassword(v);
                }
            });
        }

        if(!TextUtils.isEmpty(getIntent().getStringExtra(SMSListener.PARAM_OTP_CODE)))
        {
            etOtp.setText(getIntent().getStringExtra(SMSListener.PARAM_OTP_CODE));
            etNewPwd.requestFocus();
        }

        BroadcastReceiver otpReceiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                etOtp.setText(intent.getStringExtra(SMSListener.PARAM_OTP_CODE));
                etNewPwd.requestFocus();

            }
        };

        LocalBroadcastManager.getInstance(ResetPasswordActivity.this).registerReceiver(otpReceiver,
                new IntentFilter(SMSListener.OTP_LISTENER));

        btnResendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resendOTP(mobileNo);
            }
        });

        startCountDown();

    }

    private void resendOTP(final String mobileNo)
    {

        final ProgressDialog progressDialog=new ProgressDialog(ResetPasswordActivity.this);
        progressDialog.setMessage("Resending OTP...");
        progressDialog.show();

        ApiController.resetAndGetAPI(ResetPasswordActivity.this).recoverPassword(mobileNo, sessionManager.getBaseURL(), new Callback<JsonObject>() {
            @Override
            public void success(JsonObject resp, Response response) {

                String message=resp.get("status").getAsJsonObject().get("message").getAsString();
                Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                toast.show();
                progressDialog.dismiss();

                long millis = System.currentTimeMillis();
                sessionManager.setForgotPasswordTime(millis);
                sessionManager.setResetPasswordLastMobileNo(mobileNo);
                startCountDown();

            }

            @Override
            public void failure(RetrofitError error) {
                if (error != null) {
                    if (error.getLocalizedMessage() != null) {
                        Toast toast = Toast.makeText(ResetPasswordActivity.this, error.getLocalizedMessage(), Toast.LENGTH_LONG);
                        toast.show();
                    }
                }
                progressDialog.dismiss();
            }
        });

    }

    private void startCountDown()
    {
        final long otpSentMillis =sessionManager.getForgotPasswordTime();
        final long otpExpiryMillis=otpSentMillis+(5*60*1000);

        long remainingMillis= otpExpiryMillis-System.currentTimeMillis();

        if(remainingMillis>0) {

            if(countDownTimer!=null)
            {
                countDownTimer.cancel();
            }

            countDownTimer=new CountDownTimer(remainingMillis, 1000) {

                public void onTick(long millisUntilFinished) {

                    long millis = millisUntilFinished;

                    String msText = String.format("%02dm %02ds",
                            (TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis))),
                            (TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))));

                    msText = msText.replace("-", "");

                    tvCountDown.setText("Your OTP will be expiry in " + msText);

                }

                public void onFinish() {
                    tvCountDown.setText(R.string.otp_exipry_message);
                    sessionManager.setForgotPasswordTime(0l);

                }

            }.start();
        }
        else {
            tvCountDown.setText(R.string.otp_exipry_message);
            sessionManager.setForgotPasswordTime(0l);
        }
    }

    private void resetPassword(final View view)
    {
        if(TextUtils.isEmpty(etOtp.getText()))
        {
            Snackbar.make(view, "Please enter the otp code", Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        }
        else if(TextUtils.isEmpty(etNewPwd.getText()))
        {
            Snackbar.make(view, "Please enter the new password", Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        }
        else if(!etNewPwd.getText().toString().equals(etConfirmPwd.getText().toString()))
        {
            Snackbar.make(view, "Password doesn't match", Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        }
        else if (!AppUtils.isValidPassword(etNewPwd.getText().toString(), configManager)) {
            Toast.makeText(ResetPasswordActivity.this, getPasswordConstraintInformation(), Toast.LENGTH_SHORT).show();
        }
        else
        {

            fab.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);

            ApiController.resetAndGetAPI(ResetPasswordActivity.this).resetPassword(mobileNo, etOtp.getText().toString(), etNewPwd.getText().toString(), etConfirmPwd.getText().toString(), new Callback<JsonObject>() {
                @Override
                public void success(JsonObject resp, Response response) {

                    sessionManager.setForgotPasswordTime(0l);
                    sessionManager.setLastRegisteredUserTime(0l);
                    sessionManager.setRegisteredUserLastOTPTime(0l);
                    sessionManager.setResetPasswordLastMobileNo("");

                    String message=resp.get("status").getAsJsonObject().get("message").getAsString();
                    Toast toast = Toast.makeText(getApplicationContext(), "Your password successfully changed, Please login now", Toast.LENGTH_LONG);
                    toast.show();

                    startActivity(new Intent(ResetPasswordActivity.this, LoginActivity.class));
                    finish();

                }

                @Override
                public void failure(RetrofitError error) {
                    fab.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                    if (error != null) {
                        if (error.getLocalizedMessage() != null) {
                            Snackbar.make(view, error.getLocalizedMessage(), Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    }
                }
            });

        }
    }

    private String getPasswordConstraintInformation()
    {
        String pwdLevel=configManager.getString("app.passwordLevel");
        if(pwdLevel.equals(PasswordLevel.HIGH))
        {
            return getResources().getString(R.string.password_level_high);
        }
        else if(pwdLevel.equals(PasswordLevel.MEDIUM))
        {
            return getResources().getString(R.string.password_level_medium);
        }
        else
        {
            return getResources().getString(R.string.password_level_low);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        sessionManager.setOTPLocalBroadCastRunning(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sessionManager.setOTPLocalBroadCastRunning(false);
    }
}
