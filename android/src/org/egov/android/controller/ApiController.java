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

package org.egov.android.controller;

import org.egov.android.api.ApiUrl;
import org.egov.android.AndroidLibrary;
import org.egov.android.api.ApiClient;
import org.egov.android.api.ApiMethod;
import org.egov.android.api.IApiClient;
import org.egov.android.api.IApiListener;
import org.egov.android.api.RequestMethod;
import org.egov.android.data.cache.Cache;
import org.egov.android.model.Complaint;
import org.egov.android.model.User;

import android.content.Context;

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

    public void register(IApiListener listener, User user) {
        ApiMethod apiMethod = new ApiMethod(ApiUrl.REGISTER);
        apiMethod.setMethod(RequestMethod.POST);
        apiMethod.setQueryType("json");
        apiMethod.addParameter("emailId", user.getEmail());
        apiMethod.addParameter("mobileNumber", user.getMobileNo());
        apiMethod.addParameter("name", user.getName());
        apiMethod.addParameter("password", user.getPassword());
        apiMethod.addParameter("deviceId", user.getDeviceId());
        apiMethod.addParameter("deviceType", "mobile");
        apiMethod.addParameter("OSVersion", "1.0");
        _createApiClient(apiMethod, listener, false).call();
    }

    public void login(IApiListener listener, User user) {
        ApiMethod apiMethod = new ApiMethod(ApiUrl.LOGIN);
        apiMethod.setMethod(RequestMethod.POST);
        apiMethod.addHeader("Authorization", "Basic ZWdvdi1hcGk6ZWdvdi1hcGk=");
        apiMethod.addParameter("username", user.getEmail());
        apiMethod.addParameter("password", user.getPassword());
        apiMethod.addParameter("grant_type", user.getGrantType());
        apiMethod.addParameter("scope", user.getScope());
        _createApiClient(apiMethod, listener, false).call();
    }

    public void forgotPassword(IApiListener listener, String identity) {
        ApiMethod apiMethod = new ApiMethod(ApiUrl.FORGOT_PASSWORD);
        apiMethod.setMethod(RequestMethod.POST);
        apiMethod.addParameter("identity", identity);
        _createApiClient(apiMethod, listener, false).call();
    }

    public void accountActivation(IApiListener listener, String userName, String activationCode) {
        ApiMethod apiMethod = new ApiMethod(ApiUrl.VERIFY_OTP);
        apiMethod.setMethod(RequestMethod.POST);
        apiMethod.addParameter("userName", userName);
        apiMethod.addParameter("activationCode", activationCode);
        _createApiClient(apiMethod, listener, false).call();
    }

    public void logout(IApiListener listener) {
        ApiMethod apiMethod = new ApiMethod(ApiUrl.LOGOUT);
        apiMethod.setMethod(RequestMethod.POST);
        _createApiClient(apiMethod, listener, false).call();
    }

    public void getFreqComplaintTypes(IApiListener listener) {
        ApiMethod apiMethod = new ApiMethod(ApiUrl.GET_FREQ_COMPLAINT_TYPES);
        _createApiClient(apiMethod, listener, false).call();
    }

    public void getComplaintTypes(IApiListener listener) {
        ApiMethod apiMethod = new ApiMethod(ApiUrl.GET_COMPLAINT_TYPES);
        _createApiClient(apiMethod, listener, true).call();
    }

    public void getUserComplaints(IApiListener listener, int page) {
        ApiMethod apiMethod = new ApiMethod(ApiUrl.GET_MY_COMPLAINTS);
        apiMethod.setExtraParam(String.valueOf(page) + "/" + String.valueOf(5));
        _createApiClient(apiMethod, listener, false).call();
    }

    public void getLatestComplaints(IApiListener listener, int page) {
        ApiMethod apiMethod = new ApiMethod(ApiUrl.GET_LATEST_COMPLAINTS);
        apiMethod.setExtraParam(String.valueOf(page) + "/" + String.valueOf(5));
        _createApiClient(apiMethod, listener, false).call();
    }

    public void getNearByComplaints(IApiListener listener,
                                    int page,
                                    double lat,
                                    double lng,
                                    int distance) {
        ApiMethod apiMethod = new ApiMethod(ApiUrl.GET_NEARBY_COMPLAINTS);
        apiMethod.setExtraParam(String.valueOf(page) + "/" + String.valueOf(5));
        apiMethod.addParameter("lat", lat);
        apiMethod.addParameter("lng", lng);
        apiMethod.addParameter("distance", distance);
        _createApiClient(apiMethod, listener, false).call();
    }

    public void getComplaintDetail(IApiListener listener, String id) {
        ApiMethod apiMethod = new ApiMethod(ApiUrl.GET_COMPLAINT_DETAIL);
        apiMethod.setExtraParam(id + "/detail");
        _createApiClient(apiMethod, listener, false).call();
    }

    public void getComplaintStatus(IApiListener listener, String id) {
        ApiMethod apiMethod = new ApiMethod(ApiUrl.GET_COMPLAINT_STATUS);
        apiMethod.setExtraParam(id + "/status");
        _createApiClient(apiMethod, listener, false).call();
    }

    public void complaintChangeStatus(IApiListener listener,
                                      String id,
                                      String status,
                                      String comment) {
        ApiMethod apiMethod = new ApiMethod(ApiUrl.COMPLAINT_CHANGE_STATUS);
        apiMethod.setMethod(RequestMethod.PUT);
        apiMethod.setQueryType("json");
        apiMethod.setExtraParam(id + "/updateStatus");
        apiMethod.addParameter("action", status);
        apiMethod.addParameter("comment", comment);
        _createApiClient(apiMethod, listener, false).call();
    }

    public void getLocationByName(IApiListener listener, String locationName) {
        ApiMethod apiMethod = new ApiMethod(ApiUrl.GET_LOCATION_BY_NAME);
        apiMethod.setMethod(RequestMethod.GET);
        apiMethod.addParameter("locationName", locationName);
        _createApiClient(apiMethod, listener, false).call();
    }

    public void addComplaint(IApiListener listener, Complaint complaint) {
        ApiMethod apiMethod = new ApiMethod(ApiUrl.ADD_COMPLAINT);
        apiMethod.setMethod(RequestMethod.POST);
        apiMethod.setQueryType("json");
        if (complaint.getLatitude() == 0 && complaint.getLongitute() == 0) {
            apiMethod.addParameter("locationId", complaint.getLocationId());
        } else {
            apiMethod.addParameter("lat", complaint.getLatitude());
            apiMethod.addParameter("lng", complaint.getLongitute());
        }
        apiMethod.addParameter("details", complaint.getDetails());
        apiMethod.addParameter("complaintTypeId", complaint.getComplaintTypeId());
        apiMethod.addParameter("landmarkDetails", complaint.getLandmarkDetails());
        _createApiClient(apiMethod, listener, false).call();
    }

    public void getSearchComplaints(IApiListener listener, String searchText) {
        ApiMethod apiMethod = new ApiMethod(ApiUrl.GET_SEARCH_COMPLAINTS);
        apiMethod.setMethod(RequestMethod.POST);
        apiMethod.setQueryType("json");
        apiMethod.addParameter("searchText", searchText);
        _createApiClient(apiMethod, listener, false).call();
    }

    public void getProfile(IApiListener listener) {
        ApiMethod apiMethod = new ApiMethod(ApiUrl.GET_PROFILE);
        _createApiClient(apiMethod, listener, false).call();
    }

    public void updateProfile(IApiListener listener, User user) {
        ApiMethod apiMethod = new ApiMethod(ApiUrl.UPDATE_PROFILE);
        apiMethod.setMethod(RequestMethod.PUT);
        apiMethod.setQueryType("json");
        apiMethod.addParameter("userName", user.getMobileNo());
        apiMethod.addParameter("name", user.getName());
        apiMethod.addParameter("gender", user.getGender());
        apiMethod.addParameter("mobileNumber", user.getMobileNo());
        apiMethod.addParameter("emailId", user.getEmail());
        apiMethod.addParameter("altContactNumber", user.getAltContactNumber());
        apiMethod.addParameter("dob", user.getDateOfBirth());
        apiMethod.addParameter("pan", user.getPanCardNumber());
        apiMethod.addParameter("aadhaarNumber", user.getAadhaarCardNumber());
        apiMethod.addParameter("locale", user.getLanguage());
        _createApiClient(apiMethod, listener, false).call();
    }

    private IApiClient _createApiClient(ApiMethod apiMethod, IApiListener listener, boolean useCache) {

        IApiClient client = null;

        Cache cache = null;
        if (useCache) {
            cache = new Cache();
            cache.setDuration(AndroidLibrary.getInstance().getConfig().getCacheDuration());
        }
        client = new ApiClient(apiMethod);
        client.setCache(cache);
        client.addListener(listener);
        client.setContext(this.context);
        return client;
    }

}
