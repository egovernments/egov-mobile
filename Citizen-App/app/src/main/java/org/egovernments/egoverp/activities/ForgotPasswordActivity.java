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
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.IntentCompat;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.JsonObject;

import org.egovernments.egoverp.R;
import org.egovernments.egoverp.api.ApiController;

import retrofit2.Call;

/**
 * The password recovery screen activity
 **/

public class ForgotPasswordActivity extends BaseActivity {

    EditText phone_edittext;
    private String phone;
    private ProgressBar progressBar;
    private FloatingActionButton sendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.account_recovery_label);
        setContentViewWithNavBar(R.layout.activity_forgotpassword, false);

        progressBar = (ProgressBar) findViewById(R.id.forgotprogressBar);

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        sendButton = (FloatingActionButton) findViewById(R.id.forgotpassword_send);

        phone_edittext = (EditText) findViewById(R.id.forgotpassword_edittext);
        phone_edittext.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    phone = phone_edittext.getText().toString().trim();
                    progressBar.setVisibility(View.VISIBLE);
                    sendButton.setVisibility(View.GONE);
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
                submit(phone);
            }
        };

        sendButton.setOnClickListener(onClickListener);

    }

    //Invokes call to API
    private void submit(final String phone) {

        if (TextUtils.isEmpty(phone)) {
            showSnackBar(R.string.forgot_password_prompt);
            progressBar.setVisibility(View.GONE);
            sendButton.setVisibility(View.VISIBLE);
        } else if (phone.length() != 10) {
            showSnackBar(R.string.mobilenumber_length_prompt);
            progressBar.setVisibility(View.GONE);
            sendButton.setVisibility(View.VISIBLE);
        } else if (validateInternetConnection()) {

            progressBar.setVisibility(View.VISIBLE);
            sendButton.setVisibility(View.GONE);

            Call<JsonObject> recoverPasswordCall = ApiController.getRetrofit2API(getApplicationContext(),
                    sessionManager.getBaseURL()).recoverPassword(phone, sessionManager.getBaseURL());

            recoverPasswordCall.enqueue(new retrofit2.Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {

                    if (response.isSuccessful()) {

                        progressBar.setVisibility(View.GONE);
                        sendButton.setVisibility(View.VISIBLE);

                        long millis = System.currentTimeMillis();

                        sessionManager.setForgotPasswordTime(millis);
                        sessionManager.setResetPasswordLastMobileNo(phone);

                        Intent resetPasswordActivity = new Intent(getApplicationContext(), ResetPasswordActivity.class);
                        resetPasswordActivity.putExtra(ResetPasswordActivity.MESSAGE_SENT_TO, phone_edittext.getText().toString());
                        resetPasswordActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(resetPasswordActivity);
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    sendButton.setVisibility(View.VISIBLE);
                }
            });

        }


    }
}
