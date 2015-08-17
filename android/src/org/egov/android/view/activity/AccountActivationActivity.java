/**
 * eGov suite of products aim to improve the internal efficiency,transparency, accountability and the service delivery of the
 * government organizations.
 * 
 * Copyright (C) <2015> eGovernments Foundation
 * 
 * The updated version of eGov suite of products as by eGovernments Foundation is available at http://www.egovernments.org
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * http://www.gnu.org/licenses/ or http://www.gnu.org/licenses/gpl.html .
 * 
 * In addition to the terms of the GPL license to be adhered to in using this program, the following additional terms are to be
 * complied with:
 * 
 * 1) All versions of this program, verbatim or modified must carry this Legal Notice.
 * 
 * 2) Any misrepresentation of the origin of the material is prohibited. It is required that all modified versions of this
 * material be marked in reasonable ways as different from the original version.
 * 
 * 3) This license does not grant any rights to any user of the program with regards to rights under trademark law for use of the
 * trade names or trademarks of eGovernments Foundation.
 * 
 * In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */

package org.egov.android.view.activity;

import org.egov.android.R;
import org.egov.android.api.ApiResponse;
import org.egov.android.api.ApiUrl;
import org.egov.android.api.IApiUrl;
import org.egov.android.controller.ApiController;
import org.egov.android.listener.Event;
import org.egov.android.model.User;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class AccountActivationActivity extends BaseActivity {

    private String username = "";
    private String password = "";

    /**
     * It is used to initialize an activity. An Activity is an application component that provides a
     * screen with which users can interact in order to do something, To initialize the
     * AccountActivationActivity. Assign the value passed from the Register activity through intent
     * as 'username' to variable username. Set click listener to the verify OTP button.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        username = getIntent().getExtras().getString("username");
        password = getIntent().getExtras().getString("password");
        ((Button) findViewById(R.id.verify_otp)).setOnClickListener(this);
        ((TextView) findViewById(R.id.resend_otp)).setOnClickListener(this);
    }

    /**
     * Event triggered when clicking on the item having click listener. Clicking on verify OTP
     * button _accountActivation() function get called.
     */
    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.verify_otp:
                _accountActivation();
                break;
            case R.id.resend_otp:
                ApiController.getInstance().resendOTP(this, username);
                break;
        }
    }

    /**
     * Function called when clicking on verify OTP button. Check the account activation code entered
     * or not.If not entered show the empty message. If the user enters the code call the account
     * activation api with username and code to check whether the code is correct or not.
     */
    private void _accountActivation() {
        String code = ((EditText) findViewById(R.id.otp_code)).getText().toString().trim();
        if (isEmpty(code)) {
            showMessage(getMessage(R.string.code_empty));
        } else {
            ApiController.getInstance().accountActivation(this, username, code);
        }
    }

    /**
     * The onResponse method will be invoked after the Account activation API call onResponse
     * methods will contain the response If the response has a status as 'success' then message is
     * displayed in toast. Finally calls the startLoginActivity() function and redirects to Login
     * Activity.
     */
    @Override
    public void onResponse(Event<ApiResponse> event) {
        super.onResponse(event);
        IApiUrl url = event.getData().getApiMethod().getApiUrl();
        String status = event.getData().getApiStatus().getStatus();
        String msg = event.getData().getApiStatus().getMessage();
        showMessage(msg);
        if (status.equalsIgnoreCase("success")) {
            if (url.getUrl().equals(ApiUrl.VERIFY_OTP.getUrl())) {
                User user = new User();
                user.setEmail(username);
                user.setPassword(password);
                ApiController.getInstance().login(this, user);
            } else if (url.getUrl().equals(ApiUrl.LOGIN.getUrl())) {
                try {
                    JSONArray ja = new JSONArray(event.getData().getResponse().toString());
                    JSONObject jo = ja.getJSONObject(0);
                    getSession().edit().putString("access_token", jo.getString("access_token"))
                            .commit();
                    getSession().edit().putString("user_name", jo.getString("user_name")).commit();
                    Intent intent = new Intent(this, ComplaintActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
