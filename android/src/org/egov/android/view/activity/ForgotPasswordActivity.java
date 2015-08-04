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

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ForgotPasswordActivity extends BaseActivity {

    /**
     * To set the layout for the ForgotPasswordActivity and set click listener to the send button.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        ((Button) findViewById(R.id.forgot_pwd_send)).setOnClickListener(this);
    }

    /**
     * Event triggered when click on the item having click listener. When click on send button
     * _forgotPassword() function get called.
     */
    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.forgot_pwd_send:
                _forgotPassword();
                break;
        }
    }

    /**
     * Function called when click on forgot password. If the phone field is empty show empty field
     * message. Otherwise send the phone number to back end to reset the password through api call.
     */
    private void _forgotPassword() {
        String phone = ((EditText) findViewById(R.id.forgot_pwd_phone)).getText().toString().trim();
        if (isEmpty(phone)) {
            showMessage(getMessage(R.string.phone_empty));
        } else {
            ApiController.getInstance().forgotPassword(this, phone);
        }
    }

    /**
     * Forgot password api call response handler. Show the success/error message to the user. If the
     * response has status as 'success' then finish the ForgotPasswordActivity.
     */
    @Override
    public void onResponse(Event<ApiResponse> event) {
        super.onResponse(event);
        String status = event.getData().getApiStatus().getStatus();
        String msg = event.getData().getApiStatus().getMessage();
        showMessage(msg);
        if (status.equalsIgnoreCase("success")) {
            finish();
        }
    }
}
