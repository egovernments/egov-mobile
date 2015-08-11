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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.egov.android.R;
import org.egov.android.controller.ApiController;
import org.egov.android.api.ApiResponse;
import org.egov.android.listener.Event;
import org.egov.android.model.User;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class RegisterActivity extends BaseActivity {

    /**
     * It is  used to initialize an activity.
     * An Activity is an application component that provides a screen
     *  with which users can interact in order to do something,
     * To initialize the RegisterActivity.Set click listener to the save button.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ((Button) findViewById(R.id.register_doRegister)).setOnClickListener(this);
    }

    /**
     * Event triggered when clicking on the item having click listener. When user clicks on save button
     * _register() function get called.
     */
    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.register_doRegister:
                _register();
                break;
        }
    }

    /**
     * Function called when clicking on save button. Check the empty field validation and show the
     * message. If all fields are correct then call the register api to register a new citizen.
     */
    private void _register() {

        String name = ((EditText) findViewById(R.id.register_name)).getText().toString().trim();
        String phone = ((EditText) findViewById(R.id.register_phone)).getText().toString().trim();
        String email = ((EditText) findViewById(R.id.register_email)).getText().toString().trim();
        String password = ((EditText) findViewById(R.id.register_password)).getText().toString()
                .trim();
        String confirm_password = ((EditText) findViewById(R.id.register_confirm_password))
                .getText().toString().trim();
        String deviceId = Secure.getString(this.getContentResolver(), Secure.ANDROID_ID);

        if (isEmpty(name)) {
            showMessage(getMessage(R.string.name_empty));
            return;
        } else if (name.length() < 3) {
            showMessage(getMessage(R.string.name_length));
            return;
        } else if (isEmpty(phone)) {
            showMessage(getMessage(R.string.phone_empty));
            return;
        } else if (phone.length() != 10) {
            showMessage(getMessage(R.string.phone_number_length));
            return;
        } else if (isEmpty(email)) {
            showMessage(getMessage(R.string.email_empty));
            return;
        } else if (!_isValidEmail(email)) {
            showMessage(getMessage(R.string.invalid_email));
            return;
        } else if (isEmpty(password)) {
            showMessage(getMessage(R.string.password_empty));
            return;
        } else if (password.length() < 6) {
            showMessage(getMessage(R.string.password_length));
            return;
        } else if (isEmpty(confirm_password.toString())) {
            showMessage(getMessage(R.string.confirm_password_empty));
            return;
        } else if (!password.equalsIgnoreCase(confirm_password)) {
            showMessage(getMessage(R.string.password_match));
            return;
        }

        User user = new User();
        user.setName(name);
        user.setMobileNo(phone);
        user.setEmail(email);
        user.setPassword(password);
        user.setConfirmPassword(confirm_password);
        user.setDeviceId(deviceId);
        ApiController.getInstance().register(this, user);
    }

    /**
     * Function used to check whether the entered mail id is valid or not
     * 
     * @param email
     *            => mail id entered by user
     * @return
     */
    private boolean _isValidEmail(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches() ? true : false;
    }

    /**
     * Function used to clear the fields once user successfully registered
     */
    private void _clearAllText() {
        ((EditText) findViewById(R.id.register_name)).setText("");
        ((EditText) findViewById(R.id.register_phone)).setText("");
        ((EditText) findViewById(R.id.register_email)).setText("");
        ((EditText) findViewById(R.id.register_password)).setText("");
        ((EditText) findViewById(R.id.register_confirm_password)).setText("");
    }

    /**
     * The onResponse method will be invoked after the Register activation API call 
     * onResponse methods will contain the response
     * If the response has status as 'success' then 
     * onResponse contains the JSON object.
     * The JSONObject is handled in the onResponse method.finally
     * _clearAllText() function to reset the fields.
     * then  redirect to AccountActivationActivity and
     * pass the email/phone through intent.
     */
    @Override
    public void onResponse(Event<ApiResponse> event) {
        super.onResponse(event);
        String msg = event.getData().getApiStatus().getMessage();
        String status = event.getData().getApiStatus().getStatus();
        showMessage(msg);
        if (status.equalsIgnoreCase("success")) {
            _clearAllText();
            try {
                JSONObject jo = new JSONObject(event.getData().getResponse().toString());
                Intent intent = new Intent(this, AccountActivationActivity.class);
                intent.putExtra("username", jo.getString("userName"));
                startActivity(intent);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
