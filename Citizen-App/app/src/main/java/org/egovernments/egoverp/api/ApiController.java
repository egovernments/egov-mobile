/*
 * ******************************************************************************
 *  eGov suite of products aim to improve the internal efficiency,transparency,
 *      accountability and the service delivery of the government  organizations.
 *
 *        Copyright (C) <2016>  eGovernments Foundation
 *
 *        The updated version of eGov suite of products as by eGovernments Foundation
 *        is available at http://www.egovernments.org
 *
 *        This program is free software: you can redistribute it and/or modify
 *        it under the terms of the GNU General Public License as published by
 *        the Free Software Foundation, either version 3 of the License, or
 *        any later version.
 *
 *        This program is distributed in the hope that it will be useful,
 *        but WITHOUT ANY WARRANTY; without even the implied warranty of
 *        MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *        GNU General Public License for more details.
 *
 *        You should have received a copy of the GNU General Public License
 *        along with this program. If not, see http://www.gnu.org/licenses/ or
 *        http://www.gnu.org/licenses/gpl.html .
 *
 *        In addition to the terms of the GPL license to be adhered to in using this
 *        program, the following additional terms are to be complied with:
 *
 *    	1) All versions of this program, verbatim or modified must carry this
 *    	   Legal Notice.
 *
 *    	2) Any misrepresentation of the origin of the material is prohibited. It
 *    	   is required that all modified versions of this material be marked in
 *    	   reasonable ways as different from the original version.
 *
 *    	3) This license does not grant any rights to any user of the program
 *    	   with regards to rights under trademark law for use of the trade names
 *    	   or trademarks of eGovernments Foundation.
 *
 *      In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 *  *****************************************************************************
 */

package org.egovernments.egoverp.api;


import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.egovernments.egoverp.config.SessionManager;
import org.egovernments.egoverp.models.BuildingPlanAPIResponse;
import org.egovernments.egoverp.models.City;
import org.egovernments.egoverp.models.District;
import org.egovernments.egoverp.models.GrievanceAPIResponse;
import org.egovernments.egoverp.models.GrievanceCommentAPIResponse;
import org.egovernments.egoverp.models.GrievanceLocationAPIResponse;
import org.egovernments.egoverp.models.GrievanceTypeAPIResponse;
import org.egovernments.egoverp.models.GrievanceUpdate;
import org.egovernments.egoverp.models.Profile;
import org.egovernments.egoverp.models.ProfileAPIResponse;
import org.egovernments.egoverp.models.PropertySearchRequest;
import org.egovernments.egoverp.models.PropertyTaxCallback;
import org.egovernments.egoverp.models.PropertyViewRequest;
import org.egovernments.egoverp.models.RegisterRequest;
import org.egovernments.egoverp.models.WaterConnectionSearchRequest;
import org.egovernments.egoverp.models.WaterTaxCallback;
import org.egovernments.egoverp.models.WaterTaxRequest;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.mime.MultipartTypedOutput;

public class ApiController {

    public static APIInterface apiInterface = null;

    private final static OkHttpClient client = SSLTrustManager.createClient();

    public static City getCityURL(String url) throws IOException{
        try {
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            Response response = client.newCall(request).execute();
            return new Gson().fromJson(response.body().charStream(), City.class);
        } catch (Exception e) {
            return null;
        }
    }

