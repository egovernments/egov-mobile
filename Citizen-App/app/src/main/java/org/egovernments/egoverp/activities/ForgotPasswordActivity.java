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


import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.egovernments.egoverp.R;
import org.egovernments.egoverp.network.ApiController;
import org.egovernments.egoverp.network.SessionManager;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * The password recovery screen activity
 **/

public class ForgotPasswordActivity extends AppCompatActivity {

    private String phone;
    private ProgressBar progressBar;

    private FloatingActionButton sendButton;
    private com.melnykov.fab.FloatingActionButton sendButtonCompat;

    EditText phone_edittext;

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);

        progressBar = (ProgressBar) findViewById(R.id.forgotprogressBar);

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        sessionManager = new SessionManager(getApplicationContext());

        sendButton = (FloatingActionButton) findViewById(R.id.forgotpassword_send);
        sendButtonCompat = (com.melnykov.fab.FloatingActionButton) findViewById(R.id.forgotpassword_sendcompat);

        phone_edittext = (EditText) findViewById(R.id.forgotpassword_edittext);
        phone_edittext.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    phone = phone_edittext.getText().toString().trim();
                    progressBar.setVisibility(View.VISIBLE);
                    sendButton.setVisibility(View.GONE);
                    sendButtonCompat.setVisibility(View.GONE);
                    submit(phone);
                    return true;
                }
                return false;
            }
        });


        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                phone = phone_edittext.getText().toString().trim();
                progressBar.setVisibility(View.VISIBLE);
                sendButton.setVisibility(View.GONE);
                sendButtonCompat.setVisibility(View.GONE);
                submit(phone);


            }
        };

        if (Build.VERSION.SDK_INT >= 21)

        {

            sendButton.setOnClickListener(onClickListener);

        } else

        {
            sendButtonCompat.setVisibility(View.VISIBLE);
            sendButton.setVisibility(View.GONE);
            sendButtonCompat.setOnClickListener(onClickListener);
        }

    }

    //Invokes call to API
    private void submit(final String phone) {

        if (TextUtils.isEmpty(phone)) {
            Toast toast = Toast.makeText(ForgotPasswordActivity.this, R.string.forgot_password_prompt, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();
            progressBar.setVisibility(View.GONE);
            if (Build.VERSION.SDK_INT >= 21) {
                sendButton.setVisibility(View.VISIBLE);
            } else {
                sendButtonCompat.setVisibility(View.VISIBLE);
            }

        } else if (phone.length() != 10) {
            Toast toast = Toast.makeText(ForgotPasswordActivity.this, R.string.mobilenumber_length_prompt, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();
            progressBar.setVisibility(View.GONE);
            if (Build.VERSION.SDK_INT >= 21) {
                sendButton.setVisibility(View.VISIBLE);
            } else {
                sendButtonCompat.setVisibility(View.VISIBLE);
            }
        } else {



            ApiController.resetAndGetAPI(ForgotPasswordActivity.this).recoverPassword(phone, sessionManager.getBaseURL(), new Callback<JsonObject>() {
                @Override
                public void success(JsonObject resp, Response response) {

                    String message=resp.get("status").getAsJsonObject().get("message").getAsString();

                    Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                    toast.show();

                    progressBar.setVisibility(View.GONE);
                    if (Build.VERSION.SDK_INT >= 21) {
                        sendButton.setVisibility(View.VISIBLE);
                    } else {
                        sendButtonCompat.setVisibility(View.VISIBLE);
                    }


                    long millis = System.currentTimeMillis();

                    sessionManager.setForgotPasswordTime(millis);
                    sessionManager.setResetPasswordLastMobileNo(phone);

                    Intent resetPasswordActivity=new Intent(getApplicationContext(), ResetPasswordActivity.class);
                    resetPasswordActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    resetPasswordActivity.putExtra(ResetPasswordActivity.MESSAGE_SENT_TO, phone_edittext.getText().toString());
                    resetPasswordActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(resetPasswordActivity);

                }

                @Override
                public void failure(RetrofitError error) {
                    if (error != null) {
                        if (error.getLocalizedMessage() != null) {
                            Toast toast = Toast.makeText(ForgotPasswordActivity.this, error.getLocalizedMessage(), Toast.LENGTH_LONG);
                            toast.show();
                        }
                    }

                    progressBar.setVisibility(View.GONE);
                    if (Build.VERSION.SDK_INT >= 21) {
                        sendButton.setVisibility(View.VISIBLE);
                    } else {
                        sendButtonCompat.setVisibility(View.VISIBLE);
                    }

                }
            });

        }


    }
}
