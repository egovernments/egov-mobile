package org.egov.android.view.activity;

import org.egov.android.R;
import org.egov.android.controller.ApiController;
import org.egov.android.api.ApiResponse;
import org.egov.android.listener.Event;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AccountActivationActivity extends BaseActivity {

    private String username = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        username = getIntent().getExtras().getString("username");
        ((Button) findViewById(R.id.verify_otp)).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.verify_otp:
                _accountActivation();
                break;
        }
    }

    /**
     * Function called when click on verify OTP
     * Check whether the OTP is entered or not
     * If entered means call the account activation api to activate the account
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
     * Api response handler
     * If the response is success go to LoginActivity to login into the app
     */
    @Override
    public void onResponse(Event<ApiResponse> event) {
        super.onResponse(event);
        String status = event.getData().getApiStatus().getStatus();
        String msg = event.getData().getApiStatus().getMessage();
        showMessage(msg);
        if (status.equalsIgnoreCase("success")) {
            startLoginActivity();
        }
    }
}
