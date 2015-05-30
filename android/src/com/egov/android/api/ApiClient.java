package com.egov.android.api;

import com.egov.android.library.api.ApiClientBase;
import com.egov.android.library.api.ApiMethod;
import com.egov.android.library.api.IApiListener;
import com.egov.android.library.listener.Event;

public class ApiClient extends ApiClientBase implements IApiListener {

    public ApiClient(ApiMethod apiMethod) {
        super(apiMethod);
    }

    @Override
    protected void onPreExecute() {
        this.addListener(this);
        super.onPreExecute();
    }

    public void onResponse(@SuppressWarnings("rawtypes") Event event) {
        if (event.isStopPropogation()) {
            return;
        }
    }

}