    public static String getResponseFromUrl(String url)throws IOException{
        try {
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (Exception e) {
            return null;
        }
    }

    public static List<District> getAllCitiesURLs(String url) throws IOException {

        try {
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            Response response = client.newCall(request).execute();
            if (!response.isSuccessful())
                throw new IOException();
            Type type = new TypeToken<List<District>>() {
            }.getType();
            return new Gson().fromJson(response.body().charStream(), type);
        } catch (Exception e) {
            return null;
        }

    }

    public static City getCityURL(String url, int cityCode) throws IOException {

        try {

            Request request = new Request.Builder()
                    .url(url + "&code=" + cityCode)
                    .build();

            Response response = client.newCall(request).execute();
            if (!response.isSuccessful())
                throw new IOException();
            return new Gson().fromJson(response.body().charStream(), City.class);
        } catch (Exception e) {
            return null;
        }

    }

    //Sets up the API client
    public static APIInterface getAPI(Context context) {
        SessionManager sessionManager = new SessionManager(context);
        if (apiInterface == null) {
            RestAdapter restAdapter = new RestAdapter
                    .Builder()
                    .setClient(new OkClient(client))
                    .setEndpoint(sessionManager.getBaseURL())
                    .setErrorHandler(new CustomErrorHandler())
                    .build();
            //logger enable or disable based on app config
            restAdapter.setLogLevel((sessionManager.getKeyDebugLog()?RestAdapter.LogLevel.FULL:RestAdapter.LogLevel.NONE));
            apiInterface = restAdapter.create(APIInterface.class);
        }
        return apiInterface;
    }

    public static APIInterface resetAndGetAPI(Context context) {
        SessionManager sessionManager = new SessionManager(context);
        apiInterface=null;
        RestAdapter restAdapter = new RestAdapter
                    .Builder()
                    .setClient(new OkClient(client))
                    .setEndpoint(sessionManager.getBaseURL())
                    .setErrorHandler(new CustomErrorHandler())
                    .build();
        //logger enable or disable based on app config
        restAdapter.setLogLevel((sessionManager.getKeyDebugLog()?RestAdapter.LogLevel.FULL:RestAdapter.LogLevel.NONE));
        apiInterface = restAdapter.create(APIInterface.class);
        return apiInterface;
    }

    //For Third Party Servers
    public static APIInterface getCustomAPI(Context context, String url) {
        SessionManager sessionManager = new SessionManager(context);
        APIInterface apiInterface=null;
        RestAdapter restAdapter = new RestAdapter
                .Builder()
                .setClient(new OkClient(client))
                .setEndpoint(url)
                .setErrorHandler(new CustomErrorHandler())
                .build();
        //logger enable or disable based on app config
        restAdapter.setLogLevel((sessionManager.getKeyDebugLog()?RestAdapter.LogLevel.FULL:RestAdapter.LogLevel.NONE));
        apiInterface = restAdapter.create(APIInterface.class);
        return apiInterface;
    }

    public interface APIInterface {

        @POST(ApiUrl.CITIZEN_REGISTER)
        void registerUser(@Body RegisterRequest registerRequest,
                          Callback<JsonObject> jsonObjectCallback);

        @FormUrlEncoded
        @POST(ApiUrl.CITIZEN_LOGIN)
        void login(@Header("Authorization") String authorization, @Field("username") String username,
                   @Field("scope") String scope,
                   @Field("password") String password,
                   @Field("grant_type") String grant_type,
                   Callback<JsonObject> jsonObjectCallback);

        @FormUrlEncoded
        @POST(ApiUrl.CITIZEN_LOGOUT)
        void logout(@Field("access_token") String access_token,
                    Callback<JsonObject> jsonObjectCallback);

        @GET(ApiUrl.COMPLAINTS_COUNT_DETAILS)
        void getComplaintCountDetails(@Query(value = "access_token", encodeValue = false) String access_token,
                           Callback<JsonObject> grievanceAPIJsonCallback);


        @GET(ApiUrl.CITIZEN_GET_COMPLAINT_CATEGORIES_COUNT)
        void getComplaintCategoriesWithCount(@Query("access_token") String authorization, Callback<JsonObject> complaintCategoriesCallback);

        @GET(ApiUrl.CITIZEN_GET_MY_COMPLAINT)
        void getMyComplaints(@Path(value = "page", encode = false) String pages,
                             @Path(value = "pageSize", encode = false) String pagesize,
                             @Query(value = "access_token", encodeValue = false) String access_token,
                             @Query(value = "complaintStatus", encodeValue = false) String complaintStatus,
                             Callback<GrievanceAPIResponse> complaintAPIResponseCallback);

        @GET(ApiUrl.COMPLAINT_LATEST)
        void getLatestComplaints(@Path(value = "page", encode = false) String pages,
                                 @Path(value = "pageSize", encode = false) String pagesize,
                                 @Query(value = "access_token", encodeValue = false) String access_token,
                                 Callback<GrievanceAPIResponse> complaintAPIResponseCallback);

        @GET(ApiUrl.COMPLAINT_CATEGORIES_TYPES)
        void getComplaintTypes(@Query(value = "access_token", encodeValue = false) String access_token,
                               Callback<GrievanceTypeAPIResponse> grievanceTypeAPIResponseCallback);

        @GET(ApiUrl.COMPLAINT_HISTORY)
        void getComplaintHistory(@Path(value = "complaintNo", encode = false) String complaintNo,
                                 @Query(value = "access_token", encodeValue = false) String access_token,
                                 Callback<GrievanceCommentAPIResponse> grievanceCommentAPIResponseCallback);

        @GET(ApiUrl.COMPLAINT_GET_LOCATION_BY_NAME)
        void getComplaintLocation(@Query(value = "locationName", encodeValue = false) String location,
                                  @Query(value = "access_token", encodeValue = false) String access_token,
                                  Callback<GrievanceLocationAPIResponse> grievanceLocationAPIResponseCallback);

        @POST(ApiUrl.COMPLAINT_CREATE)
        void createComplaint(@Body MultipartTypedOutput output,
                             @Query(value = "access_token", encodeValue = false) String access_token,
                             Callback<JsonObject> jsonObjectCallback);

        @PUT(ApiUrl.COMPLAINT_UPDATE_STATUS)
        void updateGrievance(@Path(value = "complaintNo", encode = false) String complaintNo,
                             @Body GrievanceUpdate grievanceUpdate,
                             @Query(value = "access_token", encodeValue = false) String access_token,
                             Callback<JsonObject> jsonObjectCallback);

        @POST(ApiUrl.SEARCH_PROPERTY)
        void searchProperty(@Header("Referer") String referer,
                            @Body PropertySearchRequest propertySearchRequest,
                            Callback<List<PropertyTaxCallback>> taxCallback);

        @POST(ApiUrl.PROPERTY_TAX_DETAILS)
        void getPropertyTax(@Header("Referer") String referer,
                            @Body PropertyViewRequest propertyViewRequest,
                            Callback<PropertyTaxCallback> taxCallback);

        @POST(ApiUrl.SEARCH_WATER_CONNECTION)
        void searchWaterConnection(@Header("Referer") String referer,
                            @Body WaterConnectionSearchRequest waterConnectionSearchRequest,
                            Callback<List<WaterTaxCallback>> taxCallback);

        @POST(ApiUrl.WATER_TAX_DETAILS)
        void getWaterTax(@Header("Referer") String referer,
                         @Body WaterTaxRequest waterTaxRequest,
                         Callback<WaterTaxCallback> taxCallback);

        @GET(ApiUrl.BPA_DETAILS)
        void getBuildingPlanApprovalDetails(@Path(value = "applicationNo", encode = false) String applicationNo, @Path(value = "authKey", encode = false) String authKey, Callback<BuildingPlanAPIResponse> bpaDetails);

        @FormUrlEncoded
        @POST(ApiUrl.CITIZEN_ACTIVATE)
        void activate(@Field("userName") String username,
                      @Field("activationCode") String activationCode,
                      Callback<JsonObject> jsonObjectCallback);

        @FormUrlEncoded
        @POST(ApiUrl.CITIZEN_SEND_OTP)
        void sendOTP(@Field("identity") String identity,
                     Callback<JsonObject> jsonObjectCallback);

        @FormUrlEncoded
        @POST(ApiUrl.CITIZEN_PASSWORD_RECOVER)
        void recoverPassword(@Field("identity") String identity,
                             @Field("redirectURL") String redirectURL,
                             Callback<JsonObject> jsonObjectCallback);

        @FormUrlEncoded
        @POST(ApiUrl.CITIZEN_PASSWORD_RECOVER)
        void resetPassword(@Field("identity") String identity,
                             @Field("token") String token,
                             @Field("newPassword") String newPassword,
                             @Field("confirmPassword") String confirmPassword,
                             Callback<JsonObject> jsonObjectCallback);

        @GET(ApiUrl.CITIZEN_GET_PROFILE)
        void getProfile(@Query(value = "access_token", encodeValue = false) String access_token,
                        Callback<ProfileAPIResponse> profileAPIResponseCallback);

        @PUT(ApiUrl.CITIZEN_UPDATE_PROFILE)
        void updateProfile(@Body Profile profile,
                           @Query(value = "access_token", encodeValue = false) String access_token,
                           Callback<ProfileAPIResponse> profileAPIResponseCallback);


    }


}

