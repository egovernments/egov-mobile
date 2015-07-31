package org.egov.android.view.activity;

import org.egov.android.controller.ServiceController;
import org.egov.android.AndroidLibrary;
import org.egov.android.view.activity.AndroidActivity;
import org.egov.android.view.activity.helper.ActivityHelper;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class BaseActivity extends AndroidActivity implements OnClickListener {

    private ActivityHelper helper = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        helper = new ActivityHelper(this);
        helper.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        helper.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        helper.onStart();
        if (!this.getClass().equals(SplashActivity.class)) {
            ServiceController.getInstance().startService(this);
        }
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        helper.setContentView(layoutResID);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        helper.onActivityResult(requestCode, resultCode, data);
    }

    public String getMessage(int id) {
        return getResources().getString(id);
    }

    public void showMessage(String message) {
        if (message != null && !message.equals("")) {
            Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 0, 120);
            toast.show();
        }
    }

    public void startLoginActivity() {
        AndroidLibrary.getInstance().getSession().edit().putString("access_token", "").commit();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    public boolean isEmpty(String data) {
        return (data == null || (data != null && data.equals("")));
    }

    @Override
    public void onClick(View v) {
        helper.onClick(v);
    }

    @Override
    public void finish() {
        
        super.finish();
        helper.finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        helper.onStop();
    }

    @Override
    public void onBackPressed() {
        if (!helper.onBackPressed()) {
            super.onBackPressed();
        }
    }
}