package org.egov.android.view.activity;

import org.egov.android.api.ApiResponse;
import org.egov.android.api.IApiListener;
import org.egov.android.conf.Config;
import org.egov.android.listener.Event;
import org.egov.android.listener.IEventDispatcher;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public abstract class AndroidFragmentActivity extends FragmentActivity implements IApiListener,
        IEventDispatcher {

    protected final static String TAG = AndroidActivity.class.getName();

    private ActivityHelper helper = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        helper = new ActivityHelper(this);
        helper.onCreate(savedInstanceState);
    }

    public SharedPreferences getSession() {
        return helper.getSession();
    }

    @Override
    public void finish() {
        super.finish();
        helper.finish();
    }

    @Override
    public void onResponse(Event<ApiResponse> event) {
    }

    protected Config getConfig() {
        return helper.getConfig();
    }

    @Override
    public void dispatchEvent(Event<Object> event) {

    }
}
