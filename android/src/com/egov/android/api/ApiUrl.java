package com.egov.android.api;

import com.egov.android.library.AndroidLibrary;
import com.egov.android.library.api.IApiUrl;

public enum ApiUrl implements IApiUrl {

    LOGIN("egovernance/api/login.json"), REGISTER("egovernance/api/register.json"), VERIFY_OTP(
            "egovernance/api/verify_otp.json"), FORGOT_PASSWORD(
            "egovernance/api/forgot_password.json"), COMPLAINT_BY_TYPE(
            "egovernance/api/get_complaint_by_type.json"), COMPLAINT_TYPE(
            "egovernance/api/get_complaint_type.json"), ADD_COMPLAINT(
            "egovernance/api/add_complaint.json"), UPDATE_PROFILE(
            "egovernance/api/update_profile.json");

    private String url = "";

    ApiUrl(String url) {
        this.url = url;
    }

    @Override
    public String getUrl() {
        return this.url;
    }

    @Override
    public String getUrl(boolean prefixWithBaseUrl) {
        return AndroidLibrary.getInstance().getConfig().getString("api.baseUrl") + "/" + this.url;
    }

}
