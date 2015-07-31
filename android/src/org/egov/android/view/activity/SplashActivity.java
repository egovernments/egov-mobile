package org.egov.android.view.activity;

import org.egov.android.R;
import org.egov.android.data.SQLiteHelper;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends BaseActivity implements Runnable {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        SQLiteHelper
                .getInstance()
                .execSQL(
                        "CREATE TABLE IF NOT EXISTS jobs (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, data TEXT, status TEXT, type TEXT, triedCount INTEGER, timeStamp DATETIME DEFAULT (datetime('now','localtime')), UNIQUE(data) ON CONFLICT REPLACE)");
        new Handler().postDelayed(this, 2000);
    }

    @Override
    public void run() {
        String access_token = getSession().getString("access_token", "");
        if (!access_token.equalsIgnoreCase("")) {
            startActivity(new Intent(this, ComplaintActivity.class));
        } else {
            startActivity(new Intent(this, LoginActivity.class));
        }
        finish();
    }
}
