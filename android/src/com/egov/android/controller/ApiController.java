package com.egov.android.controller;

import android.content.Context;

import com.egov.android.api.ApiClient;
import com.egov.android.api.ApiUrl;
import com.egov.android.library.AndroidLibrary;
import com.egov.android.library.api.ApiMethod;
import com.egov.android.library.api.ApiMockup;
import com.egov.android.library.api.IApiClient;
import com.egov.android.library.api.IApiListener;
import com.egov.android.library.data.cache.Cache;

public class ApiController {

    private static ApiController _instance = null;

    private Context context = null;

    public ApiController() {
    }

    public static ApiController getInstance() {
        if (_instance == null) {
            _instance = new ApiController();
        }
        return _instance;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void login(IApiListener listener) {
        ApiMethod apiMethod = new ApiMethod(ApiUrl.LOGIN);
        _createApiClient(apiMethod, listener, false).call();
    }

    public void forgotPassword(IApiListener listener) {
        ApiMethod apiMethod = new ApiMethod(ApiUrl.FORGOT_PASSWORD);
        _createApiClient(apiMethod, listener, false).call();
    }

    public void register(IApiListener listener) {
        ApiMethod apiMethod = new ApiMethod(ApiUrl.REGISTER);
        _createApiClient(apiMethod, listener, false).call();
    }

    public void verifyOTP(IApiListener listener) {
        ApiMethod apiMethod = new ApiMethod(ApiUrl.VERIFY_OTP);
        _createApiClient(apiMethod, listener, false).call();
    }

    public void getComplaintByType(IApiListener listener) {
        ApiMethod apiMethod = new ApiMethod(ApiUrl.COMPLAINT_BY_TYPE);
        _createApiClient(apiMethod, listener, false).call();
    }

    public void getComplaintType(IApiListener listener) {
        ApiMethod apiMethod = new ApiMethod(ApiUrl.COMPLAINT_TYPE);
        _createApiClient(apiMethod, listener, false).call();
    }

    public void addComplaint(IApiListener listener) {
        ApiMethod apiMethod = new ApiMethod(ApiUrl.ADD_COMPLAINT);
        _createApiClient(apiMethod, listener, false).call();
    }

    public void updateProfile(IApiListener listener) {
        ApiMethod apiMethod = new ApiMethod(ApiUrl.UPDATE_PROFILE);
        _createApiClient(apiMethod, listener, false).call();
    }

    private IApiClient _createApiClient(ApiMethod apiMethod, IApiListener listener, boolean useCache) {

        IApiClient client = null;

        if (AndroidLibrary.getInstance().getConfig().getString("api.useMockup").equals("true")) {
            client = new ApiMockup(apiMethod);
        } else {
            Cache cache = null;
            if (useCache) {
                cache = new Cache();
                cache.setDuration(AndroidLibrary.getInstance().getConfig().getCacheDuration());
            }
            client = new ApiClient(apiMethod);
            client.setCache(cache);
        }
        client.addListener(listener);
        client.setContext(this.context);
        return client;
    }

}
