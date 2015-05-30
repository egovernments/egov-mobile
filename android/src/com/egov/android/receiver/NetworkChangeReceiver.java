package com.egov.android.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d("SYNC", intent.getAction());
        //        NetworkInfo info = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
        //
        //        String mTypeName = info.getTypeName();
        //        String mSubtypeName = info.getSubtypeName();
        //        boolean mAvailable = info.isAvailable();
        //
        //        Log.i("SYNC", "Network Type: " + mTypeName + ", subtype: " + mSubtypeName + ", available: "
        //                + mAvailable);

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
            
            Log.d("SYNC", "available");
        } else {
            Log.d("SYNC", "Flag No 2 =====inside else=====>> not");
            
        }
    }

}
