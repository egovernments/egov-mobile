package org.egov.android.api;

import org.egov.android.data.cache.Cache;

import android.content.Context;

public interface IApiClient {

    public void call();

    public ApiMethod getApiMethod();

    public ApiClient setApiMethod(ApiMethod apiMethod);

    public Context getContext();

    public ApiClient setContext(Context context);

    public boolean isShowSpinner();

    public ApiClient setShowSpinner(boolean showSpinner);

    public ApiClient addListener(IApiListener listener);

    public Cache getCache();

    public ApiClient setCache(Cache cache);

}