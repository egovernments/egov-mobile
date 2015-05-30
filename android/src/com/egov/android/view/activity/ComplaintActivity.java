package com.egov.android.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.egov.android.R;
import com.egov.android.view.activity.BaseActivity;

public class ComplaintActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint);

        ((ImageView) findViewById(R.id.location)).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {

            case R.id.location:
                startActivity(new Intent(this, MapActivity.class));
                break;
        }
    }
}
