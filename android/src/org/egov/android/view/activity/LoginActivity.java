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
import org.egov.android.controller.ApiController;
import org.egov.android.api.ApiResponse;
import org.egov.android.listener.Event;
import org.egov.android.model.User;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends BaseActivity {

    /**
     * To set the layout for the LoginActivity and set click listeners to the login, register and
     * forgot password views.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ((Button) findViewById(R.id.login_doLogin)).setOnClickListener(this);
        ((Button) findViewById(R.id.login_register)).setOnClickListener(this);
        ((TextView) findViewById(R.id.forgot_pwd_link)).setOnClickListener(this);

        ((TextView) findViewById(R.id.hdr_title)).setPadding(25, 0, 0, 0);

        getSession().edit().putInt("api_level", Build.VERSION.SDK_INT).commit();
    }

    /**
     * Event triggered when click on the item having click listener. When click on login button
     * _login() function get called. When click on register button redirect to RegisterActivity.
     * When click on forgot password textview redirect to ForgotPasswordActivity.
     */
    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.login_doLogin:
                _login();
                break;
            case R.id.login_register:
                startActivity(new Intent(this, RegisterActivity.class));
                break;
            case R.id.forgot_pwd_link:
                startActivity(new Intent(this, ForgotPasswordActivity.class));
                break;
        }
    }

    /**
     * Function called when click on login button. Check the empty validations if any require field
     * is empty show the message. Call the login api to check the user credentials.
     */
    private void _login() {

        String email = ((EditText) findViewById(R.id.login_email)).getText().toString().trim();
        String password = ((EditText) findViewById(R.id.login_password)).getText().toString()
                .trim();

        if (isEmpty(email)) {
            showMessage(getMessage(R.string.email_phone_empty));
            return;
        } else if (isEmpty(password)) {
            showMessage(getMessage(R.string.password_empty));
            return;
        } else if (password.length() < 6) {
            showMessage(getMessage(R.string.password_length));
            return;
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(password);

        ApiController.getInstance().login(this, user);
    }

    /**
     * Login api call response handler. If the response has status as "success" store the
     * access_token from the response in shared preference object. Then redirect to
     * ComplaintActivity and finish the LoginActivity. If response has error as activate your
     * account then redirect to the AccountActivationActivity and send the email/phone entered by
     * the user in login form through intent.
     */
    @Override
    public void onResponse(Event<ApiResponse> event) {
        super.onResponse(event);
        String status = event.getData().getApiStatus().getStatus();
        String msg = event.getData().getApiStatus().getMessage();
        showMessage(msg);
        if (status.equalsIgnoreCase("success")) {
            try {
                JSONArray ja = new JSONArray(event.getData().getResponse().toString());
                JSONObject jo = ja.getJSONObject(0);
                getSession().edit().putString("access_token", jo.getString("access_token"))
                        .commit();
                startActivity(new Intent(this, ComplaintActivity.class));
                finish();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            if (msg.equalsIgnoreCase("Please activate your account")) {
                Intent intent = new Intent(this, AccountActivationActivity.class);
                intent.putExtra("username", ((EditText) findViewById(R.id.login_email)).getText()
                        .toString().trim());
                startActivity(intent);
            }
        }
    }
}
