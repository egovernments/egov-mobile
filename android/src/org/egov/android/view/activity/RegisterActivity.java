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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ((Button) findViewById(R.id.register_doRegister)).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.register_doRegister:
                register();
                break;
        }
    }

    private void register() {

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
        } else if (phone.length() < 10) {
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

    private boolean _isValidEmail(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches() ? true : false;
    }

    private void _clearAllText() {
        ((EditText) findViewById(R.id.register_name)).setText("");
        ((EditText) findViewById(R.id.register_phone)).setText("");
        ((EditText) findViewById(R.id.register_email)).setText("");
        ((EditText) findViewById(R.id.register_password)).setText("");
        ((EditText) findViewById(R.id.register_confirm_password)).setText("");
    }

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
