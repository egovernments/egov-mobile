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

import org.egov.android.api.ApiClient;
import org.egov.android.api.ApiMethod;
import org.egov.android.api.ApiUrl;
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

    /**
     * This class is used to implement api calls. To avoid unnecessary object creation, we have used
     * the getInstance() function.
     * 
     * @return
     */
    public static ApiController getInstance() {
        if (_instance == null) {
            _instance = new ApiController();
        }
        return _instance;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    /**
     * This function is used for citizen register. Here we are passing citizen information by user
     * object. Set query type to check whether to post the parameters through the https url or
     * postData. Add parameters to apiMethod using addParameter function.
     * 
     * @param listener
     * @param user
     */
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

    /**
     * This function is used for citizen login. Here we are passing citizen information by user
     * object. We have added 'Authorization' in header for security.
     * 
     * @param listener
     * @param user
     */
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

    /**
     * This function is used for reset password. identity value is the user's mobile number.
     * 
     * @param listener
     * @param identity
     */
    public void forgotPassword(IApiListener listener, String identity) {
        ApiMethod apiMethod = new ApiMethod(ApiUrl.FORGOT_PASSWORD);
        apiMethod.setMethod(RequestMethod.POST);
        apiMethod.addParameter("identity", identity);
        _createApiClient(apiMethod, listener, false).call();
    }

    /**
     * This function is used for activate an account. After completion of registration, We have sent
     * an OTP to the user's mobile/email. Users get the OTP and activate their account by send the
     * OTP to backend.
     * 
     * @param listener
     * @param userName
     * @param activationCode
     */
    public void accountActivation(IApiListener listener, String userName, String activationCode) {
        ApiMethod apiMethod = new ApiMethod(ApiUrl.VERIFY_OTP);
        apiMethod.setMethod(RequestMethod.POST);
        apiMethod.addParameter("userName", userName);
        apiMethod.addParameter("activationCode", activationCode);
        _createApiClient(apiMethod, listener, false).call();
    }

    /**
     * This function is used for logout. When the user logs out from the app, this function is
     * called.
     * 
     * @param listener
     */
    public void logout(IApiListener listener) {
        ApiMethod apiMethod = new ApiMethod(ApiUrl.LOGOUT);
        apiMethod.setMethod(RequestMethod.POST);
        _createApiClient(apiMethod, listener, false).call();
    }

    /**
     * This function is used for get frequent complaint types to show it in list.
     * 
     * @param listener
     */
    public void getFreqComplaintTypes(IApiListener listener) {
        ApiMethod apiMethod = new ApiMethod(ApiUrl.GET_FREQ_COMPLAINT_TYPES);
        _createApiClient(apiMethod, listener, false).call();
    }

    /**
     * This function is used for get all complaint types to show it in list.
     * 
     * @param listener
     */
    public void getComplaintTypes(IApiListener listener) {
        ApiMethod apiMethod = new ApiMethod(ApiUrl.GET_COMPLAINT_TYPES);
        _createApiClient(apiMethod, listener, true).call();
    }

    /**
     * This function is used to get complaints created by the logged in user. We set item count for
     * each page as 5. By using access_token, we get the user complaints list.
     * 
     * @param listener
     * @param page
     */
    public void getUserComplaints(IApiListener listener, int page) {
        ApiMethod apiMethod = new ApiMethod(ApiUrl.GET_MY_COMPLAINTS);
        apiMethod.setExtraParam(String.valueOf(page) + "/" + String.valueOf(5));
        _createApiClient(apiMethod, listener, false).call();
    }

    /**
     * This function is used to get latest complaints. We set item count for each page as 5.
     * 
     * @param listener
     * @param page
     */
    public void getLatestComplaints(IApiListener listener, int page) {
        ApiMethod apiMethod = new ApiMethod(ApiUrl.GET_LATEST_COMPLAINTS);
        apiMethod.setExtraParam(String.valueOf(page) + "/" + String.valueOf(5));
        _createApiClient(apiMethod, listener, false).call();
    }

    /**
     * This function is used to get nearby complaints. Before calling this api, we have to get the
     * current latitude and longitude.The response will be the complaints around 5000 kms of the
     * current geo location. We set item count for each page as 5.
     * 
     * @param listener
     * @param page
     * @param lat
     * @param lng
     * @param distance
     */
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

    /**
     * This function is used to get complaint's detail. To get the complaint detail, we have sent
     * the complaint id.
     * 
     * @param listener
     * @param id
     *            => complaint id
     */
    public void getComplaintDetail(IApiListener listener, String id) {
        ApiMethod apiMethod = new ApiMethod(ApiUrl.GET_COMPLAINT_DETAIL);
        apiMethod.setExtraParam(id + "/detail");
        _createApiClient(apiMethod, listener, false).call();
    }

    /**
     * This function is used to get complaint status. To get complaint status we have to pass the
     * complaint id.
     * 
     * @param listener
     * @param id
     *            => complaint id
     */
    public void getComplaintStatus(IApiListener listener, String id) {
        ApiMethod apiMethod = new ApiMethod(ApiUrl.GET_COMPLAINT_STATUS);
        apiMethod.setExtraParam(id + "/status");
        _createApiClient(apiMethod, listener, false).call();
    }

    /**
     * This function is used to change the complaint status. We can withdrawn the complaint by
     * calling this api.
     * 
     * @param listener
     * @param id
     *            => complaint id
     * @param status
     * @param comment
     */
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

    /**
     * This function is used to get location name. When user create complaints, the location must be
     * entered. After entering three letters in the location field, we call this api to show the
     * location names by sending the three letters.
     * 
     * @param listener
     * @param locationName
     */
    public void getLocationByName(IApiListener listener, String locationName) {
        ApiMethod apiMethod = new ApiMethod(ApiUrl.GET_LOCATION_BY_NAME);
        apiMethod.setMethod(RequestMethod.GET);
        apiMethod.addParameter("locationName", locationName);
        _createApiClient(apiMethod, listener, false).call();
    }

    /**
     * This function is used to create a complaint. If the complaint object has lat and lng then
     * create complaint using them. Otherwise create complaint using location id.
     * 
     * @param listener
     * @param complaint
     */
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

    /**
     * This function is used to search complaints by search text. Search text will be send through
     * postdata so we set query type as 'json'.
     * 
     * @param listener
     * @param searchText
     */
    public void getSearchComplaints(IApiListener listener, String searchText) {
        ApiMethod apiMethod = new ApiMethod(ApiUrl.GET_SEARCH_COMPLAINTS);
        apiMethod.setMethod(RequestMethod.POST);
        apiMethod.setQueryType("json");
        apiMethod.addParameter("searchText", searchText);
        _createApiClient(apiMethod, listener, false).call();
    }

    /**
     * This function is used to get the user information by access_token.
     * 
     * @param listener
     */
    public void getProfile(IApiListener listener) {
        ApiMethod apiMethod = new ApiMethod(ApiUrl.GET_PROFILE);
        _createApiClient(apiMethod, listener, false).call();
    }

    /**
     * This function is used to update the user information. User information will be send to the
     * backend by postdata so we set query type as'json'.
     * 
     * @param listener
     * @param user
     */
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

    /**
     * This function is used to create an apiClient object for each api calls. useCache flag is to
     * denote whether the api call has cache. If the useCache flag is true, save the data in cache.
     * 
     * @param apiMethod
     * @param listener
     * @param useCache
     * @return
     */
    private IApiClient _createApiClient(ApiMethod apiMethod, IApiListener listener, boolean useCache) {

        IApiClient client = null;
        client = new ApiClient(apiMethod);
        if(useCache){
            Cache cache = new Cache();
            cache.setUrl(apiMethod.getFullUrl());
            client.setCache(cache);
        }
        client.addListener(listener);
        client.setContext(this.context);
        return client;
    }

}
