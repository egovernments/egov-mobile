package com.egov.android.view.activity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

public class RegisterActivity extends BaseActivity {

    private boolean toastShown = false;
    EditText name, phone, email, password, confirm_password;

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
                //register();
                startActivity(new Intent(this, OTPActivity.class));
                break;
        }
    }

    private void register() {

        _clearStatus(new int[] { R.id.register_name_status, R.id.register_phone_status,
                R.id.register_email_status, R.id.register_password_status,
                R.id.register_cfrm_password_status });

        name = (EditText) findViewById(R.id.register_name);
        phone = (EditText) findViewById(R.id.register_phone);
        email = (EditText) findViewById(R.id.register_email);
        password = (EditText) findViewById(R.id.register_password);
        confirm_password = (EditText) findViewById(R.id.register_confirm_password);

        if (isEmpty(name.getText().toString())) {
            _changeStatus("error", R.id.register_name_status, _setMessage(R.string.name_empty));
        }
        if (isEmpty(phone.getText().toString())) {
            _changeStatus("error", R.id.register_phone_status, _setMessage(R.string.phone_empty));
        }
        if (isEmpty(email.getText().toString())) {
            _changeStatus("error", R.id.register_email_status, _setMessage(R.string.email_empty));
        }

        if (!isValidEmail(email.getText().toString())) {
            _changeStatus("error", R.id.register_email_status, _setMessage(R.string.invalid_email));
        }
        if (isEmpty(password.getText().toString())) {
            _changeStatus("error", R.id.register_password_status,
                    _setMessage(R.string.password_empty));
        }
        if (isEmpty(confirm_password.getText().toString())) {
            _changeStatus("error", R.id.register_cfrm_password_status,
                    _setMessage(R.string.confirm_password_empty));
        }

        if (!isEmpty(name.getText().toString()) && !isEmpty(phone.getText().toString())
                && !isEmpty(email.getText().toString()) && isValidEmail(email.getText().toString())
                && !isEmpty(password.getText().toString())
                && !isEmpty(confirm_password.getText().toString())) {
            ApiController.getInstance().register(this);
        }

    }

    private boolean isValidEmail(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches() ? true : false;
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

    private String _setMessage(int id) {
        return getResources().getString(id);
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
        String msg = event.getData().getApiStatus().getMessage();
        String status = event.getData().getApiStatus().getStatus();
        showMsg(msg);
        if (status.equalsIgnoreCase("success")) {
            startActivity(new Intent(this, OTPActivity.class));
        }
    }
}
