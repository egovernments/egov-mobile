package com.egov.android;

import com.egov.android.library.AndroidApp;

public class EGovApp extends AndroidApp {

    @Override
    public void configure(String configFile) {
        super.configure("egov.conf");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
