package com.egov.android.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.egov.android.R;
import com.egov.android.library.api.ApiResponse;
import com.egov.android.library.listener.Event;

public class LoginActivity extends BaseActivity {

    private boolean toastShown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ((Button) findViewById(R.id.login_doLogin)).setOnClickListener(this);
        ((Button) findViewById(R.id.login_register)).setOnClickListener(this);
        ((TextView) findViewById(R.id.forgot_pwd_link)).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.login_doLogin:
                login();
                break;

            case R.id.login_register:
                startActivity(new Intent(this, RegisterActivity.class));
                break;
            case R.id.forgot_pwd_link:
                startActivity(new Intent(this, ForgotPasswordActivity.class));
                break;
        }
    }

    private void login() {

        _clearStatus(new int[] { R.id.login_email_status, R.id.login_password_status });

        EditText email = (EditText) findViewById(R.id.login_email);
        EditText password = (EditText) findViewById(R.id.login_password);

        if (isEmpty(email.getText().toString())) {
            _changeStatus("error", R.id.login_email_status, "Please enter email or phone number");
        }
        if (isEmpty(password.getText().toString())) {
            _changeStatus("error", R.id.login_password_status, "Please enter password");
        }

        if (!isEmpty(email.getText().toString()) && !isEmpty(password.getText().toString())) {
            // ApiController.getInstance().login(this);
            startActivity(new Intent(this, ComplaintTypeListActivity.class));
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
            startActivity(new Intent(this, ComplaintTypeListActivity.class));
        }
    }
}
