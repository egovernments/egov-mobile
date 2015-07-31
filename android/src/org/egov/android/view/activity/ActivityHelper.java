package org.egov.android.view.activity;

import org.egov.android.AndroidLibrary;
import org.egov.android.conf.Config;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

public class ActivityHelper {

    private SharedPreferences session = null;
    private Config config = null;

    private Activity activity = null;

    public ActivityHelper(Activity activity) {
        this.activity = activity;
    }

    public void onCreate(Bundle savedInstanceState) {
        this.config = AndroidLibrary.getInstance().getConfig();
        this.session = this.activity.getSharedPreferences(this.config.getString("app.name"),
                Context.MODE_PRIVATE);

    }

    public SharedPreferences getSession() {
        return this.session;
    }

    public void finish() {

    }

    public Config getConfig() {
        return this.config;
    }
}
