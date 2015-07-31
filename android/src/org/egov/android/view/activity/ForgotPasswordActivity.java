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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        ((Button) findViewById(R.id.forgot_pwd_send)).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.forgot_pwd_send:
                forgotPassword();
                break;
        }
    }

    private void forgotPassword() {
        String email = ((EditText) findViewById(R.id.forgot_pwd_email)).getText().toString().trim();
        if (isEmpty(email)) {
            showMessage(getMessage(R.string.phone_empty));
        } else {
            ApiController.getInstance().forgotPassword(this, email);
        }
    }

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
