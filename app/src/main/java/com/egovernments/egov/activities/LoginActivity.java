package com.egovernments.egov.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import com.egovernments.egov.network.SessionManager;
import com.egovernments.egov.network.UpdateService;
import com.google.gson.JsonObject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class LoginActivity extends Activity {

    private String username;
    private String password;

    private ProgressBar progressBar;

    private FloatingActionButton loginButton;
    private com.melnykov.fab.FloatingActionButton loginbuttoncompat;
    private TextView forgotLabel;
    private Button signupButton;

    private EditText username_edittext;
    private EditText password_edittext;

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(getApplicationContext());

        if (sessionManager.isLoggedIn()) {
            if (sessionManager.getAccessToken() != (null)) {
                startService(new Intent(LoginActivity.this, UpdateService.class).putExtra(UpdateService.KEY_METHOD, UpdateService.UPDATE_ALL));
                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                finish();
            } else {
                Toast.makeText(LoginActivity.this, "Session expired", Toast.LENGTH_SHORT).show();
            }
        }

        setContentView(R.layout.activity_login);


        loginButton = (FloatingActionButton) findViewById(R.id.signin_submit);
        loginbuttoncompat = (com.melnykov.fab.FloatingActionButton) findViewById(R.id.signin_submit_compat);

        forgotLabel = (TextView) findViewById(R.id.signin_forgot);
        signupButton = (Button) findViewById(R.id.signin_register);

        progressBar = (ProgressBar) findViewById(R.id.loginprogressBar);

        username_edittext = (EditText) findViewById(R.id.signin_username);
        password_edittext = (EditText) findViewById(R.id.signin_password);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                username = username_edittext.getText().toString().trim();
                password = password_edittext.getText().toString().trim();

                progressBar.setVisibility(View.VISIBLE);
                loginButton.setVisibility(View.GONE);
                loginbuttoncompat.setVisibility(View.GONE);
                forgotLabel.setVisibility(View.INVISIBLE);
                signupButton.setVisibility(View.INVISIBLE);

                login(username, password);
            }
        };

//        To make fab compatible in older android versions
        if (Build.VERSION.SDK_INT >= 21) {

            loginButton.setOnClickListener(onClickListener);

        } else {

            loginButton.setVisibility(View.GONE);
            loginbuttoncompat.setVisibility(View.VISIBLE);
            loginbuttoncompat.setOnClickListener(onClickListener);

        }

        forgotLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);


            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));

            }
        });

        password_edittext.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    username = username_edittext.getText().toString().trim();
                    password = password_edittext.getText().toString().trim();
                    login(username, password);
                    return true;
                }
                return false;
            }
        });
    }

    private void login(final String username, final String password) {

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Please enter both username and password", Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
            forgotLabel.setVisibility(View.VISIBLE);
            signupButton.setVisibility(View.VISIBLE);

            if (Build.VERSION.SDK_INT >= 21) {
                loginButton.setVisibility(View.VISIBLE);
            } else
                loginbuttoncompat.setVisibility(View.VISIBLE);

        } else {
            ApiController.getLoginAPI().Login(username, "read write", password, "password", new Callback<JsonObject>() {
                @Override
                public void success(JsonObject resp, Response response) {

                    sessionManager.loginUser(password, username, resp.get("access_token").toString());
                    startService(new Intent(LoginActivity.this, UpdateService.class).putExtra(UpdateService.KEY_METHOD, UpdateService.UPDATE_ALL));
                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                    finish();

                }

                @Override
                public void failure(RetrofitError error) {
                    JsonObject jsonObject = null;
                    if (error != null) {
                        try {
                            jsonObject = (JsonObject) error.getBody();
                        } catch (Exception e) {
                            Toast.makeText(LoginActivity.this, R.string.error_no_response, Toast.LENGTH_LONG).show();
                        }
                        if (jsonObject != null) {
                            String errorDescription = jsonObject.get("error_description").toString().trim();
                            if (errorDescription.contains("Please activate your account")) {
                                Intent intent = new Intent(LoginActivity.this, AccountActivationActivity.class);
                                intent.putExtra("username", username);
                                intent.putExtra("password", password);
                                startActivity(intent);
                            } else
                                Toast.makeText(LoginActivity.this, errorDescription, Toast.LENGTH_SHORT).show();
                        } else
                            Toast.makeText(LoginActivity.this, R.string.error_network, Toast.LENGTH_SHORT).show();
                    }

                    progressBar.setVisibility(View.GONE);
                    forgotLabel.setVisibility(View.VISIBLE);
                    signupButton.setVisibility(View.VISIBLE);

                    if (Build.VERSION.SDK_INT >= 21) {
                        loginButton.setVisibility(View.VISIBLE);
                    } else
                        loginbuttoncompat.setVisibility(View.VISIBLE);

                }
            });


        }
    }

}


