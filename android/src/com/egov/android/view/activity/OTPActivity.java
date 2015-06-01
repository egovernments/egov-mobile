package com.egov.android.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.egov.android.R;
import com.egov.android.controller.ApiController;
import com.egov.android.library.api.ApiResponse;
import com.egov.android.library.listener.Event;

public class OTPActivity extends BaseActivity {

    private boolean toastShown = false;

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

        _clearStatus(new int[] { R.id.otp_code_status });

        EditText code = (EditText) findViewById(R.id.otp_code);

        if (isEmpty(code.getText().toString())) {
            _changeStatus("error", R.id.otp_code_status, "Please enter the verification code");
        }

        if (!isEmpty(code.getText().toString())) {
            ApiController.getInstance().verifyOTP(this);
        }
    }

    private void _clearStatus(int[] ids) {
        toastShown = false;
        for (int id : ids) {
            setImageBackground(id, "success");
        }
    }

    private void _changeStatus(String type, int id, String message) {
        setImageBackground(id, type);
        showMsg(message);
    }

    private void showMsg(String message) {
        if (toastShown == false && message != null && !message.equals("")) {
            Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 0, 120);
            toast.show();
            toastShown = true;
        }
    }

    private boolean isEmpty(String data) {
        return (data == null || (data != null && data.trim().equals("")));
    }

    private void setImageBackground(int id, String type) {
        int image = (type == "success") ? R.drawable.success_icon : R.drawable.error_icon;
        ((ImageView) findViewById(id)).setVisibility(View.VISIBLE);
        ((ImageView) findViewById(id)).setBackgroundResource(image);
    }

    @Override
    public void onResponse(Event<ApiResponse> event) {
        super.onResponse(event);
        String status = event.getData().getApiStatus().getStatus();
        String msg = event.getData().getApiStatus().getMessage();
        showMsg(msg);
        if (status.equalsIgnoreCase("success")) {
            startActivity(new Intent(this, ComplaintListActivity.class));
        }
    }
}
