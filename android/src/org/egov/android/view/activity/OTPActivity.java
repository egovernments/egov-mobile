package org.egov.android.view.activity;

import org.egov.android.R;
import org.egov.android.controller.ApiController;
import org.egov.android.library.api.ApiResponse;
import org.egov.android.library.listener.Event;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class OTPActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        ((Button) findViewById(R.id.verify_otp)).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.verify_otp:
                verifyOTP();
                break;
        }
    }

    private void verifyOTP() {

        EditText code = (EditText) findViewById(R.id.otp_code);

        if (isEmpty(code.getText().toString())) {
            showMsg(_getMessage(R.string.code_empty));
        } else {
            String userName = getSession().getString("user_name", "");
            ApiController.getInstance().verifyOTP(this, userName, code.getText().toString());
        }
    }

    private String _getMessage(int id) {
        return getResources().getString(id);
    }

    private void showMsg(String message) {
        if (message != null && !message.equals("")) {
            Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 0, 120);
            toast.show();
        }
    }

    private boolean isEmpty(String data) {
        return (data == null || (data != null && data.trim().equals("")));
    }

    @Override
    public void onResponse(Event<ApiResponse> event) {
        super.onResponse(event);
        String status = event.getData().getApiStatus().getStatus();
        String msg = event.getData().getApiStatus().getMessage();
        showMsg(msg);
        if (status.equalsIgnoreCase("success")) {
            startActivity(new Intent(this, ComplaintActivity.class));
        }
    }
}
