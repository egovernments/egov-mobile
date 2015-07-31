package org.egov.android.api;

import org.egov.android.AndroidLibrary;
import org.egov.android.api.IApiUrl;

public enum ApiUrl implements IApiUrl {

    //@formatter:off

    LOGIN("api/oauth/token"),

    LOGOUT("api/v1.0/citizen/logout", true),

    REGISTER("api/v1.0/createCitizen"),

    VERIFY_OTP("api/v1.0/activateCitizen"),

    FORGOT_PASSWORD("api/v1.0/recoverPassword"),

    GET_PROFILE("api/v1.0/citizen/getProfile", true),

    UPDATE_PROFILE("api/v1.0/citizen/updateProfile", true),

    GET_COMPLAINT_TYPES("api/v1.0/complaint/getAllTypes", true),

    GET_FREQ_COMPLAINT_TYPES("api/v1.0/complaint/getFrequentlyFiledTypes", true),

    GET_LATEST_COMPLAINTS("api/v1.0/complaint/latest", true),

    GET_NEARBY_COMPLAINTS("api/v1.0/complaint/nearby", true),

    GET_MY_COMPLAINTS("api/v1.0/citizen/getMyComplaint", true),

    GET_COMPLAINT_STATUS("api/v1.0/complaint", true),

    COMPLAINT_CHANGE_STATUS("api/v1.0/complaint", true),

    ADD_COMPLAINT("api/v1.0/complaint/create", true),

    GET_COMPLAINT_DETAIL("api/v1.0/complaint", true),

    GET_LOCATION_BY_NAME("api/v1.0/complaint/getLocation", true),

    GET_SEARCH_COMPLAINTS("api/v1.0/complaint/search", true);

    //@formatter:off

    private String url = "";

    private boolean isSecured = false;

    private boolean useAccessToken = false;

    ApiUrl(String url) {
        this.url = url;
    }

    ApiUrl(String url, boolean useAccessToken) {
        this.url = url;
        this.useAccessToken = useAccessToken;
    }

    @Override
    public String getUrl() {
        return this.url;
    }

    @Override
    public String getUrl(boolean prefixWithBaseUrl) {
        return AndroidLibrary.getInstance().getConfig().getString("api.baseUrl") + "/" + this.url;
    }

    public void setSecured(boolean isSecured) {
        this.isSecured = isSecured;
    }

    @Override
    public boolean isSecured() {
        return isSecured;
    }

    public boolean useAccessToken() {
        return useAccessToken;
    }

    public void setUseAccessToken(boolean useAccessToken) {
        this.useAccessToken = useAccessToken;
    }
}
