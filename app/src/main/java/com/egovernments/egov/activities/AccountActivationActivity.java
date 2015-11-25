package com.egovernments.egov.activities;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.egovernments.egov.R;
import com.egovernments.egov.network.ApiController;
import com.google.gson.JsonObject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class AccountActivationActivity extends AppCompatActivity {

    private String username;
    private String password;
    private String activationCode;

    private ProgressBar progressBar;

    private FloatingActionButton activateButton;
    private com.melnykov.fab.FloatingActionButton activateButtonCompat;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accountactivation);

        progressBar = (ProgressBar) findViewById(R.id.activateprogressBar);
        button = (Button) findViewById(R.id.activate_resend);

        username = getIntent().getStringExtra("username");
        password = getIntent().getStringExtra("password");

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        final EditText code_edittext = (EditText) findViewById(R.id.activate_otpfield);

        code_edittext.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    activationCode = code_edittext.toString().trim();
                    progressBar.setVisibility(View.VISIBLE);
                    activateButton.setVisibility(View.GONE);
                    activateButtonCompat.setVisibility(View.GONE);
                    button.setVisibility(View.INVISIBLE);
                    submit(activationCode);
                    return true;
                }
                return false;
            }
        });


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApiController.getAPI().sendOTP(username, new Callback<JsonObject>() {
                    @Override
                    public void success(JsonObject jsonObject, Response response) {
                        Toast.makeText(AccountActivationActivity.this, R.string.otp_resent_msg, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void failure(RetrofitError error) {

                        Toast.makeText(AccountActivationActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        activateButton = (FloatingActionButton) findViewById(R.id.activate_verify);
        activateButtonCompat = (com.melnykov.fab.FloatingActionButton) findViewById(R.id.activate_verifycompat);

        final View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                activationCode = code_edittext.getText().toString().trim();
                progressBar.setVisibility(View.VISIBLE);
                activateButton.setVisibility(View.GONE);
                activateButtonCompat.setVisibility(View.GONE);
                submit(activationCode);
            }
        };


        if (Build.VERSION.SDK_INT >= 21) {

            activateButton.setOnClickListener(onClickListener);
        } else {

            activateButtonCompat.setVisibility(View.VISIBLE);
            activateButton.setVisibility(View.GONE);
            activateButtonCompat.setOnClickListener(onClickListener);

        }
    }

    private void submit(String activationCode) {

        if (!activationCode.isEmpty()) {
            ApiController.getAPI().activate(username, activationCode, new Callback<JsonObject>() {
                @Override
                public void success(JsonObject jsonObject, Response response) {
                    Toast.makeText(AccountActivationActivity.this, R.string.account_activated_msg, Toast.LENGTH_SHORT).show();
                    ApiController.getLoginAPI().Login(username, "read write", password, "password", new Callback<JsonObject>() {
                        @Override
                        public void success(JsonObject jsonObject, Response response) {

                            startActivity(new Intent(AccountActivationActivity.this, HomeActivity.class));
                            finish();
                        }

                        @Override
                        public void failure(RetrofitError error) {

                            Toast.makeText(AccountActivationActivity.this, "An error occurred while logging in", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(AccountActivationActivity.this, LoginActivity.class));
                        }
                    });
                }

                @Override
                public void failure(RetrofitError error) {
                    if (error != null) {

                        JsonObject jsonObject = (JsonObject) error.getBody();
                        if (jsonObject != null)
                            Toast.makeText(AccountActivationActivity.this, "The OTP is incorrect or has expired", Toast.LENGTH_SHORT).show();

                    } else
                        Toast.makeText(AccountActivationActivity.this, "An unexpected error occurred", Toast.LENGTH_SHORT).show();

                    progressBar.setVisibility(View.GONE);
                    if (Build.VERSION.SDK_INT >= 21) {
                        activateButton.setVisibility(View.VISIBLE);
                    } else {
                        activateButtonCompat.setVisibility(View.VISIBLE);
                    }
                }
            });
        } else {
            Toast.makeText(AccountActivationActivity.this, "Please enter OTP", Toast.LENGTH_SHORT).show();

            progressBar.setVisibility(View.GONE);
            button.setVisibility(View.VISIBLE);
            if (Build.VERSION.SDK_INT >= 21) {
                activateButton.setVisibility(View.VISIBLE);
            } else {
                activateButtonCompat.setVisibility(View.VISIBLE);
            }
        }


    }
}
