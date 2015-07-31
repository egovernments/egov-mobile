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
