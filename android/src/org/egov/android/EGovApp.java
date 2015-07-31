package org.egov.android;

import org.egov.android.AndroidApp;

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
