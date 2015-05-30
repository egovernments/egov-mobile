package com.egov.android.view.activity;

import android.os.Bundle;
import android.util.Log;

import com.egov.android.R;

public class ComplaintDetailActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint_detail);

        Bundle b = getIntent().getExtras();
        Log.d(TAG, b.getString("name"));
    }

}
