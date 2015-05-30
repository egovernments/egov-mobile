package com.egov.android.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.egov.android.view.activity.helper.ActivityHelper;
import com.egov.android.library.view.activity.AndroidActivity;

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