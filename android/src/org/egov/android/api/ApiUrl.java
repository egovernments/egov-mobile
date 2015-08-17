/**
 * eGov suite of products aim to improve the internal efficiency,transparency, accountability and the service delivery of the
 * government organizations.
 * 
 * Copyright (C) <2015> eGovernments Foundation
 * 
 * The updated version of eGov suite of products as by eGovernments Foundation is available at http://www.egovernments.org
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * http://www.gnu.org/licenses/ or http://www.gnu.org/licenses/gpl.html .
 * 
 * In addition to the terms of the GPL license to be adhered to in using this program, the following additional terms are to be
 * complied with:
 * 
 * 1) All versions of this program, verbatim or modified must carry this Legal Notice.
 * 
 * 2) Any misrepresentation of the origin of the material is prohibited. It is required that all modified versions of this
 * material be marked in reasonable ways as different from the original version.
 * 
 * 3) This license does not grant any rights to any user of the program with regards to rights under trademark law for use of the
 * trade names or trademarks of eGovernments Foundation.
 * 
 * In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */

package org.egov.android.api;

import org.egov.android.AndroidLibrary;
import org.egov.android.api.IApiUrl;

public enum ApiUrl implements IApiUrl {

    /**
     * To set api url. To check whether we have to pass access token to the url. If it is true the
     * access token will be passed to the url. If it is false access token will not be passed to the
     * url.
     */

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

    GET_SEARCH_COMPLAINTS("api/v1.0/complaint/search", true),

    RESEND_OTP("api/v1.0/sendOTP");

    //@formatter:off

    private String url = "";

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

    public boolean useAccessToken() {
        return useAccessToken;
    }

    public void setUseAccessToken(boolean useAccessToken) {
        this.useAccessToken = useAccessToken;
    }
}
