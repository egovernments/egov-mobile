package com.egov.android.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.egov.android.R;

public class ComplaintDetailActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint_detail);

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
