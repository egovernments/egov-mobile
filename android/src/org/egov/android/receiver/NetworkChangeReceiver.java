package org.egov.android.receiver;

import org.egov.android.controller.ServiceController;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo netInfo = connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        boolean available = false;
        if (netInfo.isConnected()) {
            available = true;
        } else {
            netInfo = connectivity.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            available = netInfo.isConnected();
        }
        if (available) {
            ServiceController.getInstance().startJobs();
        } else {
            ServiceController.getInstance().stopJobs();
        }
    }

}
