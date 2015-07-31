package org.egov.android;

import org.egov.android.conf.Config;

import android.content.SharedPreferences;

public class AndroidLibrary {

    private static AndroidLibrary _instance = null;

    private Config config = null;
    private SharedPreferences session = null;

    public AndroidLibrary() {

    }

    public static AndroidLibrary getInstance() {
        if (_instance == null) {
            _instance = new AndroidLibrary();
        }
        return _instance;
    }

    public Config getConfig() {
        return this.config;
    }

    public SharedPreferences getSession() {
        return this.session;
    }
}
