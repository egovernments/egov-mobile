package com.egov.android.view.activity;

import com.egov.android.R;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class ComplaintDetailActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint_detail);

        Bundle b = getIntent().getExtras();
        Log.d(TAG, b.getString("name"));

        ((Button) findViewById(R.id.status_summary)).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.status_summary:
                startActivity(new Intent(this, StatusSummaryActivity.class));
                break;
        }
    }

}
