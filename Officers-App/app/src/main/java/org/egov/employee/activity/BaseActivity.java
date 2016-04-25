package org.egov.employee.activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;

import org.egov.employee.api.LoggingInterceptor;
import org.egov.employee.config.AppPreference;

import java.lang.reflect.Method;

import offices.org.egov.egovemployees.R;

/**
 * Created by egov on 15/12/15.
 */
public abstract class BaseActivity extends AppCompatActivity implements LoggingInterceptor.ErrorListener {

    AppPreference preference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResource());
        //app preference variable
        preference=new AppPreference(getApplicationContext());
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
    }

    protected abstract int getLayoutResource();

    //error message function called from error listener when receive an error response from server
    @Override
    public void showSnackBar(String msg) {
        Snackbar.make(findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG).show();
    }

    //user session timeout function handle
    @Override
    public void sessionTimeOutError() {

        //clear current access token
        preference.setApiAccessToken("");

        Intent intent = new Intent(this, SplashScreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("isFromSessionTimeOut", true);
        startActivity(intent);

    }

    //check internet connection available method with retry function parameter
    public boolean checkInternetConnectivity(final Object classObj, final String retrymethod)
    {
        ConnectivityManager connectivity = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (NetworkInfo anInfo : info)
                {
                    if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }

        Snackbar snackbar=Snackbar.make(findViewById(android.R.id.content), "No connection", Snackbar.LENGTH_LONG);

        //if retry function is not null then, this code will add retry button along with snackbar
        if(!TextUtils.isEmpty(retrymethod)) {
            snackbar.setAction("RETRY", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //call retry function
                    try {
                        Method method = classObj.getClass().getMethod(retrymethod, null);
                        method.invoke(classObj, null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        snackbar.show();

        return false;
    }

    //check internet connection available method
    public boolean checkInternetConnectivity() {
        ConnectivityManager connectivity = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (NetworkInfo anInfo : info) {
                    if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }

        Snackbar snackbar=Snackbar.make(findViewById(android.R.id.content), "No connection", Snackbar.LENGTH_LONG);
        snackbar.show();

        return false;
    }

}
